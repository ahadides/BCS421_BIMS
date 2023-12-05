package edu.farmingdale.bcs421_bims

data class Item(
    val imageUrl: String = "",
    val barcodeNumber: String = "",
    val productName: String = "",
    val quantity: String = "",
    val location: String = ""
)
