package ep.rest

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object SneakersService {

    interface RestApi {

        companion object {
            // AVD emulator
            const val URL = "http://172.22.200.86/netbeans/ep-seminarska/api/"
            // Genymotion
            // const val URL = "http://10.0.3.2:8080/netbeans/mvc-rest/api/"
        }

        @GET("products")
        fun getAll(): Call<List<Product>>

        @GET("products/{id}")
        fun get(@Path("id") id: Int): Call<Product>

        @FormUrlEncoded
        @POST("enroll")
        fun login(@Field("email") email: String,
                   @Field("password") password: String): Call<Void>

        @DELETE("products/{id}")
        fun delete(@Path("id") id: Int): Call<Void>

        @FormUrlEncoded
        @POST("products")
        fun insert(@Field("author") author: String,
                   @Field("title") title: String,
                   @Field("price") price: Double,
                   @Field("year") year: Int,
                   @Field("description") description: String): Call<Void>

        @FormUrlEncoded
        @PUT("products/{id}")
        fun update(@Path("id") id: Int,
                   @Field("author") author: String,
                   @Field("title") title: String,
                   @Field("price") price: Double,
                   @Field("year") year: Int,
                   @Field("description") description: String): Call<Void>
    }

    val instance: RestApi by lazy {
        val retrofit = Retrofit.Builder()
                .baseUrl(RestApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofit.create(RestApi::class.java)
    }
}
