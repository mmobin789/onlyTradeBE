package onlytrade.app.product

import onlytrade.app.db.suspendTransaction
import onlytrade.app.offer.OfferRepository
import onlytrade.app.product.data.dao.ProductDao
import onlytrade.app.product.data.table.ProductTable
import onlytrade.app.viewmodel.product.repository.data.db.Product
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.selectAll

object ProductRepository {
    private val offerRepository = OfferRepository
    private val table = ProductTable
    private val dao = ProductDao

    fun getProductsByIds(ids: Set<Long>) =
        dao.forIds(ids.toList()).map { dao ->
            Product(
                id = dao.id.value,
                categoryId = dao.categoryId,
                subcategoryId = dao.subcategoryId,
                userId = dao.userId,
                name = dao.name,
                description = dao.description,
                estPrice = dao.estPrice,
                imageUrls = dao.imageUrls.split(","),
                offers = offerRepository.getOffersByProductId(dao.id.value)
                    .ifEmpty { null }
            )
        }

    suspend fun getProducts(pageNo: Int, pageSize: Int, userId: Long? = null) =
        suspendTransaction {
            var query = table.selectAll()
            if (userId != null)
                query = query.where(table.userId eq userId)

            query = if (pageNo > 1) {    // 2..20..3..40..4..60
                val offset = ((pageSize * pageNo) - pageSize).toLong()
                query.offset(offset).limit(pageSize)
            } else query.limit(pageSize)

            query.map(::toModel)

        }

    /**
     * Returns the id of product on successful insertion.
     */
    suspend fun addProduct(userId: Long, product: Product) =
        suspendTransaction {
            dao.new {
                this.userId = userId
                categoryId = product.categoryId
                subcategoryId = product.subcategoryId
                name = product.name
                description = product.description
                estPrice = product.estPrice
                imageUrls = "ImageURLs to be added"
            }.id.value.also {
                exposedLogger.info("Product Added :$it")
            }
        }

    suspend fun setProductImages(id: Long, imageUrls: String) = suspendTransaction {
        dao.findByIdAndUpdate(id) { product ->
            product.imageUrls = imageUrls
        }?.also {
            exposedLogger.info("Updated Product image urls =  ${it.imageUrls}")
        }
    }

    private fun toModel(row: ResultRow) = Product(
        id = row[table.id].value,
        categoryId = row[table.categoryId],
        subcategoryId = row[table.subcategoryId],
        userId = row[table.userId],
        name = row[table.name],
        description = row[table.description],
        estPrice = row[table.estPrice],
        imageUrls = row[table.imageUrls].split(","),
        offers = offerRepository.getOffersByProductId(row[table.id].value)
            .ifEmpty { null }
    )
}