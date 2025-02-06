package onlytrade.app.login.session

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val name: String, val count: Int)