package com.team695.scoutifyapp.data.api.model

import com.team695.scoutifyapp.data.extensions.convertIsoToUnix
import com.team695.scoutifyapp.db.TaskEntity
import com.team695.scoutifyapp.ui.extensions.convertUnixToIso
import java.util.Date
import kotlin.Int

class Task(
    val id: Int,
    val type: TaskType,
    val matchNum: Int,
    val teamNum: Int,
    val time: Long,
    val progress: Int, // 0-100
    ) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        if (time != other.time) {
            return if ((time - other.time) > 0) 1 else -1
        }

        println("${type.ordinal}, ${other.type.ordinal}, type: ${type.toString()} ")

        return type.ordinal - other.type.ordinal
    }
}

fun TaskEntity.createTaskFromDb(): Task {
    return Task(
        id = this.id.toInt(),
        type = TaskType.valueOf(this.type),
        matchNum = this.matchNum.toInt(),
        teamNum = this.teamNum.toInt(),
        time = this.time,
        progress = this.progress.toInt()
    )
}

data class ServerFormatTask(
    val sm_year: Int,
    val cm_event_code: String,
    val tm_number: Int,
    val um_id: String,
    val user_tm_number: Int,
    val gm_number: Int,
    val checkin_task: String,
    val task_completed: Int,
    val gm_game_type: Char,
    val task_id: Int,
    val ett_ts: String,
) {
    fun convertToAppFormat(): Task {
        return Task(
            id = task_id,
            type = if (checkin_task == "Scout") TaskType.SCOUTING else TaskType.PIT,
            matchNum = gm_number,
            teamNum = tm_number,
            time = ett_ts.convertIsoToUnix(),
            progress = task_completed
        )
    }
}

fun TaskEntity.convertToServerFormat(
    gameConstants: GameConstants,
    tm_number: Int,
    um_id: String,
    user_tm_number: Int = 695,
    gm_game_type: Char
): ServerFormatTask {
    return ServerFormatTask(
        sm_year = gameConstants.frc_season_master_sm_year,
        cm_event_code = gameConstants.competition_master_cm_event_code,
        tm_number = tm_number,
        um_id = um_id,
        user_tm_number = user_tm_number,
        gm_number = this.matchNum.toInt(),
        checkin_task = TaskType.valueOf(this.type).toString(),
        task_completed = (this.progress == 100L).toString().toInt(),
        gm_game_type = gm_game_type,
        task_id = this.id.toInt(),
        ett_ts = this.time.convertUnixToIso()
    )
}