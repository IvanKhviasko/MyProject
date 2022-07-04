package space.kis.myproject.domain

import space.kis.myproject.remote.CountriesAPI
import space.kis.myproject.remote.toCountryModel
import space.kis.myproject.util.Resource
import space.kis.myproject.util.Status
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val countriesAPI: CountriesAPI
) : QuizRepository {

    override suspend fun getAllCountries(): Resource<List<CountryModel>> {
        return try {
            val response = countriesAPI.getAll()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource(Status.SUCCESS, it.filter {
                        it.name?.common != null && it.flags?.png != null
                    }.map {
                        it.toCountryModel()
                    })
                } ?: Resource(Status.ERROR, null)
            } else Resource(Status.ERROR, null)
        } catch (e: Exception) {
            Resource(Status.ERROR, null)
        }
    }
}