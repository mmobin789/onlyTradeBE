package onlytrade.app.product.data.table

import org.jetbrains.exposed.dao.id.LongIdTable


object ProductTable : LongIdTable(name = "product") {
    val userId = long("userId")
    val categoryId = long("categoryId")
    val subcategoryId = long("subcategoryId")
    val name = text("name")
    val description = text("desc")
    val estPrice = double("estPrice")
    val imageUrls = text("imageUrls")
    val approved = bool("approved").default(false)
}