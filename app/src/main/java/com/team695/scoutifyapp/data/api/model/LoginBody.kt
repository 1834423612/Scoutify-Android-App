package com.team695.scoutifyapp.data.api.model

data class LoginBody(
    final val team_number: Int,
    final val username: String,
    final val password: String,
)