package ru.test.data.network.repository

import kotlinx.coroutines.flow.firstOrNull
import ru.test.data.network.mappers.WeekDtoToDbMapper
import ru.test.data.network.services.VoguService
import ru.test.data.network.utils.InternetChecker
import ru.test.data.storage.dao.VoguDao
import ru.test.data.storage.entities.domainToDto
import ru.test.data.storage.mappers.CabinetDbToDomainMapper
import ru.test.data.storage.mappers.GroupDbToDomainMapper
import ru.test.data.storage.mappers.TeacherDbToDomainMapper
import ru.test.data.storage.mappers.WeekDbToDomainMapper
import ru.test.data.storage.store.VoguStore
import ru.test.domain.models.EntityItem
import ru.test.domain.models.EntityType
import ru.test.domain.models.Week
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class VoguRepositoryImpl @Inject constructor(
    private val voguService: VoguService,
    private val voguDao: VoguDao,
    private val voguStore: VoguStore,
    private val weekDtoToDbMapper: WeekDtoToDbMapper,
    private val weekDbToDomainMapper: WeekDbToDomainMapper,
    private val groupDbToDomainMapper: GroupDbToDomainMapper,
    private val teacherDbToDomainMapper: TeacherDbToDomainMapper,
    private val cabinetDbToDomainMapper: CabinetDbToDomainMapper,
    private val internetChecker: InternetChecker
) : VoguRepository {

//    private fun decodeHtml(encodedString: String): String {
//        return Html.fromHtml(encodedString, Html.FROM_HTML_MODE_LEGACY).toString()
//    }

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

    override suspend fun saveTimetableConfig(
        timetableId: Int,
        type: EntityType
    ) {
        voguStore.saveTimetableId(timetableId)
        voguStore.saveTimetableType(type.domainToDto().type)
    }

    override suspend fun clearGroupId() {
        voguStore.deleteTimetableId()
    }

    override suspend fun getEntitiesByTypePaged(
        query: String,
        offset: Int,
        pageSize: Int,
        type: EntityType
    ): List<EntityItem> {

        return when (type) {
            EntityType.TEACHER -> {
                teacherDbToDomainMapper.invoke(
                    voguDao.getParsedTeachers(
                        query = query,
                        offset = offset,
                        pageSize = pageSize
                    )
                )
            }

            EntityType.GROUP -> {
                groupDbToDomainMapper.invoke(
                    voguDao.getParsedGroups(
                        query = query,
                        offset = offset,
                        pageSize = pageSize
                    )
                )
            }

            EntityType.CABINET -> {
                cabinetDbToDomainMapper(
                    voguDao.getParsedCabinets(
                        query = query,
                        offset = offset,
                        pageSize = pageSize
                    )
                )
            }
        }
    }

    override suspend fun getTimetableForGroup(
        dateStart: String,
        dateEnd: String
    ): List<Week> {

        if (internetChecker.isInternetAvailable()) {

            voguDao.deleteAllWeeks()
            voguDao.deleteAllDays()
            voguDao.deleteAllLessons()

            val timetableIdId = voguStore.timetableId.firstOrNull()
            val timetableType = voguStore.timetableType.firstOrNull()

            val requestBody = mapOf(
                timetableType.toString() to timetableIdId.toString(),
                "date_start" to dateStart,
                "date_end" to dateEnd,
                "selected_lesson_type" to "typical"
            )

            voguService.getToken()

            val response = voguService.getTimetable(requestBody).schedule
            voguDao.insertWeeksWithDaysAndLessons(
                weekDtoToDbMapper.invoke(response)
            )
        }

        return weekDbToDomainMapper.invoke(voguDao.getWeek())
    }
}