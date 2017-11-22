package uagrm.promoya.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uagrm.promoya.Model.Notification.MyResponse;
import uagrm.promoya.Model.Notification.Notification;
import uagrm.promoya.Model.Notification.Sender;
import uagrm.promoya.Model.Notification.SenderTopic.Data;
import uagrm.promoya.Model.Notification.SenderTopic.SenderTopic;
import uagrm.promoya.Model.Notification.SenderTopic.TopicResponse;
import uagrm.promoya.Remote.APIService;
import uagrm.promoya.Remote.RetrofitClient;

/**
 * Created by Shep on 10/25/2017.
 */

public class Common {
    //CHAT
    public static final String USER_ID_EXTRA = "userIdExtra";
    //ENDCHAT

    public static FirebaseUser currentUser;
    //public static Request currentRequest;
    private static final String BASE_URL = "https://fcm.googleapis.com/";
    public static final String UPDATE ="Actualizar";
    public static final String DELETE ="Eliminar";
    public static final String OFFER = "Ofertar";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static uagrm.promoya.Model.User user;
    public static DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    public static DatabaseReference DBSTORES = FirebaseDatabase.getInstance().getReference().child("stores");

    public static final String baseUrl = "https://maps.googleapis.com";
    public static final APIService mService = Common.getFCMService();

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info!=null)
            {
                for (int i = 0; i < info.length; i++) {
                    if(info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static String getTiempoTranscurrido(long date)
    {
        if(System.currentTimeMillis()-date<3600000)
        {
            return "hace "+(System.currentTimeMillis()-date)/60000+" minutos";
        }
        else if(System.currentTimeMillis()-date<86400000){
            return "hace "+(System.currentTimeMillis()-date)/3600000+" Horas";
        }
        else if(System.currentTimeMillis()-date<604800000){
            return "hace "+(System.currentTimeMillis()-date)/86400000+" Dias";
        }
        else {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTimeInMillis(date);
            int year = calendar.get(calendar.YEAR);
            int month = calendar.get(calendar.MONTH);
            int dia = calendar.get(calendar.DATE);
            return "el "+dia+"/"+month+"/"+year;
        }
    }

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static void sendNotification(String token,Notification notification) {

        Sender content = new Sender(token, notification);

        mService.sendNotification(content)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if(response.code()==200)
                        {
                            if (response.body().success == 1) {
                            }
                            else
                            {
                                //Toast.makeText(Cart.this, "Fallo !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Log.e("ERRORNOTIFICATION",t.getMessage());
                    }
                });
    }
    public static void sendNotificationTopic(String token,Data message) {

        SenderTopic content = new SenderTopic("/topics/"+token, message);

        //System.out.println("SENDERTOPIC :"+content.toString());
        mService.sendNotificationTopic(content)
                .enqueue(new Callback<TopicResponse>() {
                    @Override
                    public void onResponse(Call<TopicResponse> call, Response<TopicResponse> response) {
                        if(response.code()==200)
                        {
                            System.out.println("exito :" +response.body());

                            if (response.isSuccessful()) {
                                System.out.println("exito :" +response.body());
                            }
                            else
                            {
                                //Toast.makeText(Cart.this, "Fallo !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        System.out.println("RESPONSE : "+response.code());
                    }
                    @Override
                    public void onFailure(Call<TopicResponse> call, Throwable t) {
                        Log.e("ERRORNOTIFICATION",t.getMessage());
                    }
                });
    }


}
