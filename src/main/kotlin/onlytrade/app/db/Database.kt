package onlytrade.app.db

import io.ktor.server.application.Application
import io.ktor.server.application.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlytrade.app.login.data.user.table.UserTable
import onlytrade.app.offer.data.table.OfferTable
import onlytrade.app.product.data.table.ProductTable
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.MigrationUtils

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
        MigrationUtils.statementsRequiredForDatabaseMigration(UserTable, ProductTable, OfferTable)
            .apply {
                execInBatch(this)
            }
    }
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    withContext(Dispatchers.IO) {
        transaction {
            block()
        }
    }