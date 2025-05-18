package onlytrade.app.login.data.user

import onlytrade.app.db.suspendTransaction
import onlytrade.app.login.data.user.dao.UserDAO
import onlytrade.app.login.data.user.table.UserTable
import onlytrade.app.utils.BcryptUtils.hashPassword
import onlytrade.app.viewmodel.login.repository.data.db.User
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.exposedLogger
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.deleteWhere

object UserRepository {
    private val dao = UserDAO
    private val table = UserTable

    /**
     * The user needs to be authenticated before this method can be used else it will throw an exception.
     */
    suspend fun findUserByCredential(credential: String) = suspendTransaction {
        dao.find((table.phone eq credential) or (table.email eq credential)).singleOrNull()
            ?.toModel()
    }

    suspend fun findUserByEmail(email: String) = suspendTransaction {
        dao.find(table.email eq email).singleOrNull()?.toModel()
        /*     table.selectAll().where(table.email eq email).singleOrNull()?.let { row ->
                 User(
                     id = row[table.id].value,
                     name = row[table.usersName],
                     phone = row[table.phone],
                     email = row[table.email],
                     verified = row[table.verified] == "true",
                     loggedIn = row[table.verified] == "true"
                 )
             }*/
    }

    suspend fun findUserByPhone(phone: String) = suspendTransaction {
        dao.find(table.phone eq phone).singleOrNull()?.toModel()
        /*  table.selectAll().where(table.phone eq phone).singleOrNull()?.let { row ->
              User(
                  id = row[table.id].value,
                  name = row[table.usersName],
                  phone = row[table.phone],
                  email = row[table.email],
                  verified = row[table.verified] == "true",
                  loggedIn = row[table.loggedIn] == "true"
              )
          }*/
    }

    suspend fun addUserByEmail(email: String, password: String) = suspendTransaction {
        dao.new {
            this.email = email
            this.password = hashPassword(password)
        }.toModel()
    }


    suspend fun addUserByPhone(phone: String, password: String) = suspendTransaction {
        dao.new {
            this.phone = phone
            this.password = hashPassword(password)
        }.toModel()
    }

    suspend fun setUserDocs(id: Long, imageUrls: String) = suspendTransaction {
        dao.findByIdAndUpdate(id) { user ->
            user.docs = imageUrls
        }?.also {
            exposedLogger.info("Updated User doc urls =  ${it.docs}")
        }
    }

    suspend fun setUserLoggedInByEmail(email: String) = suspendTransaction {
        dao.findSingleByAndUpdate(table.email eq email) { user ->
            user.loggedIn = true
        }?.loggedIn == true
    }

    suspend fun setUserLoggedInByPhone(phone: String) = suspendTransaction {
        dao.findSingleByAndUpdate(table.phone eq phone) { user ->
            user.loggedIn = true
        }?.loggedIn == true
    }

    suspend fun setUserVerifiedByEmail(email: String) = suspendTransaction {
        dao.findSingleByAndUpdate(table.email eq email) { user ->
            user.verified = true
        }?.verified == true
    }

    suspend fun setUserVerifiedByPhone(phone: String) = suspendTransaction {
        dao.findSingleByAndUpdate(table.phone eq phone) { user ->
            user.verified = true
        }?.verified == true
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

    private fun UserDAO.toModel(): User = User(
        id = id.value,
        name = usersName,
        password = password,
        phone = phone,
        email = email,
        verified = verified,
        loggedIn = loggedIn
    )
}