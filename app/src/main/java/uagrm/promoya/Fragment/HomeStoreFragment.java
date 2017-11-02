package uagrm.promoya.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import uagrm.promoya.R;
import uagrm.promoya.Model.Store;

/**
 * Created by Mako on 1/13/2017.
 */
public class HomeStoreFragment extends Fragment implements OnMapReadyCallback {

    TextView storeName;
    TextView storeDescription;
    ImageView backgroundImg;
    ImageView logoImg;
    DatabaseReference db;

    GoogleMap mGoogleMap;
    MapView mMapView;

    private static HomeStoreFragment singleton = new HomeStoreFragment();

    private HomeStoreFragment() {

    }

    public static HomeStoreFragment getInstance( ) {
        return singleton;
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
        logoImg = (ImageView) view.findViewById(R.id.logo_img);
        db = FirebaseDatabase.getInstance().getReference().child("stores").child("GtfoxqmB6yXRCuKuGk62aInYrJF3");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.getValue(Store.class);
                storeName.setText(store.getDisplayName());
                storeDescription.setText(store.getDescription());
                Picasso.with(getActivity().getApplicationContext()).load(store.getBackgroundImgUrl())
                        .into(backgroundImg);
                Picasso.with(getActivity().getApplicationContext()).load(store.getLogoImgUrl())
                        .into(logoImg);

                /*Glide.with(getActivity().getApplicationContext()).load(store.getBackgroundImgUrl())
                        .into(backgroundImg);
                Glide.with(getActivity().getApplicationContext()).load(store.getLogoImgUrl()).apply(RequestOptions.circleCropTransform())
                        .into(logoImg);*/
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        googleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247,-74.044502))
                .title("Estatua libertad")
                .snippet("I hope something pass"));
        CameraPosition Liberty =  CameraPosition.builder().target(new LatLng(40.689247,-74.044502))
                .zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
    }
}
