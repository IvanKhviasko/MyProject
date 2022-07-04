package space.kis.myproject.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.kis.myproject.domain.QuizRepository
import space.kis.myproject.domain.QuizRepositoryImpl
import space.kis.myproject.remote.CountriesAPI
import space.kis.myproject.util.Constants.BASE_URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCountriesApi(): CountriesAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(CountriesAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideQuizRepository(
        api: CountriesAPI
    ) = QuizRepositoryImpl(api) as QuizRepository
}