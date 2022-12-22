package com.mndsystem.newcalendar

import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface APIS {

    @POST("2022/DATE/date.php")
    fun getCalendarData(
        @Query("year") year: String,
        @Query("mon") mon: String
    ): Call<String>

    @POST("Android/Calendar/register_schedule.php")
    fun registerSchedule(
        @Query("dat")dat:String,
        @Query("tit")tit:String,
        @Query("dam")dam:String,
        @Query("big")big:String
    ): Call<String>


    @POST("Android/Calendar/delete_schedule.php")
    fun deleteSchedule(
        @Query("sdx")sdx:String
    ): Call<String>

    @POST("Android/Calendar/update_schedule.php")
    fun updateSchedule(
        @Query("sdx")sdx:String,
        @Query("dat")dat:String,
        @Query("tit")tit:String,
        @Query("dam")dam:String,
        @Query("big")big:String
    ):Call<String>


    companion object{
        private const val url = "http://mndsystem.iptime.org:4321"

        fun create():APIS{
            val interceptor = HttpLoggingInterceptor()
                .apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor).build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(APIS::class.java)
        }
    }
}