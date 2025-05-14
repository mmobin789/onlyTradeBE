package onlytrade.app.login.data.user.dao

import onlytrade.app.login.data.user.table.UserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID


class UserDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserDAO>(UserTable)

    var usersName by UserTable.usersName
    var password by UserTable.password
    var email by UserTable.email
    var phone by UserTable.phone
    var verified by UserTable.verified
    var loggedIn by UserTable.loggedIn

}