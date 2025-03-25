package onlytrade.app.login.data.user.table

import org.jetbrains.exposed.dao.id.IntIdTable


object UserTable : IntIdTable(name = "user") {
    val phone = varchar("phone", 13).uniqueIndex().nullable()
    val email = text("email").uniqueIndex().nullable()
    val password = varchar("pwd", 10)
    val verified = bool("verified").default(false)
    val loggedIn = bool("loggedIn").default(true)
    val usersName = varchar("name", 50).nullable()

}