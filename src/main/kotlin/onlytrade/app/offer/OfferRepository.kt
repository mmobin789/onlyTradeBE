package onlytrade.app.offer

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import onlytrade.app.db.suspendTransaction
import onlytrade.app.offer.data.dao.OfferDao
import onlytrade.app.offer.data.table.OfferTable
import onlytrade.app.viewmodel.product.offer.repository.data.db.Offer
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.selectAll

object OfferRepository {
    val table = OfferTable
    val dao = OfferDao

    suspend fun addOffer(offer: Offer) = suspendTransaction {
        dao.new {
            this.offerMakerId = offer.offerMakerId
            this.offerReceiverId = offer.offerReceiverId
            this.offerReceiverProductId = offer.offerReceiverProductId
            this.offeredProductIds = Json.encodeToString(offer.offeredProductIds)
            this.extraPrice = offer.extraPrice
        }.id.value.also {
            exposedLogger.info("Offer Added :$it")
        }
    }

    suspend fun getOffersMade(userId: Long, pageNo: Int, pageSize: Int) =
        suspendTransaction {
            val query = table.selectAll().limit(pageSize).where(table.offerMakerId eq userId)
            if (pageNo > 1) {    // 2..20..3..40..4..60
                val offset = ((pageSize * pageNo) - pageSize).toLong()
                query.offset(offset)
            }

            query.map { row ->
                Offer(
                    id = row[table.id].value,
                    offerMakerId = row[table.offerMakerId],
                    offerReceiverId = row[table.offerReceiverId],
                    offerReceiverProductId = row[table.offerReceiverProductId],
                    offeredProductIds = Json.decodeFromString(row[table.offeredProductIds]),
                    extraPrice = row[table.extraPrice],
                    approved = row[table.approved]
                )

            }

        }

    suspend fun getOffersReceived(userId: Long, pageNo: Int, pageSize: Int) =
        suspendTransaction {
            val query = table.selectAll().limit(pageSize).where(table.offerReceiverId eq userId)
            if (pageNo > 1) {    // 2..20..3..40..4..60
                val offset = ((pageSize * pageNo) - pageSize).toLong()
                query.offset(offset)
            }

            query.map { row ->
                Offer(
                    id = row[table.id].value,
                    offerMakerId = row[table.offerMakerId],
                    offerReceiverId = row[table.offerReceiverId],
                    offerReceiverProductId = row[table.offerReceiverProductId],
                    offeredProductIds = Json.decodeFromString(row[table.offeredProductIds]),
                    extraPrice = row[table.extraPrice],
                    approved = row[table.approved]
                )

            }

        }


    suspend fun approveOffer(id: Long, approved: Boolean) = suspendTransaction {
        dao.findByIdAndUpdate(id) { offer ->
            offer.approved = approved
        }?.also {
            exposedLogger.info("offer approved by seller =  ${it.approved}")
            it.approved
        } ?: false
    }

}