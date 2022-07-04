package space.kis.myproject.domain

import space.kis.myproject.util.Resource

interface QuizRepository {

    suspend fun getAllCountries(): Resource<List<CountryModel>>
}