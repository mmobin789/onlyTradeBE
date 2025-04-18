package onlytrade.app.product.data.dao

import onlytrade.app.product.data.table.ProductTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ProductDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProductDao>(ProductTable)

    var userId by ProductTable.userId
    var subcategoryId by ProductTable.subcategoryId
    var name by ProductTable.name
    var estPrice by ProductTable.estPrice
    var description by ProductTable.description
    var imageUrls by ProductTable.imageUrls

}