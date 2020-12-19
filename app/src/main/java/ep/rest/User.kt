package ep.rest

import java.io.Serializable

data class User(
        val id: Int = 0,
        val email: String = "",
        val name: String = "",
        val surname: String = "",
        val token: String = "") : Serializable