package uagrm.promoya.Fragment.Store;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uagrm.promoya.Adapter.ProductAdapter;
import uagrm.promoya.Common.Common;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Product;
import uagrm.promoya.Model.Store;
import uagrm.promoya.ProductDetail;
import uagrm.promoya.ProductList;
import uagrm.promoya.R;
import uagrm.promoya.ViewHolder.ProductViewHolder;
import uagrm.promoya.utils.Utils;

/**
 * Created by Mako on 1/13/2017.
 */
public class StoreOfferFragment extends Fragment{
    public static final String PRODUCT_CHILD = "Products";

    //Model
    Product newProduct;

    //FIREBASE
    FirebaseDatabase db;
    DatabaseReference products;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    ProductAdapter productAdapter;

    //Recycler
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    View rootView;
    List<Product> listProducts;

    Store currentStore;

    public StoreOfferFragment() {
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        listProducts = new ArrayList<>();
        return inflater.inflate(R.layout.activity_offer_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView=view;
        //Init Firebase
        db = FirebaseDatabase.getInstance();
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

        productAdapter = new ProductAdapter(listProducts,this.getContext());
                //adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(productAdapter);
        updateList();
    }

    private void updateList() {
        products.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Product model = dataSnapshot.getValue(Product.class);
                if (model.getStoreId().equals(Utils.getFirebaseUser().getUid()) && model.getOfferExpire()>System.currentTimeMillis())
                {
                    listProducts.add(model);
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Product model = dataSnapshot.getValue(Product.class);
                int index = getItemIndex(model);
                if (listProducts.contains(model) && model.getStoreId().equals(Utils.getFirebaseUser().getUid()) && model.getOfferExpire()>System.currentTimeMillis())
                {
                    listProducts.set(index,model);
                    productAdapter.notifyItemChanged(index);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Product model = dataSnapshot.getValue(Product.class);
                int index = getItemIndex(model);
                if (model.getStoreId().equals(Utils.getFirebaseUser().getUid()) && model.getOfferExpire()>System.currentTimeMillis())
                {
                    listProducts.remove(index);
                    productAdapter.notifyItemRemoved(index);
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(Product product)
    {
        int index = -1;

        for (int i = 0; i < listProducts.size(); i++) {
            if(listProducts.get(i).getProductId().equals(product.getProductId()))
            {
                index = i ;
                break;
            }
        }
        return index;
    }
}
