package com.aronid.weighttrackertft.data.routine.section


import com.aronid.weighttrackertft.data.routine.RoutineModel.SectionModel
import javax.inject.Inject

// NEW: Created SectionRepository to manage sections (mock implementation)
class SectionRepository @Inject constructor() {
    private val sections = mutableListOf<SectionModel>()

    suspend fun getSections(): List<SectionModel> {
        return sections.toList()
    }

    suspend fun createSection(name: String) {
        sections.add(SectionModel(name = name))
    }

    suspend fun addRoutineToSection(sectionId: String, routineId: String) {
        val section = sections.find { it.id == sectionId }
        section?.routineIds?.add(routineId)
    }

    suspend fun updateSection(section: SectionModel) {
        val index = sections.indexOfFirst { it.id == section.id }
        if (index != -1) {
            sections[index] = section
        }
    }
}