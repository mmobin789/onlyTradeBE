package onlytrade.app.product.data.table

import org.jetbrains.exposed.dao.id.IntIdTable


object ProductTable : IntIdTable(name = "product") {
    val userId = integer("userId")
    val subcategoryId = integer("subcategoryId")
    val name = text("name", )
    val description = text("desc")
    val estPrice = double("estPrice")
    val imageUrls = text("imageUrls")
}