package ru.test.data.network.repository

import android.text.Html
import kotlinx.coroutines.flow.firstOrNull
import ru.test.data.network.mappers.WeekDtoToDbMapper
import ru.test.data.network.services.VoguService
import ru.test.data.network.utils.InternetChecker
import ru.test.data.storage.dao.VoguDao
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
        /*

                Стоит вынести в отдельный сервис
                Для создания фоновой задачи и первого заполнения бд

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
                }*/
    }

    override suspend fun saveGroupId(groupId: Int) {
        voguStore.saveGroupId(groupId = groupId)
    }

    override suspend fun clearGroupId() {
        voguStore.deleteGroupId()
    }

    override suspend fun getGroupsPaged(
        query: String,
        offset: Int,
        pageSize: Int
    ): List<Group> {
        return groupDbToDomainMapper.invoke(
            voguDao.getAllGroups(
                query = query,
                offset = offset,
                pageSize = pageSize
            )
        )
    }

    override suspend fun getTimetableForGroup(
        dateStart: String,
        dateEnd: String
    ): List<Week> {

        if (internetChecker.isInternetAvailable()) {

            voguDao.deleteAllWeeks()
            voguDao.deleteAllDays()
            voguDao.deleteAllLessons()

            val groupId = voguStore.groupId.firstOrNull()

            val requestBody = mapOf(
                "group_id" to groupId.toString(),
                "date_start" to dateStart,
                "date_end" to dateEnd,
                "selected_lesson_type" to "typical"
            )

            try {
                voguService.getToken()

                val response = voguService.getTimetable(requestBody).schedule
                voguDao.insertWeeksWithDaysAndLessons(
                    weekDtoToDbMapper.invoke(response)
                )
            } catch (e: Exception) {
                println(e.stackTrace)
            }
        }

        return weekDbToDomainMapper.invoke(voguDao.getWeek())
    }
}