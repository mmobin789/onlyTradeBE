package onlytrade.app.login.data.user.dao

import onlytrade.app.login.data.user.table.UserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID


class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable)

    var usersName by UserTable.usersName
    var email by UserTable.email
    var phone by UserTable.phone
    var verified by UserTable.verified

}