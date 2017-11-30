package uagrm.promoya.Fragment.Home;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Product;
import uagrm.promoya.Model.Store;
import uagrm.promoya.ProductDetail;
import uagrm.promoya.R;
import uagrm.promoya.StoreDetail;
import uagrm.promoya.ViewHolder.ProductViewHolder;
import uagrm.promoya.ViewHolder.StoreViewHolder;

/**
 * Created by Mako on 1/13/2017.
 */
public class HomeStoresFragment extends Fragment{
    public static final String STORES_CHILD = "stores";

    //Model

    //FIREBASE
    FirebaseDatabase db;
    DatabaseReference stores;
    FirebaseRecyclerAdapter<Store, StoreViewHolder> adapter;

    //Recycler
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    View rootView;

    public HomeStoresFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_store_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView=view;
        //Init Firebase
        db = FirebaseDatabase.getInstance();
        //stores = db.getReference(STORES_CHILD).child(Common.currentUser.getUid());
        stores = db.getReference(STORES_CHILD);

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

        adapter=new FirebaseRecyclerAdapter<Store, StoreViewHolder>(
                Store.class,
                R.layout.store_item,
                StoreViewHolder.class,
                stores
        ) {
            @Override
            protected void populateViewHolder(StoreViewHolder viewHolder, final Store model, int position) {
                viewHolder.product_name.setText(model.getDisplayName());
                Picasso.with(getActivity().getApplicationContext()).load(model.getLogoImgUrl())
                        .into(viewHolder.product_image);
                //final  Category clickItem =model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent storeDetail = new Intent(getContext(), StoreDetail.class);
                        storeDetail.putExtra("STORE",model);
                        startActivity(storeDetail);
                    }
                });
                viewHolder.product_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        String shareBody = Common.storeUrl+model.getStoreId();
                        String shareSub = model.getDisplayName();
                        myIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                        myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                        startActivity(Intent.createChooser(myIntent,"Compartir Usando"));

                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }



}
