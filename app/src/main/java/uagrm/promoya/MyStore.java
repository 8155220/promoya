package uagrm.promoya;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import uagrm.promoya.Fragment.CategoryStoreFragment;
import uagrm.promoya.Fragment.HomeStoreFragment;
import uagrm.promoya.Model.Category;

public class MyStore extends BaseActivity {

    View view;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mystore);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_mystore, contentFrameLayout);


        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottonNavigationView);
        onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction;
                switch (item.getItemId())
                {
                    case R.id.bottonNavigation_home:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        HomeStoreFragment homeStoreFragment = HomeStoreFragment.getInstance();
                        fragmentTransaction.replace(R.id.content,
                                homeStoreFragment,"Inicio");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.bottonNavigation_category:
                        System.out.println("Entro aqui");
                        CategoryStoreFragment categoryStoreFragment = new CategoryStoreFragment();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.content,
                                categoryStoreFragment,"Categorias");
                        fragmentTransaction.commit();
                        return true;
                }
                return false;
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        /*HomeStoreFragment homeStoreFragment = HomeStoreFragment.getInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content,
                homeStoreFragment,"Inicio");
        fragmentTransaction.commit();*/





    }

    /*private class sliderAdapter extends FragmentPagerAdapter {

        final String tabs[]={"Mi Tienda", "Categorias","Ofertas"};
        public sliderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //return new HomeStoreFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }*/
}
