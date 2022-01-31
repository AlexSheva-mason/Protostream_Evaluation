package alexei.sevcisen.android.protostreamevaluation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface MovieDataRetrofitService {

    @GET("/v6/interview")
    suspend fun getMovieData(): List<Movie>

}

object RetrofitServiceProvider {

    private const val BASE_URL = "https://content-cache.watchcorridor.com/"
    val apiService = getRetrofitService()

    private fun getRetrofitService(): MovieDataRetrofitService {
        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        return builder.build().create(MovieDataRetrofitService::class.java)
    }

}
