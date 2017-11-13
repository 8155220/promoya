package uagrm.promoya.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uagrm.promoya.BaseActivity;
import uagrm.promoya.Common.Common;
import uagrm.promoya.Login;
import uagrm.promoya.Model.User;


/**
 * Created by DhytoDev on 3/12/17.
 */

public class Utils<Data> {

    public static Intent setIntent(Context context, Class destination) {

        Intent intent = new Intent(context, destination);
        context.startActivity(intent);

        return intent ;
    }
    public static Intent setIntent(Context context, Class destination,boolean FLAG_ACTIVITY_CLEAR_TOP ) {

        Intent intent = new Intent(context, destination);
        if(FLAG_ACTIVITY_CLEAR_TOP)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
        return intent ;
    }

    public Intent setIntentExtra(Context context, Class destination, String key, Data data) {
        Intent intent = new Intent(context, destination);
        intent.putExtra(key, (Parcelable) data);
        context.startActivity(intent);

        return intent ;
    }

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void showMessage(View v, String message){
        Snackbar.make(v,message, Snackbar.LENGTH_LONG).show();
    }
    /*public static Intent logOutIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        return intent ;
    }*/

    public static void sendNewUserInfoDatabase(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("users");
        mUser.child(firebaseUser.getUid()).setValue(new User(firebaseUser));
    }
    public static FirebaseUser getFirebaseUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser;
    }



}
