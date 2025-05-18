package onlytrade.app.product

import onlytrade.app.db.suspendTransaction
import onlytrade.app.offer.OfferRepository
import onlytrade.app.product.data.dao.ProductDao
import onlytrade.app.product.data.table.ProductTable
import onlytrade.app.viewmodel.product.repository.data.db.Product
import onlytrade.app.viewmodel.product.repository.data.remote.request.AddProductRequest
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.inList
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.exposedLogger
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update


object ProductRepository {
    private val offerRepository = OfferRepository
    private val table = ProductTable
    private val dao = ProductDao

    fun getProductById(id: Long): Product? = dao.findById(id)?.let(::toModel)

    /**
     * Returns true if any of the ids are already traded.
     */
    fun haveTraded(ids: Set<Long>) =
        table.selectAll().where(table.id inList ids and (table.traded eq true)).limit(1)
            .any()

    /**
     * Batch update setting multiple products to traded.
     */
    fun setTraded(ids: Set<Long>): Int = table.update {
        table.id inList ids
    }

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
                traded = dao.traded,
                offers = emptyList()
            )
        }

    suspend fun getProducts(pageNo: Int, pageSize: Int, userId: Long? = null) =
        suspendTransaction {
            var notTraded = table.traded eq false
            var query = table.selectAll()
            if (userId != null)
                notTraded = notTraded and (table.userId eq userId)

            query = query.where(notTraded)

            query = if (pageNo > 1) {    // 2..20..3..40..4..60
                val offset = ((pageSize * pageNo) - pageSize).toLong()
                query.offset(offset).limit(pageSize)
            } else query.limit(pageSize)

            query.map(::toModel)

        }

    /**
     * Returns the id of product on successful insertion.
     */
    suspend fun addProduct(userId: Long, addProductRequest: AddProductRequest) =
        suspendTransaction {
            dao.new {
                this.userId = userId
                categoryId = addProductRequest.categoryId
                subcategoryId = addProductRequest.subcategoryId
                name = addProductRequest.name
                description = addProductRequest.description
                estPrice = addProductRequest.estPrice
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
        traded = row[table.traded],
        offers = offerRepository.getOffersByProductId(row[table.id].value)
            .ifEmpty { null }
    )

    private fun toModel(productDao: ProductDao) = Product(
        id = productDao.id.value,
        categoryId = productDao.categoryId,
        subcategoryId = productDao.subcategoryId,
        userId = productDao.userId,
        name = productDao.name,
        description = productDao.description,
        estPrice = productDao.estPrice,
        imageUrls = productDao.imageUrls.split(","),
        traded = productDao.traded
    )
}