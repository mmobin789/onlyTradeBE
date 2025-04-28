package onlytrade.app.offer.data.dao

import onlytrade.app.offer.data.table.OfferTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OfferDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OfferDao>(OfferTable)

    var offerMakerId by OfferTable.offerMakerId
    var offerReceiverId by OfferTable.offerReceiverId
    var productIds by OfferTable.productIds
    var price by OfferTable.extraPrice
    var approved by OfferTable.approved
}