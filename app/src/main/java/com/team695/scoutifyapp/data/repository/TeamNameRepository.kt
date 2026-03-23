package com.team695.scoutifyapp.data.repository

import com.team695.scoutifyapp.data.api.service.TeamNameService
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.db.TeamNamesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeamNameRepository (
    private val service: TeamNameService,
    private val db: AppDatabase
): Repository {
    private val queries = db.teamNamesQueries

    override suspend fun fetch(): Result<Any> {
        return withContext(Dispatchers.IO) {
            val teams = service.fetchTeamNames()

            queries.deleteAll()

            teams.forEach {
                queries.insertTeam(
                    team_number = it.team_number,
                    team_name = it.team_name
                )
            }

            return@withContext Result.success(Unit)
        }
    }

    suspend fun getTeam(teamNumber: String): TeamNamesEntity? {
        return withContext(Dispatchers.IO) {
            queries.selectByTeamNumber(teamNumber).executeAsOneOrNull()
        }
    }

    // you need the chatgpt ahh remapping cuz sqldelight dont let you do ts in house (iykyk)
    suspend fun getMatchTeams(
        redAlliance: List<String>,
        blueAlliance: List<String>
    ): List<String?> {
        return withContext(Dispatchers.IO) {
            val requested = listOf(
                redAlliance[0], redAlliance[1], redAlliance[2],
                blueAlliance[0], blueAlliance[1], blueAlliance[2]
            )

            val teamsFromDb = queries.selectMatchTeams(
                requested[0], requested[1], requested[2],
                requested[3], requested[4], requested[5]
            ).executeAsList()

            requested.map { teamNumber ->
                teamsFromDb.find { it.team_number == teamNumber }?.team_name
            }
        }
    }

    suspend fun getAllTeams(): List<TeamNamesEntity> {
        return withContext(Dispatchers.IO) {
            queries.selectAll().executeAsList()
        }
    }
}