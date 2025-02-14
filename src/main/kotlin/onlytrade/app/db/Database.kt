package onlytrade.app.db

import kotlinx.coroutines.Dispatchers
import onlytrade.app.login.data.user.table.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases() {
    val dbUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/ot_dev"
    val dbPwd = System.getenv("DATABASE_PASSWORD") ?: "1994"
    val dbUser = System.getenv("DATABASE_USER") ?: "postgres"
    Database.connect(
        url = dbUrl,
        user = dbUser,
        password = dbPwd
    )

    transaction {
        SchemaUtils.create(UserTable)
    }
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)