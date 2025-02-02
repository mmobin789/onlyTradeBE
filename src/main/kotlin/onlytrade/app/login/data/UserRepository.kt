package onlytrade.app.login.data

import onlytrade.app.db.suspendTransaction
import onlytrade.app.login.data.user.dao.UserDAO
import onlytrade.app.login.data.user.mapper.toModel
import onlytrade.app.login.data.user.table.UserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

object UserRepository {
    private val userDao = UserDAO
    private val table = UserTable

    suspend fun addUserByEmail(email: String) = suspendTransaction {
        userDao.new {
            this.email = email
        }.toModel()
    }


    suspend fun addUserByPhone(phone: String) = suspendTransaction {
        userDao.new {
            this.phone = phone
        }.toModel()
    }

    suspend fun setUserVerifiedByEmail(email: String) = suspendTransaction {
        userDao.findSingleByAndUpdate(table.email eq email) { user ->
            user.verified = "true"
        }?.verified == "true"
    }

    suspend fun setUserVerifiedByPhone(phone: String) = suspendTransaction {
        userDao.findSingleByAndUpdate(table.phone eq phone) { user ->
            user.verified = "true"
        }?.verified == "true"
    }

    suspend fun deleteUserByEmail(email: String) = suspendTransaction {
        table.deleteWhere(limit = 1) {
            table.email eq email
        } == 1
    }

    suspend fun deleteUserByPhone(phone: String) = suspendTransaction {
        table.deleteWhere(limit = 1) {
            table.phone eq phone
        } == 1
    }
}