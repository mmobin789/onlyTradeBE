package onlytrade.app.login.data.user.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String?,
    val phone: String?,
    val email: String?,
    val verified: Boolean,
    val loggedIn: Boolean
)
