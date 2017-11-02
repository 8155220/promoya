package uagrm.promoya;

import android.media.Rating;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Common.ImageSlider.ViewPagerAdapter;
import uagrm.promoya.Model.Product;

public class ProductDetail extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    public static final String PRODUCT_CHILD = "Products";
    public static String KEY_PRODUCT = "PRODUCT";
    private ActionBar actionBar;

    TextView product_name, product_price, product_description;
    ImageView product_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton numberButton;

    FirebaseDatabase database;
    DatabaseReference products;

    Product currentProduct;

    //VIEWPAGER
    protected View view;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount; //depende la cantidad de imagenes en el adaptador
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        //Firebase
        database = FirebaseDatabase.getInstance().getInstance();
        products = database.getReference(PRODUCT_CHILD);

        //init view
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        //tool

        //getIntent
        if (getIntent() != null)
            currentProduct = (Product) getIntent().getExtras().getSerializable(KEY_PRODUCT);

        initToolbar();


        product_description = (TextView) findViewById(R.id.product_description);
        product_name = (TextView) findViewById(R.id.product_name);
        product_price = (TextView) findViewById(R.id.product_price);
        //product_image = (ImageView) findViewById(R.id.img_product);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get Product Id from Intent
         if (!currentProduct.getProductId().isEmpty()) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                getDetailProduct();
                //getRatingProduct(productId);
                //IMAGESLIDER
                intro_images = (ViewPager) findViewById(R.id.pager_introduction);
                pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
                mAdapter = new ViewPagerAdapter(ProductDetail.this, currentProduct.getListImage());
                intro_images.setAdapter(mAdapter);
                intro_images.setCurrentItem(0);
                intro_images.setOnPageChangeListener(this);
                setUiPageViewController();
            } else {
                Toast.makeText(ProductDetail.this, "verifique su conexion a internet", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void setUiPageViewController() {
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            pager_indicator.addView(dots[i], params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    /*private void getDetailProduct(String productId) {
        products.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentProduct = dataSnapshot.getValue(Product.class);

                //Set Image
                *//*Picasso.with(getBaseContext()).load(currentProduct.getImage())
                        .into(product_image);*//*
                collapsingToolbarLayout.setTitle(currentProduct.getName());

                product_price.setText(currentProduct.getPrice());
                product_name.setText(currentProduct.getName());

                product_description.setText(currentProduct.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
    private void getDetailProduct() {


        collapsingToolbarLayout.setTitle(currentProduct.getName());

        product_price.setText(currentProduct.getPrice());
        product_name.setText(currentProduct.getName());

        product_description.setText(currentProduct.getDescription());

    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //actionBar.setTitle(friend.getName());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
