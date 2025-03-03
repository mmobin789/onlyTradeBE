package onlytrade.app.product.add.data.table

import org.jetbrains.exposed.dao.id.IntIdTable


object ProductTable : IntIdTable(name = "product") {
    val uid = integer("uid")
    val name = text("name", )
    val description = text("desc")
    val estPrice = integer("estPrice")
    val imageUrls = text("imageUrls")
}