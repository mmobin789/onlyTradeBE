package onlytrade.app.login.data.user.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime


object UserTable : LongIdTable(name = "user") {
    val phone = text("phone").uniqueIndex().nullable()
    val email = text("email").uniqueIndex().nullable()
    val password = text("pwd")
    val verified = bool("verified").default(false)
    val loggedIn = bool("loggedIn").default(true)
    val usersName = text("name").nullable()
    val docs = text("docs").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}