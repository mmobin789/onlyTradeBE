package onlytrade.app.product.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.util.logging.Logger
import onlytrade.app.product.ProductRepository
import onlytrade.app.viewmodel.product.repository.data.remote.response.GetProductsResponse

fun Route.getProducts(logger: Logger) {
    get("/products") {
        val params = call.queryParameters
        val pageSize = params["size"]?.toIntOrNull() ?: 20
        val pageNo = params["page"]?.toIntOrNull() ?: 1
        val userId = params["uid"]?.toLongOrNull()

        logger.info("GetProductsService: pageSize=$pageSize, pageNo=$pageNo, userId=$userId")

        val products = ProductRepository.getProducts(
            pageNo = pageNo,
            pageSize = pageSize,
            userId = userId
        )
        if (products.isNotEmpty()) {
            val statusCode = HttpStatusCode.PartialContent
            call.respond(statusCode, GetProductsResponse(statusCode.value, products))
        } else {
            val statusCode = HttpStatusCode.NotFound
            call.respond(
                statusCode,
                GetProductsResponse(statusCode.value, error = "No products found.")
            )
        }
    }
}