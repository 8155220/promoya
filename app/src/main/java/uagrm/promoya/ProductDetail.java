package uagrm.promoya;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import cn.iwgang.countdownview.CountdownView;
import uagrm.promoya.Chat.ChatDetail.ThreadActivity;
import uagrm.promoya.Common.Common;
import uagrm.promoya.Common.ImageSlider.ViewPagerAdapter;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Comment;
import uagrm.promoya.Model.Product;
import uagrm.promoya.ViewHolder.CommentViewHolder;
import uagrm.promoya.ViewHolder.ProductViewHolder;

public class ProductDetail extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    public static final String PRODUCT_CHILD = "Products";
    public static String KEY_PRODUCT = "PRODUCT";
    private ActionBar actionBar;

    TextView product_name, product_price, product_description,product_date;
    Button btn_message;
    ImageView product_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ElegantNumberButton numberButton;

    FirebaseDatabase database;
    DatabaseReference productsComments;

    Product currentProduct;

    //VIEWPAGER
    protected View view;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount; //depende la cantidad de imagenes en el adaptador
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;

    //countdown
    CountdownView mCvCountdownView;

    //Comments
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Comment,CommentViewHolder> commentAdapter;

    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private Button button_post_comment;
    private EditText field_comment_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        //Firebase
        database = FirebaseDatabase.getInstance().getInstance();
        productsComments = database.getReference("product-comments");

        //getIntent
        if (getIntent() != null)
            currentProduct = (Product) getIntent().getExtras().getSerializable(KEY_PRODUCT);

        initToolbar();

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        product_description = (TextView) findViewById(R.id.product_description);
        product_name = (TextView) findViewById(R.id.product_name);
        product_price = (TextView) findViewById(R.id.product_price);
        product_date =  (TextView) findViewById(R.id.product_date);
        mCvCountdownView = (CountdownView)findViewById(R.id.count_down);
        btn_message = (Button)findViewById(R.id.btn_message);

        //commets
        recyclerView = (RecyclerView)findViewById(R.id.recycler_comments);
        button_post_comment = (Button)findViewById(R.id.button_post_comment);
        field_comment_text = (EditText)findViewById(R.id.field_comment_text);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.transparentBlack));
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
        //escuchador
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKONE");
                Intent thread = new Intent(ProductDetail.this, ThreadActivity.class);
                thread.putExtra(Common.USER_ID_EXTRA, currentProduct.getStoreId()); //selectedRef.getKey() == a quien quiero hablarle
                startActivity(thread);
            }
        });

        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                //product_price
                int precio = Integer.parseInt(currentProduct.getPrice());
                product_price.setText(String.valueOf(precio*newValue));
                //Log.d("PRODUCDETAIL", String.format("oldValue: %d   newValue: %d", oldValue, newValue));
            }
        });
        button_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!field_comment_text.getText().toString().isEmpty()){
                    Comment comment = new Comment();
                    comment.setAuthor(Common.user.getDisplayName());
                    comment.setPhotoUrl(Common.user.getPhotoUrl());
                    comment.setText(field_comment_text.getText().toString());
                    productsComments.child(currentProduct.getProductId()).push().setValue(comment);
                    field_comment_text.setText("");
                    System.out.println("COMENT CANTIDAD :"+commentAdapter.getItemCount());
                    recyclerView.scrollToPosition(commentAdapter.getItemCount());
                }
            }
        });
        loadComments();


    }

    private void loadComments() {
            commentAdapter = new FirebaseRecyclerAdapter<Comment,CommentViewHolder>(
                    Comment.class
                    ,R.layout.comment_item,
                    CommentViewHolder.class,
                    productsComments.child(currentProduct.getProductId())) {

                @Override
                protected void populateViewHolder(CommentViewHolder viewHolder, final Comment model, int position) {
                    viewHolder.authorView.setText(model.getAuthor());
                    Glide.with(getBaseContext()).load(model.getPhotoUrl()).apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.photoUrl);
                    viewHolder.bodyView.setText(model.getText());
                }

            };

            commentAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(commentAdapter);

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


    private void getDetailProduct() {


        collapsingToolbarLayout.setTitle(currentProduct.getName());

        product_price.setText(currentProduct.getPrice());
        product_name.setText(currentProduct.getName());
        product_date.setText(Common.getTiempoTranscurrido(Long.parseLong(currentProduct.getDate())));
        product_description.setText(currentProduct.getDescription());
        if(currentProduct.getOfferExpire()>System.currentTimeMillis())
        {
            long tiempoRestante = currentProduct.getOfferExpire() - System.currentTimeMillis();
            mCvCountdownView.start(tiempoRestante);
        }
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
