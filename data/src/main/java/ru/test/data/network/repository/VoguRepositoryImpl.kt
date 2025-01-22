package ru.test.data.network.repository

import kotlinx.coroutines.flow.firstOrNull
import ru.test.data.network.mappers.timetable.TimetableDtoToDbMapper
import ru.test.data.network.services.VoguService
import ru.test.data.network.utils.InternetChecker
import ru.test.data.storage.dao.VoguDao
import ru.test.data.storage.entities.domainToDto
import ru.test.data.storage.mappers.CabinetDbToDomainMapper
import ru.test.data.storage.mappers.GroupDbToDomainMapper
import ru.test.data.storage.mappers.TeacherDbToDomainMapper
import ru.test.data.storage.mappers.WeekDbToDomainMapper
import ru.test.data.storage.store.VoguStore
import ru.test.domain.models.timetable.EntityItem
import ru.test.domain.models.timetable.EntityType
import ru.test.domain.models.timetable.Timetable
import ru.test.domain.repository.VoguRepository
import javax.inject.Inject

class VoguRepositoryImpl @Inject constructor(
    private val voguService: VoguService,
    private val voguDao: VoguDao,
    private val voguStore: VoguStore,
    private val timetableDtoToDbMapper: TimetableDtoToDbMapper,
    private val weekDbToDomainMapper: WeekDbToDomainMapper,
    private val groupDbToDomainMapper: GroupDbToDomainMapper,
    private val teacherDbToDomainMapper: TeacherDbToDomainMapper,
    private val cabinetDbToDomainMapper: CabinetDbToDomainMapper,
    private val internetChecker: InternetChecker
) : VoguRepository {

    override suspend fun parseData() {
       TODO("Удалить эту функцию")
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
    ): Timetable {

        var isOffline = true

        if (internetChecker.isInternetAvailable()) {

            isOffline = false

            voguDao.deleteAllTimetables()
            voguDao.deleteAllWeeks()
            voguDao.deleteAllDays()
            voguDao.deleteAllLessons()

            val timetableId = voguStore.timetableId.firstOrNull()
            val timetableType = voguStore.timetableType.firstOrNull()

            val requestBody = mapOf(
                timetableType.toString() to timetableId.toString(),
                "date_start" to dateStart,
                "date_end" to dateEnd,
                "selected_lesson_type" to "typical"
            )

            voguService.getToken()

            val response = voguService.getTimetable(requestBody)

            voguDao.insertWeeksWithDaysAndLessons(
                timetableDtoToDbMapper.invoke(
                    timetable = response,
                    timetableId = timetableId!! // <--- Придумай способ исправить это (!!)
                )
            )
        }

        return weekDbToDomainMapper.invoke(
            timetable = voguDao.getTimetable(),
            isOffline = isOffline
        )
    }
}