package ru.dvfu.appliances.model.repository.entity.notifications

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.dvfu.appliances.model.repository.entity.notifications.NotificationConstants.CONTENT_TYPE
import ru.dvfu.appliances.model.repository.entity.notifications.NotificationConstants.SERVER_KEY

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}