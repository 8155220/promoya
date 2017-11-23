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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import uagrm.promoya.Common.Common;
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

    //GoogleMap mGoogleMap;
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
            //db.addValueEventListener(new ValueEventListener() {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Store store = dataSnapshot.getValue(Store.class);
                    storeName.setText(store.getDisplayName());
                    storeDescription.setText(store.getDescription());
                    if(getActivity().getApplicationContext()!=null)
                    {
                        Glide.with(getActivity().getApplicationContext()).load(store.getBackgroundImgUrl()).apply(RequestOptions.circleCropTransform())
                                .into(backgroundImg);
                        Glide.with(getActivity().getApplicationContext()).load(store.getLogoImgUrl()).apply(RequestOptions.circleCropTransform())
                                .into(logoImg);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            store_button_suscribe.setVisibility(View.GONE);
        }
        else {
            storeName.setText(currentStore.getDisplayName());
            storeDescription.setText(currentStore.getDescription());
            Glide.with(getActivity().getApplicationContext()).load(currentStore.getBackgroundImgUrl())
                    .into(backgroundImg);
            Glide.with(getActivity().getApplicationContext()).load(currentStore.getLogoImgUrl()).apply(RequestOptions.circleCropTransform())
                    .into(logoImg);
            db.child("subscriptions").addChildEventListener(getChildEventlistenerSuscriptions());
            if (currentStore.subscriptions.containsKey(Common.currentUser.getUid())){
                store_button_suscribe.setText("Suscrito");
            }else{
                store_button_suscribe.setText("Suscribirse");
            }
            store_button_suscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     onClickSuscribe(db);
                }
            });

        }
        /*mMapView = (MapView) view.findViewById(R.id.map);
        if (mMapView!=null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }*/
    }
    public ChildEventListener getChildEventlistenerSuscriptions(){
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("datasnapshot added :"+dataSnapshot.toString());
                Boolean booleanValue = dataSnapshot.getValue(Boolean.class);
                if (dataSnapshot.getKey().equals(Common.currentUser.getUid())) {
                    FirebaseMessaging.getInstance().subscribeToTopic(currentStore.getStoreId());
                }
                currentStore.subscriptions.put(dataSnapshot.getKey(), booleanValue);
                registerKeySubscriptionInStatistics();
                updateSuscriptionButton();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Boolean booleanValue = snapshot.getValue(Boolean.class);
                    currentStore.subscriptions.put(snapshot.getKey(), booleanValue);
                }
                updateSuscriptionButton();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getKey().equals(Common.currentUser.getUid())) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(currentStore.getStoreId());
                }
                currentStore.subscriptions.remove(dataSnapshot.getKey());
                removeKeySubscriptionInStatistics();
                updateSuscriptionButton();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
    public void updateSuscriptionButton(){

        if (currentStore.subscriptions.containsKey(Common.currentUser.getUid())){
            store_button_suscribe.setText("Suscrito");
        }else{
            store_button_suscribe.setText("Suscribirse");
        }
    }
    private void onClickSuscribe(DatabaseReference preguntasRef) {
        preguntasRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                currentStore = mutableData.getValue(Store.class);
                if (currentStore==null){
                    return Transaction.success(mutableData);
                    //return Transaction.abort();
                }
                if (currentStore.subscriptions.containsKey(Common.currentUser.getUid())){
                    //Si ya le habia dado Encorazona entonces ya me toca quitarle al clickearlo xD
                    currentStore.subscriptionsCount = currentStore.subscriptionsCount -1;
                    currentStore.subscriptions.remove(Common.currentUser.getUid());
                    //store_button_suscribe.setText("Suscribirse");
                }else{
                    //Si no le he dado me Encorazona, entonces me toca aumentar un punto
                    currentStore.subscriptionsCount = currentStore.subscriptionsCount +1;
                    currentStore.subscriptions.put(Common.currentUser.getUid(), true);
                    //store_button_suscribe.setText("Suscrito");
                }

                //Ahora toca guardar el valor en la nube y reportar que fue exitoso :v
                mutableData.setValue(currentStore);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                //Log.d("onClickSuscribe", databaseError.toString());
            }
        });
    }
    public void registerKeySubscriptionInStatistics(){
        DatabaseReference statistics = FirebaseDatabase.getInstance().getReference();
        statistics.child("statistics")
                .child("suscriptions")
                .child(currentStore.getStoreId())
                .setValue(System.currentTimeMillis());
    }
    public void removeKeySubscriptionInStatistics(){
        DatabaseReference statistics = FirebaseDatabase.getInstance().getReference();
        statistics.child("statistics")
                .child("suscriptions")
                .child(currentStore.getStoreId()).setValue(null);

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
