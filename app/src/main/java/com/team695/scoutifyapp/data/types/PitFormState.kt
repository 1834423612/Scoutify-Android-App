package com.team695.scoutifyapp.data.types

data class PitFormState(
    val eventId: String = "",
    val eventDisplayName: String = "",
    val formVersion: String = "",
    val activeTab: PitScoutingTab? = null,
    val tabs: List<PitScoutingTab> = emptyList(),
    val teamSuggestions: List<TeamSuggestion> = emptyList(),
    val assignments: List<PitAssignment> = emptyList(),
    val completedTeams: Set<String> = emptySet(),
    val syncBanner: String? = null,
    val versionMismatch: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null
)
