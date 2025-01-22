package ru.test.data.network.repository

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
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

    override suspend fun downloadUpdate(url: String): Result<String> = runCatching {
        suspendCoroutine { continuation ->
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("Загрузка обновления")
                .setDescription("Загружается новая версия приложения")
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                .setDestinationInExternalFilesDir(
                    context,
                    Environment.DIRECTORY_DOWNLOADS,
                    "app-update.apk"
                )

            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            val onCompleter = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (downloadId == id) {
                        val uri = getUpdateFileUri()
                        context?.unregisterReceiver(this)
                        continuation.resume(uri.toString())
                    }
                }
            }

            ContextCompat.registerReceiver(
                context,
                onCompleter,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
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