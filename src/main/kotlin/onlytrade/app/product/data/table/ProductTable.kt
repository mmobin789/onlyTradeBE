package onlytrade.app.product.data.table

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime


object ProductTable : LongIdTable(name = "product") {
    val userId = long("user_id")
    val categoryId = long("category_id")
    val subcategoryId = long("subcategory_id")
    val name = text("name")
    val description = text("desc")
    val estPrice = double("est_price")
    val imageUrls = text("image_urls")
    val approved = bool("approved").default(false)
    val traded = bool("traded").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}