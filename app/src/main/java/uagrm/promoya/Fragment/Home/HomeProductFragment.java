package uagrm.promoya.Fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Product;
import uagrm.promoya.ProductDetail;
import uagrm.promoya.R;
import uagrm.promoya.ViewHolder.ClientViewHolder.ClientProductViewHolder;

/**
 * Created by Mako on 1/13/2017.
 */
public class HomeProductFragment extends Fragment{
    public static final String PRODUCT_CHILD = "Products";

    //Model
    Product newProduct;

    //FIREBASE
    FirebaseDatabase db;
    DatabaseReference products;
    FirebaseRecyclerAdapter<Product, ClientProductViewHolder> adapter;

    //Recycler
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    View rootView;
    List<Product> listProducts;

    public HomeProductFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        listProducts = new ArrayList<>();
        return inflater.inflate(R.layout.activity_product_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView=view;
        //Init Firebase
        db = FirebaseDatabase.getInstance();
        //stores = db.getReference(STORES_CHILD).child(Common.currentUser.getUid());
        products = db.getReference(PRODUCT_CHILD);

        //tolbar
        //setHasOptionsMenu(true);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.hide();

        //Init View

        recycler_menu = (RecyclerView)view.findViewById(R.id.recycler_product);
        recycler_menu.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(getContext()); //original
        layoutManager = new GridLayoutManager(getContext(),2);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();


    }
    private void loadMenu() {

        adapter=new FirebaseRecyclerAdapter<Product, ClientProductViewHolder>(
                Product.class,
                R.layout.product_item,
                ClientProductViewHolder.class,
                products
        ) {
            @Override
            protected void populateViewHolder(ClientProductViewHolder viewHolder, final Product model, int position) {
                viewHolder.product_name.setText(model.getName());
                Picasso.with(getActivity().getApplicationContext()).load(model.getListImage().get(0))
                        .into(viewHolder.product_image);
                //final  Category clickItem =model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get CategoryId and send to new Activity
                        Intent producDetail = new Intent(getContext(),ProductDetail.class);
                        //producDetail.putExtra("ProductId",adapter.getRef(position).getKey());
                        producDetail.putExtra("PRODUCT",model);
                        //SE PUEDE MEJORAR ESTO SI LE PASAMOS EL MODELO PRODUCTO IMPLEMENTANDO EL SERIALISABLE
                        startActivity(producDetail);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }



}
