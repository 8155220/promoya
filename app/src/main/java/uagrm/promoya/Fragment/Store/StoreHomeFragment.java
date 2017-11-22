package uagrm.promoya.Fragment.Store;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Model.Notification.SenderTopic.Data;
import uagrm.promoya.R;
import uagrm.promoya.Model.Store;

/**
 * Created by Mako on 1/13/2017.
 */
public class StoreHomeFragment extends Fragment
        //implements OnMapReadyCallback
{

    //REQUIERO EL ID DE LA TIENDA

    TextView storeName;
    TextView storeDescription;
    ImageView backgroundImg;
    ImageView logoImg;
    Button store_button_suscribe;
    DatabaseReference db;

    GoogleMap mGoogleMap;
   // MapView mMapView;

    Store currentStore;


    public StoreHomeFragment() {

    }

    public StoreHomeFragment(Store currentStore) {
        this.currentStore = currentStore;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.home_store_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storeName = (TextView)view.findViewById(R.id.store_name);
        storeDescription = (TextView)view.findViewById(R.id.store_description);
        backgroundImg = (ImageView) view.findViewById(R.id.background_img);
        store_button_suscribe = (Button) view.findViewById(R.id.store_button_suscribe);

        logoImg = (ImageView) view.findViewById(R.id.logo_img);
        if(currentStore!=null)
        {
            db = FirebaseDatabase.getInstance().getReference().child("stores").child(currentStore.getStoreId());
        } else {
            db = FirebaseDatabase.getInstance().getReference().child("stores").child(Common.currentUser.getUid());
        }
        //SI QUISIERA QUE FUERA EN TIEMPO REAL
        if(currentStore==null)
        {
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Store store = dataSnapshot.getValue(Store.class);
                    storeName.setText(store.getDisplayName());
                    storeDescription.setText(store.getDescription());
                    Glide.with(getActivity().getApplicationContext()).load(store.getBackgroundImgUrl()).apply(RequestOptions.circleCropTransform())
                            .into(backgroundImg);
                    Glide.with(getActivity().getApplicationContext()).load(store.getLogoImgUrl()).apply(RequestOptions.circleCropTransform())
                            .into(logoImg);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            store_button_suscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.sendNotificationTopic(Common.currentUser.getUid(),new Data("Hello World"));
                }
            });
        }
        else {
            storeName.setText(currentStore.getDisplayName());
            storeDescription.setText(currentStore.getDescription());
            /*Picasso.with(getActivity().getApplicationContext()).load(currentStore.getBackgroundImgUrl())
                    .into(backgroundImg);*/
            Glide.with(getActivity().getApplicationContext()).load(currentStore.getBackgroundImgUrl())
                    .into(backgroundImg);

            Glide.with(getActivity().getApplicationContext()).load(currentStore.getLogoImgUrl()).apply(RequestOptions.circleCropTransform())
                    .into(logoImg);

            if (currentStore.suscripciones.containsKey(Common.currentUser.getUid())){
                store_button_suscribe.setText("Suscrito");
                //store_button_suscribe.setBackground(getResources().getColor(R.color.colorAccent));
                //store_button_suscribe.setBackground(getResources().getColor(R.color.colorAccent));
            }else{
                store_button_suscribe.setText("Suscribirse");
            }
            store_button_suscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentStore!=null){
                        FirebaseMessaging.getInstance().subscribeToTopic(currentStore.getStoreId());
                        onClickSuscribe(db);
                    }
                }
            });

            /*viewHolder.enlazarPregunta(model, new View.OnClickListener(){
                @Override
                public void onClick(View puntosView) {
                    DatabaseReference preguntasRef = bdReferencia.child("Preguntas").child(preguntaRef.getKey());

                    System.out.println(preguntaRef);
                    System.out.println(preguntaRef +"gguwu");
                    //DatabaseReference usuariosRef = bdReferencia.child("Usuarios-Preguntas").child(model.id).child(preguntaRef.getKey());

                    //Ejecutar las 2 transacciones
                    onClickSuscribe(preguntasRef);
                    //onClickSuscribe(usuariosRef);
                }
            });*/

        }



        /*mMapView = (MapView) view.findViewById(R.id.map);
        if (mMapView!=null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }*/
    }
    private void onClickSuscribe(DatabaseReference preguntasRef) {
        preguntasRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Store p = mutableData.getValue(Store.class);
                if (p==null){
                    return Transaction.success(mutableData);
                }
                if (p.suscripciones.containsKey(Common.currentUser.getUid())){
                    //Si ya le habia dado Encorazona entonces ya me toca quitarle al clickearlo xD
                    //p.puntosContador = p.puntosContador -1;
                    p.suscripciones.remove(Common.currentUser.getUid());
                    store_button_suscribe.setText("Suscribirse");
                }else{
                    //Si no le he dado me Encorazona, entonces me toca aumentar un punto
                    //p.puntosContador = p.puntosContador +1;
                    p.suscripciones.put(Common.currentUser.getUid(), true);
                    store_button_suscribe.setText("Suscrito");
                }

                //Ahora toca guardar el valor en la nube y reportar que fue exitoso :v
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                //Log.d("onClickSuscribe", databaseError.toString());
            }
        });
    }
/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(-17.781822,-63.181690))
                .title("Estatua libertad")
                .snippet("I hope something pass"));
        CameraPosition Liberty =  CameraPosition.builder().target(new LatLng(-17.781822,-63.181690))
                .zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
    }*/
}
