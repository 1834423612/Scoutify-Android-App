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

            //queries.deleteAll()

            teams.forEach {
                queries.insertTeam          (
                    team_number = it.team_number,
                    team_name = it.team_name
                )
            }

            Result.success(Unit)
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
    ): List<TeamNamesEntity?> {
        return withContext(Dispatchers.IO) {
            val red1: String = redAlliance[0]
            val red2: String = redAlliance[1]
            val red3: String = redAlliance[2]
            val blue1: String = blueAlliance[0]
            val blue2: String = blueAlliance[1]
            val blue3: String = blueAlliance[2]

            val requested = listOf(red1, red2, red3, blue1, blue2, blue3)

            // Query the DB for any existing teams
            val teamsFromDb = queries.selectMatchTeams(
                red1, red2, red3, blue1, blue2, blue3
            ).executeAsList()

            // Map the requested numbers to their DB objects, using null if missing
            requested.map { teamNumber ->
                teamsFromDb.find { it.team_number == teamNumber }
            }
        }
    }

    suspend fun getAllTeams(): List<TeamNamesEntity> {
        return withContext(Dispatchers.IO) {
            queries.selectAll().executeAsList()
        }
    }
}