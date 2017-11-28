package uagrm.promoya;

/**
 * Created by ravi on 3/8/2017.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.UUID;

import uagrm.promoya.Chat.HomeChat.ChatHomeActivity;
import uagrm.promoya.Common.Common;
import uagrm.promoya.Common.FirebaseDatabaseHelper;
import uagrm.promoya.Firebase.MyFirebaseIdService;
import uagrm.promoya.Model.Product;
import uagrm.promoya.Model.Store;
import uagrm.promoya.Model.User;
import uagrm.promoya.Watson.Conversation.ChatBotActivity;
import uagrm.promoya.utils.Utils;


public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    int itemSelected;
    private View navHeader;

    private ImageView navHeaderProfilePhoto;
    private TextView navHeaderName;
    private TextView navHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //LoadFirebaseDatabaseHelper
        FirebaseDatabaseHelper firebaseDatabaseHelper = FirebaseDatabaseHelper.getInstance();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        loadUserData();
        if (Common.user == null) {
            userRegistered();            //Obtiene los datos del usuario registrado dela db
        }else {
            MyFirebaseIdService.refresToken();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_menu_item_home:
                        if (itemSelected == 1) {
                            Intent dash = new Intent(getApplicationContext(), Home.class);
                            //item.setChecked(true);
                            dash.putExtra("itemSelected", 0);
                            dash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(dash);
                            finish();
                            drawerLayout.closeDrawers();
                            return true;
                        }
                        break;
                    case R.id.navigation_menu_item_my_store:
                        if (itemSelected == 0) {
                            if (Common.user != null) {
                                if (Common.user.getHasStore() == 1) {
                                    Intent myStore = new Intent(getApplicationContext(), MyStore.class);
                                    //item.setChecked(true);
                                    myStore.putExtra("itemSelected", 1);
                                    myStore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(myStore);
                                    finish();
                                    drawerLayout.closeDrawers();
                                } else {
                                    showEnableStoreDialog();
                                }
                            }
                            return true;
                        }
                        break;
                    case R.id.navigation_menu_item_statistics:
                            if (Common.user != null) {
                                    Intent statistics = new Intent(getApplicationContext(), StatisticsActivity.class);
                                    //item.setChecked(true);
                                    statistics.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(statistics);
                                overridePendingTransitionEnter();

                                //finish();
                                    drawerLayout.closeDrawers();
                                    return true;
                            }
                            else break;
                    case R.id.navigation_menu_item_message:
                            if (Common.user != null) {
                                    Intent message = new Intent(getApplicationContext(), ChatHomeActivity.class);
                                    //item.setChecked(true);
                                    message.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(message);
                                    //finish();
                                    drawerLayout.closeDrawers();
                                    return true;
                            }
                            else break;
                    case R.id.navigation_menu_item_chatbot:
                                    Intent myStore = new Intent(getApplicationContext(), ChatBotActivity.class);
                                    //item.setChecked(true);
                                    //myStore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(myStore);
                                    //finish();
                                    drawerLayout.closeDrawers();
                    break;
                    case R.id.navigation_menu_item_logout:
                        Toast.makeText(getBaseContext(), "Hasta la vista ...", Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(getApplicationContext(), Login.class);
                        startActivity(login);
                        logout();
                        finish();
                        break;
                    /*case R.id.nav_about_us:
                        Intent anIntent = new Intent(getApplicationContext(), AboutUS.class);
                        startActivity(anIntent);
//                        finish();
                        drawerLayout.closeDrawers();
                        break;*/
                }
                //item.setChecked(true);
                drawerLayout.closeDrawers();
                return false;
            }
        });

    }

    private void showEnableStoreDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(BaseActivity.this);
        alertDialog.setTitle("Habilitar Tienda");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialog_enable_store = inflater.inflate(R.layout.dialog_enable_store, null);
        alertDialog.setView(dialog_enable_store);
        alertDialog.setIcon(R.drawable.ic_menu_store); //cambiar
        alertDialog.setPositiveButton("Crear Tienda", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                /*Snackbar.make(getWindow().getDecorView(), "Click En Aceptar", Snackbar.LENGTH_SHORT)
                        .show();*/
                //showCreateStoreDialog();
                Intent i = new Intent(getApplicationContext(),CreateStore.class);
                startActivity(i);
            }

        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadUserData() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        navHeaderProfilePhoto = (ImageView) navHeader.findViewById(R.id.img_profile);
        navHeaderName = (TextView) navHeader.findViewById(R.id.name);
        navHeaderEmail = (TextView) navHeader.findViewById(R.id.email);
        if(this.getBaseContext()!=null)
            Glide.with(this.getBaseContext()).load(Common.currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform())
                    .into(navHeaderProfilePhoto);
        navHeaderName.setText(Common.currentUser.getDisplayName());
        navHeaderEmail.setText(Common.currentUser.getEmail());
    }

    public void logout() {
        AuthUI.getInstance().signOut(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //actionBarDrawerToggle.syncState();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            //Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            Snackbar.make(getWindow().getDecorView(), R.string.press_again_exit_app, Snackbar.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    public void userRegistered() {
        final ProgressDialog mDialog = new ProgressDialog(BaseActivity.this);
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
    }



}