package uagrm.promoya.Remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import uagrm.promoya.Model.Notification.MyResponse;
import uagrm.promoya.Model.Notification.Sender;
import uagrm.promoya.Model.Notification.SenderTopic.SenderTopic;
import uagrm.promoya.Model.Notification.SenderTopic.TopicResponse;

/**
 * Created by Shep on 10/29/2017.
 */

public interface APIService {
    @Headers(
        {
            "Content-Type:application/json",
            "Authorization:key=AIzaSyCiooxSaMkXoEOQzIMby9RvW6BTVX4EMXs"
        }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AIzaSyCiooxSaMkXoEOQzIMby9RvW6BTVX4EMXs"
            }
    )
    @POST("fcm/send")
    Call<TopicResponse> sendNotificationTopic(@Body SenderTopic body);
}
