package uagrm.promoya.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

/*import uagrm.promoya.Model.Request;
import uagrm.promoya.Model.User;
import uagrm.promoya.Remote.APIService;
import uagrm.promoya.Remote.FCMRetrofitClient;
import uagrm.promoya.Remote.IGeoCoordinates;
import uagrm.promoya.Remote.RetrofitClient;*/

/**
 * Created by Shep on 10/25/2017.
 */

public class Common {
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

    public static String convertCodeToStatus(String code)
    {
       if(code.equals("0"))
           return "Pendiente";
        else if(code.equals("1"))
           return "En camino";
       else
           return "Enviado";
    }
    /*public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }*/

    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatrix= new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;


    }
    /*public static APIService getFCMService()
    {
        return FCMRetrofitClient.getClient(BASE_URL).create(APIService.class);
    }*/
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
    public static String getTiempoOferta(long date)
    {
        if(date-System.currentTimeMillis()<3600000)
        {
            return "hace "+(System.currentTimeMillis()-date)/60000+" minutos";
        }
        else if(date-System.currentTimeMillis()<86400000){
            return "hace "+(date -System.currentTimeMillis())/3600000+" Horas";
        }
        else if(date -System.currentTimeMillis()<604800000){
            return "hace "+(date-System.currentTimeMillis())/86400000+" Dias";
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


}
