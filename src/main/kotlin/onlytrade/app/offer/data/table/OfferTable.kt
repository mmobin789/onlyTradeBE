package onlytrade.app.offer.data.table

import org.jetbrains.exposed.dao.id.LongIdTable

object OfferTable : LongIdTable("offer") {
    val offerMakerId = long("offerMakerId").uniqueIndex()
    val offerReceiverId = long("offerReceiverId").uniqueIndex()
    val productIds = text("productId")
    val extraPrice = double("extraPrice")
    val approved = bool("approved").default(false)
}