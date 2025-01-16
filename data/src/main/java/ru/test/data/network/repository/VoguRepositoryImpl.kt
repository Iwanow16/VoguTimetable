package ru.test.data.network.repository

import android.text.Html
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.test.data.network.mappers.WeekDtoToDbMapper
import ru.test.data.network.services.VoguService
import ru.test.data.network.utils.InternetChecker
import ru.test.data.storage.dao.VoguDao
import ru.test.data.storage.entities.CabinetDb
import ru.test.data.storage.entities.GroupDb
import ru.test.data.storage.entities.TeacherDb
import ru.test.data.storage.mappers.GroupDbToDomainMapper
import ru.test.data.storage.mappers.WeekDbToDomainMapper
import ru.test.data.storage.store.VoguStore
import ru.test.domain.model.Group
import ru.test.domain.model.Week
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class VoguRepositoryImpl @Inject constructor(
    private val voguService: VoguService,
    private val voguDao: VoguDao,
    private val voguStore: VoguStore,
    private val weekDtoToDbMapper: WeekDtoToDbMapper,
    private val weekDbToDomainMapper: WeekDbToDomainMapper,
    private val groupDbToDomainMapper: GroupDbToDomainMapper,
    private val internetChecker: InternetChecker
) : VoguRepository {

    private fun decodeHtml(encodedString: String): String {
        return Html.fromHtml(encodedString, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    override suspend fun parseData() {
        withContext(Dispatchers.IO) {
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

            // Clear db
            voguDao.deleteAllGroups()
            voguDao.deleteAllTeachers()
            voguDao.deleteAllCabinets()

            // Insert db
            voguDao.insertGroups(groups)
            voguDao.insertTeachers(teachers)
            voguDao.insertCabinets(cabinets)

            // Get csrf-token
            val token = doc.selectFirst("input[name=csrfmiddlewaretoken]").attr("value")

            // Save token
            voguStore.saveCsrfToken(token)
        }
    }

    override suspend fun getGroupsFromCache(): List<Group> {
        return groupDbToDomainMapper.invoke(voguDao.getAllGroups())
    }

    override suspend fun getTimetableForGroup(groupId: Int): List<Week> {
        if (internetChecker.isInternetAvailable()) {

            val requestBody = mapOf(
                "group_id" to groupId.toString(),
                "date_start" to "2025-01-20",
                "date_end" to "2025-1-30",
                "selected_lesson_type" to "typical"
            )

            val response = voguService.getTimetable(requestBody).schedule

            val weekDbList = response.map { weekDto ->
                weekDtoToDbMapper(weekDto)
            }

            voguDao.insertWeeks(*weekDbList.toTypedArray())
        }

        return weekDbToDomainMapper.invoke(listOf())
    }
}