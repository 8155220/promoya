package uagrm.promoya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.Arrays;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Model.User;
import uagrm.promoya.utils.Utils;

public class Login extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //onSignedInInitialize(user.getDisplayName());
                    Common.currentUser = user;
                    Intent myStore = new Intent(Login.this,Home.class);
                    myStore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myStore);
                    finish();
                    //Toast.makeText(Login.this,"your are now sign in ",Toast.LENGTH_SHORT);
                }else  {
                    //onSignedOutCleanedup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.mipmap.promoyalogowhite)
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    //new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    //  new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build())
                                            //  new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build())
                                    )
                                    .setTheme(R.style.GreenTheme)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener!=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                userRegistered(); //verifica que este en la base de datos y si no esta lo envia
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "SIgn in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*private boolean userRegistered() {
        final ProgressDialog mDialog = new ProgressDialog(Login.this);
        mDialog.setMessage("Por favor espere....");
        mDialog.show();
        final boolean[] flag = {false};
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("users");
        mUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(firebaseUser.getUid()))
                {
                    Common.user = dataSnapshot.getValue(User.class);
                    mDialog.dismiss();
                    flag[0] = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
        return flag[0];
    }*/

    public void userRegistered() {
        if(mDialog==null)mDialog = new ProgressDialog(Login.this);

        mDialog.setMessage("Por favor espere....");
        mDialog.show();
        //final boolean[] flag = {false};
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("users");
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(firebaseUser.getUid())) {
                    Common.user = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                    mDialog.dismiss();
                    //flag[0] = true;
                } else {
                    Utils.sendNewUserInfoDatabase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
        //return flag[0];
    } //Esta pele falta modificar , tambien de loginactivity


}
