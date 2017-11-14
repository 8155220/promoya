package uagrm.promoya.Fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
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

    //Search
    //Search Functionality
    FirebaseRecyclerAdapter<Product,ClientProductViewHolder> searchAdapter;
    List<String> suggesList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

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
        layoutManager = new GridLayoutManager(getContext(),2);//Cudrado
       // layoutManager = new StaggeredGridLayoutManager(getContext(),StaggeredGridLayoutManager.VERTICAL,);//Cudrado
        //layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);//Cudrado

        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();

        //Search
        materialSearchBar = (MaterialSearchBar)view.findViewById(R.id.searchBar);
        materialSearchBar.setHint("Ingresa tu Pregunta");
        //materialSearchBar.setSpeechMode(false); no need, because we already define in xml
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggesList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When user type their text, we will change suggest list
                //System.out.println("TEXTO CAMBIADO");
                List<String> suggest = new ArrayList<String>();
                for(String search:suggesList)//loop om siggest List
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                    {
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);
                if(materialSearchBar.isSearchEnabled())
                {
                    materialSearchBar.showSuggestionsList();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enable) {
                //When Search bar is close
                //restore original adapter
                if(!enable)
                {
                    materialSearchBar.hideSuggestionsList();
                    recycler_menu.setAdapter(adapter);

                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show result of search adapter
                //ELIMINAR TEST
                /*List<String> suggest = new ArrayList<String>();
                for(String search:suggesList)//loop om siggest List
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                    {
                        suggest.add(search);
                    }
                }*/
                //System.out.println("LIstaSuggest :"+suggest.toString());
                //materialSearchBar.set(suggest);
                // materialSearchBar.setLastSuggestions(suggest);
                //materialSearchBar.showSuggestionsList();

                //END TEST
                startSearch(materialSearchBar.getText());
                //startSearch(text);//original
            }

            @Override
            public void onButtonClicked(int i) {

            }
        });


    }

    private void startSearch(CharSequence text){
        searchAdapter = new FirebaseRecyclerAdapter<Product, ClientProductViewHolder>(
                Product.class,
                R.layout.product_item,
                ClientProductViewHolder.class,
                products.orderByChild("name").equalTo(text.toString()) //compare Name
        ) {
            @Override
            protected void populateViewHolder(ClientProductViewHolder viewHolder, final Product model, int position) {

                viewHolder.product_name.setText(model.getName());
                Picasso.with(getActivity().getApplicationContext()).load(model.getListImage().get(0))
                        .into(viewHolder.product_image);
                viewHolder.product_price.setText(model.getPrice()+ " Bs");

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

                /*//Determinar si el usuario actual ha dado un me encorazona a una pregunta y ha configurado
                //las imagenes de los corazones como debe ser
                if (model.puntos.containsKey(getUid())){
                    viewHolder.corazonView.setImageResource(R.drawable.corazon_llenito);
                }else{
                    viewHolder.corazonView.setImageResource(R.drawable.corazon_vacio);
                }
                //Ahora falta enlazar la Pregunta al ViewHolder que creamos antes,
                // y configurando el CorazonEscuchador <3
                viewHolder.enlazarPregunta(model, new View.OnClickListener(){
                    @Override
                    public void onClick(View puntosView) {
                        DatabaseReference preguntasRef = bdReferencia.child("Preguntas").child(preguntaRef.getKey());

                        //Ejecutar las 2 transacciones
                        alClickearCorazon(preguntasRef);
                        //alClickearCorazon(usuariosRef);
                    }
                });*/

            }
        };
        recycler_menu.setAdapter(searchAdapter);// Set adapter for recycler view is sarch result
    }

    private void loadSuggest() {
        products.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //suggesList.clear();
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Product item = postSnapshot.getValue(Product.class);
                            if(!suggesList.contains(item.getName()))
                                suggesList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
                viewHolder.product_price.setText(model.getPrice()+ " Bs");
                //viewHOlder.
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
