package onlytrade.app.product.data.dao

import onlytrade.app.product.data.table.ProductTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class ProductDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ProductDao>(ProductTable)

    var userId by ProductTable.userId
    var categoryId by ProductTable.categoryId
    var subcategoryId by ProductTable.subcategoryId
    var name by ProductTable.name
    var estPrice by ProductTable.estPrice
    var description by ProductTable.description
    var imageUrls by ProductTable.imageUrls
    var traded by ProductTable.traded

}