package onlytrade.app.offer.data.dao

import onlytrade.app.offer.data.table.OfferTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class OfferDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OfferDao>(OfferTable)

    var userId by OfferTable.userId
    var productId by OfferTable.productId
    var price by OfferTable.price
    var approved by OfferTable.approved
}