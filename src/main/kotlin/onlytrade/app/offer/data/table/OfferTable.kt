package onlytrade.app.offer.data.table

import org.jetbrains.exposed.dao.id.LongIdTable

object OfferTable : LongIdTable("offer") {
    val offerMakerId = long("offerMakerId").uniqueIndex()
    val offerReceiverId = long("offerReceiverId").uniqueIndex()
    val offeredProductIds = text("offeredProductIds")
    val offerReceiverProductId = long("offerReceiverProductId")
    val extraPrice = double("extraPrice")
    val accepted = bool("accepted").default(false)
    val completed = bool("completed").default(false)
}