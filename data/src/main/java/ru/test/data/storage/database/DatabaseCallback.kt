package ru.test.data.storage.database

import android.text.Html
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.test.data.storage.dao.VoguDao
import ru.test.data.storage.entities.CabinetDb
import ru.test.data.storage.entities.GroupDb
import ru.test.data.storage.entities.TeacherDb
import javax.inject.Inject
import javax.inject.Provider

class DatabaseCallback @Inject constructor(
    private val provider: Provider<VoguDao>
) : RoomDatabase.Callback() {

    companion object {
        private const val TAG = "DATABASE_CALLBACK"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            try {
                parseInitialData()
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }
    }

    private fun decodeHtml(encodedString: String): String {
        return Html.fromHtml(encodedString, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    private fun parseInitialData() {
        val url = "https://tt2.vogu35.ru/"
        val doc: Document = Jsoup.connect(url).get()

        // Get json
        val jsonGroups = decodeHtml(doc.getElementById("jsonGroups").attr("data-json"))
        val jsonTeachers = decodeHtml(doc.getElementById("jsonTeachers").attr("data-json"))
        val jsonLocations = decodeHtml(doc.getElementById("jsonLocations").attr("data-json"))

        val gson = Gson()

        // Convert json to data class
        val groups = gson.fromJson<List<GroupDb>>(
            jsonGroups,
            object : TypeToken<List<GroupDb>>() {}.type
        )

        val teachers = gson.fromJson<List<TeacherDb>>(
            jsonTeachers,
            object : TypeToken<List<TeacherDb>>() {}.type
        )

        val cabinets = gson.fromJson<List<CabinetDb>>(
            jsonLocations,
            object : TypeToken<List<CabinetDb>>() {}.type
        )

        val dao = provider.get()

        dao.updateGroups(groups)
        dao.updateTeachers(teachers)
        dao.updateCabinets(cabinets)
    }
}