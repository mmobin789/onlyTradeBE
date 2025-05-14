package onlytrade.app.product.data.table

import org.jetbrains.exposed.dao.id.LongIdTable


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
}