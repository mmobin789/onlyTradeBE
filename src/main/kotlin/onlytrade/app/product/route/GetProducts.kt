package onlytrade.app.product.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.util.logging.Logger
import onlytrade.app.product.ProductsRepository
import onlytrade.app.viewmodel.product.repository.data.remote.response.GetProductsResponse

fun Route.getProducts(logger: Logger) {
    get("/products") {
        val params = call.queryParameters
        val pageSize = params["size"]?.toIntOrNull() ?: 20
        val pageNo = params["page"]?.toIntOrNull() ?: 1
        val userId = params["uid"]?.toIntOrNull()

        logger.info("GetProductService: pageSize=$pageSize, pageNo=$pageNo, userId=$userId")

        val products = ProductsRepository.getProducts(
            pageNo = pageNo,
            pageSize = pageSize,
            userId = userId
        )
        val statusCode = HttpStatusCode.OK
        call.respond(statusCode, GetProductsResponse(statusCode.value, products))
    }
}