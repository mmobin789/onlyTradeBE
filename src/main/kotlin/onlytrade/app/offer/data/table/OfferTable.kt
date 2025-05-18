package onlytrade.app.offer.data.table

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime


object OfferTable : LongIdTable("offer") {
    val offerMakerId = long("offer_maker_id")
    val offerReceiverId = long("offer_receiver_id")
    val offeredProductIds = text("offered_product_ids")
    val offerReceiverProductId = long("offer_receiver_product_id")
    val extraPrice = double("extra_price")
    val accepted = bool("accepted").default(false)
    val completed = bool("completed").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}