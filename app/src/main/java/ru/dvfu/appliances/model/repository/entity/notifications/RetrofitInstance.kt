package ru.dvfu.appliances.model.repository.entity.notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.dvfu.appliances.model.repository.entity.notifications.NotificationConstants.BASE_URL

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: NotificationAPI by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}