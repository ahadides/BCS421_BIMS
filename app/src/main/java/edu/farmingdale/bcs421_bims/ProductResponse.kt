package edu.farmingdale.bcs421_bims

data class ProductResponse(
    val code: String,
    val total: Int,
    val offset: Int,
    val items: List<ProductItem>
)

data class ProductItem(
    val ean: String,
    val title: String,
    val images: List<String>
)
