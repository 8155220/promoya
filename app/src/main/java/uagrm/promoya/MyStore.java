package uagrm.promoya;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import uagrm.promoya.Fragment.Store.StoreCategoryFragment;
import uagrm.promoya.Fragment.Store.StoreHomeFragment;
import uagrm.promoya.Fragment.Store.StoreOfferFragment;

public class MyStore extends BaseActivity {

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
        getLayoutInflater().inflate(R.layout.activity_mystore, contentFrameLayout);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottonNavigationView);

        StoreHomeFragment storeHomeFragment = new StoreHomeFragment();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content,
                storeHomeFragment,"Inicio");
        fragmentTransaction.commit();

        onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction;
                switch (item.getItemId())
                {
                    case R.id.bottonNavigation_home:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        StoreHomeFragment storeHomeFragment = new StoreHomeFragment();
                        fragmentTransaction.replace(R.id.content,
                                storeHomeFragment,"Inicio");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.bottonNavigation_category:
                        StoreCategoryFragment storeCategoryFragment = new StoreCategoryFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content,
                                storeCategoryFragment,"Categorias");
                        fragmentTransaction.commit();
                        return true;
                        case R.id.bottonNavigation_offer:
                        StoreOfferFragment storeOfferFragment = new StoreOfferFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content,
                                storeOfferFragment,"Ofertas");
                        fragmentTransaction.commit();
                        return true;
                }
                return false;
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

    }

}
