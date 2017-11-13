package uagrm.promoya;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import uagrm.promoya.Fragment.Home.HomeOffersFragment;
import uagrm.promoya.Fragment.Home.HomeProductFragment;
import uagrm.promoya.Fragment.Home.HomeStoresFragment;
import uagrm.promoya.Fragment.Store.StoreCategoryFragment;
import uagrm.promoya.Fragment.Store.StoreOfferFragment;

/**
 * Created by Shep on 11/12/2017.
 */

public class Home extends BaseActivity{
    View view;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mystore);
        if(getIntent()!= null)
        {
            itemSelected = getIntent().getIntExtra("itemSelected",0);
            navigationView.getMenu().getItem(itemSelected).setChecked(true);
        }
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);


        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottonNavigationView);

        FragmentTransaction fragmentTransaction;

        HomeProductFragment homeProductFragment = new HomeProductFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, homeProductFragment,"Productos");
        fragmentTransaction.commit();

        onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction;
                switch (item.getItemId())
                {
                    case R.id.bottonNavigation_products:
                        HomeProductFragment homeProductFragment = new HomeProductFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content, homeProductFragment,"Productos");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.bottonNavigation_stores:
                        HomeStoresFragment homeStoresFragment = new HomeStoresFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content,
                                homeStoresFragment,"Tiendas");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.bottonNavigation_offers:
                        HomeOffersFragment homeOffersFragment = new HomeOffersFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content,
                                homeOffersFragment,"Ofertas");
                        fragmentTransaction.commit();
                        return true;
                }
                return false;
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);






    }
}
