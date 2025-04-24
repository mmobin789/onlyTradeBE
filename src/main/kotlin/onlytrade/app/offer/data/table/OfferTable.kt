package onlytrade.app.offer.data.table

import org.jetbrains.exposed.dao.id.LongIdTable

object OfferTable : LongIdTable("offer") {
    val userId = long("userId").uniqueIndex()
    val productId = long("productId").uniqueIndex()
    val price = double("price")
    val approved = bool("approved").default(false)
}