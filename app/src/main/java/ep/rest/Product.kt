package ep.rest

import java.io.Serializable

data class Product(
        val id: Int = 0,
        val title: String = "",
        val size: Int = 0,
        val company: String = "",
        val color: String = "",
        val description: String = "",
        val price: Double = 0.0) : Serializable
