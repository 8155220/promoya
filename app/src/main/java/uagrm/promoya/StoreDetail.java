package uagrm.promoya;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import uagrm.promoya.Fragment.Store.StoreCategoryFragment;
import uagrm.promoya.Fragment.Store.StoreHomeFragment;
import uagrm.promoya.Fragment.Store.StoreOfferFragment;
import uagrm.promoya.Model.Product;
import uagrm.promoya.Model.Store;

public class StoreDetail extends AppCompatActivity {
    public static String KEY_STORE = "STORE";
    View view;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;
    Store currentStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store_detail);
        //setContentView(R.layout.activity_mystore);

        if (getIntent() != null)
            currentStore = (Store) getIntent().getExtras().getSerializable(KEY_STORE);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(currentStore.getDisplayName());
        //
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_store_detail, null);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottonNavigationView);

        StoreHomeFragment storeHomeFragment = new StoreHomeFragment(currentStore);
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
                        StoreHomeFragment storeHomeFragment = new StoreHomeFragment(currentStore);
                        fragmentTransaction.replace(R.id.content,
                                storeHomeFragment,"Inicio");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.bottonNavigation_category:
                        StoreCategoryFragment storeCategoryFragment = new StoreCategoryFragment(currentStore);
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
