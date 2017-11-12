package uagrm.promoya.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Product;
import uagrm.promoya.ProductDetail;
import uagrm.promoya.ProductList;
import uagrm.promoya.R;
import uagrm.promoya.ViewHolder.ProductViewHolder;

/**
 * Created by Mako on 1/13/2017.
 */
public class OfferStoreFragment extends Fragment{
    public static final String PRODUCT_CHILD = "Products";

    //Model
    Product newProduct;

    //FIREBASE
    FirebaseDatabase db;
    DatabaseReference products;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    //Recycler
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    View rootView;

    public OfferStoreFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_offer_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView=view;
        //Init Firebase
        db = FirebaseDatabase.getInstance();
        //products = db.getReference(PRODUCT_CHILD).child(Common.currentUser.getUid());
        products = db.getReference(PRODUCT_CHILD);

        //tolbar
        //setHasOptionsMenu(true);

        //Init View
        recycler_menu = (RecyclerView)view.findViewById(R.id.recycler_product);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();


    }
    private void loadMenu() {
        Query query = products;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                    // dataSnapshot.getValue(Product.class).getOfferExpire()
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter=new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product_item,
                ProductViewHolder.class,
                products
        ) {

            @Override
            public void onBindViewHolder(ProductViewHolder viewHolder, int position) {
                long offerExpire = Long.parseLong(adapter.getItem(position).getOfferExpire());
                if(offerExpire>System.currentTimeMillis())
                {
                    adapter.getRef(position).removeValue();
                    super.onBindViewHolder(viewHolder, position);
                }
            }

            @Override
            protected void populateViewHolder(ProductViewHolder viewHolder, final Product model, int position) {
                long offerExpire = Long.parseLong(model.getOfferExpire());
                System.out.println("POPULANDO-> offerExpire:"+offerExpire +"CurrentTimeMs :"+System.currentTimeMillis());
                if(offerExpire>System.currentTimeMillis())
                {
                    viewHolder.product_name.setText(model.getName());
                    Picasso.with(getActivity().getApplicationContext()).load(model.getImage())
                            .into(viewHolder.product_image);
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //Get ProductId and send to new Activity
                            Intent producDetail = new Intent(getContext(),ProductDetail.class);
                            //Because ProductId is key, so we just get key of this item
                            // productList.putExtra("ProductId",adapter.getRef(position).getKey());//ORIGINAL
                            producDetail.putExtra("PRODUCT",model);
                            //productList.putExtra("currentProduct",model);
                            startActivity(producDetail);
                        }
                    });
                }
            }
        };
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }

}
