package onlytrade.app.offer

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import onlytrade.app.db.suspendTransaction
import onlytrade.app.offer.data.dao.OfferDao
import onlytrade.app.offer.data.table.OfferTable
import onlytrade.app.product.ProductRepository
import onlytrade.app.utils.CustomBooleanOp
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.selectAll

object OfferRepository {
    private val table = OfferTable
    private val dao = OfferDao
    private val productRepository = ProductRepository

    suspend fun addOffer(addOfferRequest: AddOfferRequest) = suspendTransaction {

        if (getOffersByProductId(addOfferRequest.offerReceiverProductId).size > 20) {
            return@suspendTransaction null
        }

        dao.new {
            this.offerMakerId = addOfferRequest.offerMakerId
            this.offerReceiverId = addOfferRequest.offerReceiverId
            this.offerReceiverProductId = addOfferRequest.offerReceiverProductId
            this.offeredProductIds = Json.encodeToString(addOfferRequest.offeredProductIds)
            this.extraPrice = 0.0  //todo addOfferRequest.extraPrice
        }.let {
            exposedLogger.info("Offer Added :${it.id.value}")
            toModel(it)
        }
    }

    /**
     * This method returns relevant unaccepted offers for a product.
     * Used by getProducts api.
     */
    fun getOffersByProductId(productId: Long) =
        dao.find { table.offerReceiverProductId eq productId and (table.accepted eq false) }
            .map(::toModel)

    /**
     * Returns the 1st offer made only or null.
     */
    /*  suspend fun getOfferMade(offerMakerId: Long, productId: Long) = suspendTransaction {
          dao.find((table.offerMakerId eq offerMakerId) and (table.offerReceiverProductId eq productId))
              .firstOrNull()?.let(::toModel)
      }*/

    /**
     * Returns the 1st offer received only or null.
     */
    /*   suspend fun getOfferReceived(offerReceiverId: Long, productId: Long) = suspendTransaction {
           dao.find((table.offerReceiverId eq offerReceiverId) and (table.offerReceiverProductId eq productId))
               .firstOrNull()?.let(::toModel)
       }*/

    /**
     * Returns all incomplete offers.
     * The number of offers will not exceed 20 for retrieval.
     */
    suspend fun getOffers() =
        suspendTransaction {
            /*    var query = table.selectAll()
                query = if (pageNo > 1) {    // 2..20..3..40..4..60
                    val offset = ((pageSize * pageNo) - pageSize).toLong()
                    query.offset(offset).limit(pageSize)
                } else query.limit(pageSize)*/

            table.selectAll().where(table.completed eq false).limit(20).map(::toModel)

        }

    suspend fun getOfferMade(offerMakerId: Long, offerReceiverProductId: Long) =
        suspendTransaction {
            dao.find((table.offerMakerId eq offerMakerId) and (table.offerReceiverProductId eq offerReceiverProductId))
                .firstOrNull()?.let(::toModel)
        }


    suspend fun acceptOffer(id: Long) = suspendTransaction {
        var productTraded = false
        var deletedOffers = 0
        dao.findByIdAndUpdate(id) { offer ->
            offer.accepted = true
            val offeredProducts = Json.decodeFromString<Set<Long>>(offer.offeredProductIds)
            val tradedProductIds = offeredProducts + offer.offerReceiverProductId
            val tradedProductIdsJson = Json.encodeToString(tradedProductIds)
            var productTradedCount = 0
            tradedProductIds.forEach { offeredProductId ->
                if (productRepository.setTraded(offeredProductId)) {
                    productTradedCount++
                }
            }
            deletedOffers = table.deleteWhere {
                table.id neq offer.id and Op.build {
                    val receiverCheck = "${table.offerReceiverProductId.name} = ANY ('{${
                        tradedProductIds.joinToString(",")
                    }}')"
                    val offeredCheck =
                        "${table.offeredProductIds.name}::jsonb && '$tradedProductIdsJson'::jsonb"
                    CustomBooleanOp("($receiverCheck OR $offeredCheck)")
                }

            }
            productTraded =
                productTradedCount == offeredProducts.size && deletedOffers > 0
        }?.let {
            exposedLogger.info("offer accepted = ${it.accepted} and deleted = $deletedOffers other offers involving traded products.")
            it.accepted && productTraded
        } ?: false
    }

    suspend fun completeOffer(id: Long) = suspendTransaction {
        dao.findByIdAndUpdate(id) { offer ->
            offer.completed = true
        }?.let {
            exposedLogger.info("offer completed = ${it.completed}")
            it.completed
        } ?: false
    }

    suspend fun deleteOffer(id: Long) = suspendTransaction {
        dao.findById(id)?.let {
            it.delete()
            true
        } ?: false
    }


    private fun toModel(offerDao: OfferDao) = offerDao.run {
        Offer(
            id = id.value,
            offerMakerId = offerMakerId,
            offerReceiverId = offerReceiverId,
            offerReceiverProduct = productRepository.getProductById(offerReceiverProductId)!!,
            extraPrice = extraPrice,
            accepted = accepted,
            completed = completed,
            offeredProducts = emptyList()
        )
    }

    private fun toModel(row: ResultRow): Offer {
        val offeredProductIds = Json.decodeFromString<Set<Long>>(row[table.offeredProductIds])
        return Offer(
            id = row[table.id].value,
            offerMakerId = row[table.offerMakerId],
            offerReceiverId = row[table.offerReceiverId],
            offerReceiverProduct = productRepository.getProductById(row[table.offerReceiverProductId])!!,
            extraPrice = row[table.extraPrice],
            accepted = row[table.accepted],
            completed = row[table.completed],
            offeredProducts = productRepository.getProductsByIds(offeredProductIds)
        )
    }

}