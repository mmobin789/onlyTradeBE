package onlytrade.app.offer.data.dao

import onlytrade.app.offer.data.table.OfferTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OfferDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OfferDao>(OfferTable)

    var offerMakerId by OfferTable.offerMakerId
    var offerReceiverId by OfferTable.offerReceiverId
    var offeredProductIds by OfferTable.offeredProductIds
    var offerReceiverProductId by OfferTable.offerReceiverProductId
    var extraPrice by OfferTable.extraPrice
    var accepted by OfferTable.accepted
    var completed by OfferTable.completed
}