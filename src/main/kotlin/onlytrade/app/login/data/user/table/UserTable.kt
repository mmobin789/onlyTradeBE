package onlytrade.app.login.data.user.table

import org.jetbrains.exposed.dao.id.IntIdTable


object UserTable : IntIdTable(name = "user") {
    val phone = text("phone").uniqueIndex().nullable()
    val email = text("email").uniqueIndex().nullable()
    val password = text("pwd")
    val verified = bool("verified").default(false)
    val loggedIn = bool("loggedIn").default(true)
    val usersName = text("name").nullable()

}