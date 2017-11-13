package uagrm.promoya;

/**
 * Created by ravi on 3/8/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;

import java.sql.SQLOutput;

import uagrm.promoya.Common.Common;


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
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //cargando item selected
        /*if(getIntent()!= null)
        {
            itemSelected = getIntent().getIntExtra("itemSelected",0);
            navigationView.getMenu().getItem(itemSelected).setChecked(true);
        }*/ /*else {
            itemSelected = 0;
            navigationView.getMenu().getItem(itemSelected).setChecked(true);
        }*/

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        loadUserData();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_menu_item_home:
                        if(itemSelected==1)
                        {
                            Intent dash = new Intent(getApplicationContext(), Home.class);
                            //item.setChecked(true);
                            dash.putExtra("itemSelected",0);
                            dash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(dash);
                            finish();
                            drawerLayout.closeDrawers();
                            return true;
                        }
                        break;
                        case R.id.navigation_menu_item_my_store:
                        if(itemSelected==0)
                        {
                            Intent myStore = new Intent(getApplicationContext(), MyStore.class);
                            //item.setChecked(true);
                            myStore.putExtra("itemSelected",1);
                            myStore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(myStore);
                            finish();
                            drawerLayout.closeDrawers();
                            return true;
                        }
                        break;
                    case R.id.navigation_menu_item_logout:
                        Toast.makeText(getBaseContext(),"Touch",Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(getApplicationContext(),Login.class);
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
                //return false;
            }
        });

    }

    private void loadUserData() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        navHeaderProfilePhoto = (ImageView) navHeader.findViewById(R.id.img_profile);
        navHeaderName = (TextView) navHeader.findViewById(R.id.name);
        navHeaderEmail = (TextView) navHeader.findViewById(R.id.email);
        Glide.with(this.getBaseContext()).load(Common.currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform())
                .into(navHeaderProfilePhoto);
        navHeaderName.setText(Common.currentUser.getDisplayName());
        navHeaderEmail.setText(Common.currentUser.getEmail());
    }

    public void logout(){
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
}