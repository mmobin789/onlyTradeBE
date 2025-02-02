package onlytrade.app.login.data.user.table

import org.jetbrains.exposed.dao.id.IntIdTable


object UserTable : IntIdTable(name = "user") {
    val phone = varchar("phone", 13).uniqueIndex().nullable()
    val email = varchar("email", 100).uniqueIndex().nullable()
    val verified = varchar("verifiedUser", 5).default("false")
    val usersName = varchar("name", 50).nullable()
}