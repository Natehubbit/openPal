package com.walkerstechbase.openpal.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "content-type:application/json",
            "Authorization:key=AAAAGoZLLWc:APA91bHxo5izhujb3LuDiI-JRsG73OogV-LZqnwxGrF6SexowVXw7UOM27ABRqBwYoqGBgdmCdQ0ckDPDYI9z_FJKGwd9SOGTHJNRZdafhj1PZMmyT7mB17frYovmlN8lyuV2EXY9142"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
