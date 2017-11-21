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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import uagrm.promoya.Common.Common;
import uagrm.promoya.R;
import uagrm.promoya.Model.Store;

/**
 * Created by Mako on 1/13/2017.
 */
public class StoreHomeFragment extends Fragment implements OnMapReadyCallback {

    //REQUIERO EL ID DE LA TIENDA

    TextView storeName;
    TextView storeDescription;
    ImageView backgroundImg;
    ImageView logoImg;
    Button store_button_suscribe;
    DatabaseReference db;

    GoogleMap mGoogleMap;
    MapView mMapView;

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
            /*Picasso.with(getActivity().getApplicationContext()).load(currentStore.getLogoImgUrl())
                    .into(logoImg);*/
        }

        store_button_suscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentStore!=null){
                    FirebaseMessaging.getInstance().subscribeToTopic(currentStore.getStoreId());
                }
            }
        });
        //pero como ya tenemos los datos

        mMapView = (MapView) view.findViewById(R.id.map);
        if (mMapView!=null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }
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
    }
}
