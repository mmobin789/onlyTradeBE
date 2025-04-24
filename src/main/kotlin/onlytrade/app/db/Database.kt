package onlytrade.app.db

import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.Dispatchers
import onlytrade.app.login.data.user.table.UserTable
import onlytrade.app.offer.data.table.OfferTable
import onlytrade.app.product.data.table.ProductTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val dbUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/ot_dev"
    val dbPwd = System.getenv("DATABASE_PASSWORD") ?: "1994"
    val dbUser = System.getenv("DATABASE_USER") ?: "postgres"
    Database.connect(
        url = dbUrl,
        user = dbUser,
        password = dbPwd
    )

    log.info("Connecting to DB: $dbUrl with user: $dbUser")

    transaction {
        SchemaUtils.create(UserTable, ProductTable, OfferTable)
    }
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)