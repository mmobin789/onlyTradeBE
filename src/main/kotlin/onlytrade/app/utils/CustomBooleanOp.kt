package onlytrade.app.utils

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryBuilder

class CustomBooleanOp(private val sql: String) : Op<Boolean>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append(sql)
    }
}