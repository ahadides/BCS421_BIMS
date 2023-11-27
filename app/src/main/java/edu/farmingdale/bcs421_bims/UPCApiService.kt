package edu.farmingdale.bcs421_bims

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UPCApiService {
    @GET("lookup")
    fun lookupUPC(@Query("upc") upcCode: String): Call<ProductResponse>
}
