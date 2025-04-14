package onlytrade.app.product

import onlytrade.app.db.suspendTransaction
import onlytrade.app.product.data.dao.ProductDao
import onlytrade.app.product.data.table.ProductTable
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.repository.data.db.Product
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.selectAll

object ProductsRepository {
    private val table = ProductTable
    private val dao = ProductDao

    suspend fun getProducts(pageNo: Int, pageSize: Int, userId: Int? = null) =
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
                    subcategoryId = row[table.subcategoryId],
                    userId = row[table.userId],
                    name = row[table.name],
                    description = row[table.description],
                    estPrice = row[table.estPrice],
                    imageUrls = row[table.imageUrls]
                )

            }

        }

    /**
     * Returns the id of product on successful insertion.
     */
    suspend fun addProduct(userId: Int, addProductRequest: AddProductRequest) = suspendTransaction {
        val productId = dao.new {
            this.userId = userId
            subcategoryId = addProductRequest.subcategoryId
            name = addProductRequest.name
            description = addProductRequest.description
            estPrice = addProductRequest.estPrice
            imageUrls = "ImageURLs to be added"
        }.id.value
        exposedLogger.info("Product Added :$productId")
        productId
    }

    suspend fun setProductImages(id: Int, imageUrls: String) = suspendTransaction {
        dao.findByIdAndUpdate(id) { product ->
            product.imageUrls = imageUrls
        }?.also {
            exposedLogger.info("Updated Product image urls =  ${it.imageUrls}")
        }
    }


}