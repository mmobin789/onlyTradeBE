package onlytrade.app.offer

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import onlytrade.app.db.suspendTransaction
import onlytrade.app.offer.data.dao.OfferDao
import onlytrade.app.offer.data.table.OfferTable
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.selectAll

object OfferRepository {
    private val table = OfferTable
    private val dao = OfferDao

    suspend fun addOffer(offer: Offer) = suspendTransaction {
        dao.new {
            this.offerMakerId = offer.offerMakerId
            this.offerReceiverId = offer.offerReceiverId
            this.offerReceiverProductId = offer.offerReceiverProductId
            this.offeredProductIds = Json.encodeToString(offer.offeredProductIds)
            this.extraPrice = offer.extraPrice
        }.let {
            exposedLogger.info("Offer Added :${it.id.value}")
            toModel(it)
        }
    }

    fun getOffersByProductId(productId: Long) =
        //todo add limit to offers.  (like if there are 50 offers keep adding them but don't return to clients unless old offers are rejected.
        dao.find { table.offerReceiverProductId eq productId }.limit(50).map(::toModel)


    suspend fun getOfferMade(offerMakerId: Long, productId: Long) = suspendTransaction {
        dao.find((table.offerMakerId eq offerMakerId) and (table.offerReceiverProductId eq productId))
            .firstOrNull()?.let(::toModel)
    }

    suspend fun getOfferReceived(offerReceiverId: Long, productId: Long) = suspendTransaction {
        dao.find((table.offerReceiverId eq offerReceiverId) and (table.offerReceiverProductId eq productId))
            .firstOrNull()?.let(::toModel)
    }


    suspend fun getOffersMade(offerMakerId: Long, pageNo: Int, pageSize: Int) =
        suspendTransaction {
            var query = table.selectAll().where(table.offerMakerId eq offerMakerId)
            query = if (pageNo > 1) {    // 2..20..3..40..4..60
                val offset = ((pageSize * pageNo) - pageSize).toLong()
                query.offset(offset).limit(pageSize)
            } else query.limit(pageSize)

            query.map(::toModel)

        }

    suspend fun getOffersReceived(offerReceiverId: Long, pageNo: Int, pageSize: Int) =
        suspendTransaction {
            var query = table.selectAll().where(table.offerReceiverId eq offerReceiverId)
            query = if (pageNo > 1) {    // 2..20..3..40..4..60
                val offset = ((pageSize * pageNo) - pageSize).toLong()
                query.offset(offset).limit(pageSize)
            } else query.limit(pageSize)

            query.map(::toModel)

        }


    suspend fun acceptOffer(id: Long, approved: Boolean) = suspendTransaction {
        dao.findByIdAndUpdate(id) { offer ->
            offer.accepted = approved
        }?.also {
            exposedLogger.info("offer approved by seller =  ${it.accepted}")
            it.accepted
        } ?: false
    }

    private fun toModel(offerDao: OfferDao) = offerDao.run {
        Offer(
            id = id.value,
            offerMakerId = offerMakerId,
            offerReceiverId = offerReceiverId,
            offerReceiverProductId = offerReceiverProductId,
            offeredProductIds = Json.decodeFromString(offeredProductIds),
            extraPrice = extraPrice,
            accepted = accepted,
            completed = completed
        )
    }

    private fun toModel(row: ResultRow) = Offer(
        id = row[table.id].value,
        offerMakerId = row[table.offerMakerId],
        offerReceiverId = row[table.offerReceiverId],
        offerReceiverProductId = row[table.offerReceiverProductId],
        offeredProductIds = Json.decodeFromString(row[table.offeredProductIds]),
        extraPrice = row[table.extraPrice],
        accepted = row[table.accepted],
        completed = row[table.completed]
    )

}