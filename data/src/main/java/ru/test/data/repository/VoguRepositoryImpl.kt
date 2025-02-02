package ru.test.data.repository

import ru.test.data.network.mappers.timetable.TimetableDtoToDbMapper
import ru.test.data.network.mappers.timetable.TimetableDtoToDomainMapper
import ru.test.data.network.services.VoguService
import ru.test.data.network.utils.InternetChecker
import ru.test.data.storage.dao.VoguDao
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
    private val timetableDtoToDomainMapper: TimetableDtoToDomainMapper,
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
        voguStore.saveTimetableType(type.type)
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

    override suspend fun getTimetable(
        timetableId: Int,
        timetableType: String,
        dateStart: String,
        dateEnd: String,
        isPaging: Boolean
    ): Result<Timetable> {
        return try {
//            val isOffline = !internetChecker.isInternetAvailable()

            val requestBody = mapOf(
                timetableType to timetableId.toString(),
                "date_start" to dateStart,
                "date_end" to dateEnd,
                "selected_lesson_type" to "typical"
            )
            voguService.getToken()
            val response = voguService.getTimetable(requestBody)

            if (!isPaging) {
                voguDao.clearTimetable()
                voguDao.insertWeeksWithDaysAndLessons(
                    timetableDtoToDbMapper.invoke(
                        timetable = response,
                        timetableId = timetableId
                    )
                )
            }

            Result.success(timetableDtoToDomainMapper.invoke(response))

        } catch (e: Exception) {
            Result.success(
                if (isPaging)
                    Timetable(
                        name = "",
                        weeks = emptyList(),
                        isOffline = true
                    )
                else
                    weekDbToDomainMapper.invoke(
                        timetable = voguDao.getTimetable(),
                        isOffline = true
                    )
            )
        }
    }
}