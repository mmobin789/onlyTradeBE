package onlytrade.app.product

import onlytrade.app.db.suspendTransaction
import onlytrade.app.product.data.dao.ProductDao
import onlytrade.app.product.data.table.ProductTable
import onlytrade.app.viewmodel.product.add.repository.data.db.Product
import onlytrade.app.viewmodel.product.add.repository.data.remote.model.request.AddProductRequest
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll

object ProductsRepository {
    private val table = ProductTable
    private val dao = ProductDao

    suspend fun getProducts(pageNo: Int, pageSize: Int = 20, userId: Int? = null) =
        suspendTransaction {
            val query = table.selectAll().limit(pageSize)
            if (userId != null)
                query.where(table.userId eq userId)

            if (pageNo > 1) {    // 2..20..3..40..4..60
                val offset = ((pageSize * pageNo) - pageSize).toLong()
                query.offset(offset)
            }

            query.map { row ->
                Product(
                    id = row[table.id].value,
                    userId = row[table.userId],
                    name = row[table.name],
                    description = row[table.description],
                    estPrice = row[table.estPrice]
                )

            }

        }

    suspend fun addProduct(addProductRequest: AddProductRequest, uid: Int) = suspendTransaction {
//todo
        dao.new {
            userId = uid
            name = addProductRequest.name
            description = addProductRequest.description
            estPrice = addProductRequest.estPrice
        }
    }

}