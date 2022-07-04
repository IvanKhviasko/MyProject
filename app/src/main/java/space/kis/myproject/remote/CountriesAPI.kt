package space.kis.myproject.remote

import retrofit2.Response
import retrofit2.http.GET

interface CountriesAPI {

    @GET("all")
    suspend fun getAll(): Response<List<CountryDto>>
}