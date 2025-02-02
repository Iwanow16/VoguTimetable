package ru.test.data.repository

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ru.test.data.network.mappers.update.ReleaseMapper
import ru.test.data.network.services.GitHubApiService
import ru.test.domain.models.update.Release
import ru.test.domain.repository.UpdateRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UpdateRepositoryImpl @Inject constructor(
    private val api: GitHubApiService,
    private val mapper: ReleaseMapper,
    private val context: Context,
    @Named("repoOwner") private val repoOwner: String,
    @Named("repoName") private val repoName: String
) : UpdateRepository {

    override suspend fun getLatestRelease(): Result<Release> = runCatching {
        val response = api.getLatestRelease(repoOwner, repoName)
        mapper.mapToDomain(response)
    }

    override suspend fun getCurrentVersion(): String {
        return context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName.toString()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun downloadUpdate(url: String): Result<String> = runCatching {
        suspendCoroutine { continuation ->

            val resolver = context.contentResolver
            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            val fileName = "app-update.apk"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName) // Исправлено
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // Добавлено
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val fileUri = resolver.insert(collection, contentValues)

            if (fileUri == null) {
                continuation.resumeWith(Result.failure(Exception("Ошибка: не удалось создать URI файла")))
                return@suspendCoroutine
            }

            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle("Загрузка обновления")
                setDescription("Загружается новая версия приложения")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationUri(fileUri)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (downloadId == id) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(fileUri, contentValues, null, null)

                        context?.unregisterReceiver(this)
                        continuation.resume(fileUri.toString())
                    }
                }
            }

            ContextCompat.registerReceiver(
                context,
                onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    override suspend fun installUpdate(uri: String): Result<Unit> = runCatching {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(uri), "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    override suspend fun cleanupUpdate(): Result<Unit> = runCatching {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "app-update.apk"
        )
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getUpdateFileUri(): Uri {
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "app-update.apk"
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}