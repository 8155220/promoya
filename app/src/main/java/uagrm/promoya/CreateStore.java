package uagrm.promoya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import uagrm.promoya.Common.Common;
import uagrm.promoya.Common.Location.PermissionUtils;
import uagrm.promoya.Model.Store;

/**
 * Created by Shep on 11/27/2017.
 */

public class CreateStore extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback {


    @BindView(R.id.txt_location)
    TextView txt_location;

    //
    @BindView(R.id.store_my_location)
    ImageView store_my_location;
    @BindView(R.id.edtName)
    TextView edtName;
    @BindView(R.id.edtDescription)
    TextView edtDescription;
    @BindView(R.id.logoImgUrl)
    ImageView imageViewLogo;
    @BindView(R.id.backgroundImgUrl)
    ImageView imageViewBackground;

    Button btnUpload;

    @BindView(R.id.btnCreate)
    Button btnCreate;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    boolean flagLogoImgClicked;
    boolean flagBackgroundImgClicked;

    Store newStore;
    private static final int GALLERY_REQUEST = 1;
    Uri uriLogoImg;
    Uri uriBackgroundImg;
    // LogCat tag
    //private static final String TAG = MyLocationUsingHelper.class.getSimpleName();
    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    double latitude;
    double longitude;
    // list of permissions
    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils;
    boolean isPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Crear Tienda");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        permissionUtils = new PermissionUtils(CreateStore.this);
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionUtils.check_permission(permissions, "Need GPS permission for getting your location", 1);

        store_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_location.setText("Esto Puede Tardar varios minutos ... Puede Saltarse este paso");
                getLocation();
                if (mLastLocation != null) {
                    store_my_location.setImageResource(R.drawable.ic_check);
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    getAddress();
                } else {
                    //if (btnProceed.isEnabled())
                    //    btnProceed.setEnabled(false);
                    showToast("No se pudo obtener la ubicacion, asegurate de la ubicaicon este habilitada");
                }
            }
        });
        // check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        //Listeners
        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagLogoImgClicked = true;
                CameraOpen();
            }
        });
        imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagBackgroundImgClicked = true;
                CameraOpen();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCreateStore()) {
                    newStore= new Store();
                    uploadImage();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        flagLogoImgClicked = false;
        flagBackgroundImgClicked = false;
    }
    public void pushToDataBase(){
        newStore.setDescription(edtDescription.getText().toString());
        newStore.setDisplayName(edtName.getText().toString());
        newStore.setStoreId(Common.currentUser.getUid());
        Common.DB.child("stores").child(Common.currentUser.getUid()).setValue(newStore);
        Common.DB.child("users").child(Common.currentUser.getUid()).child("hasStore").setValue(1);
        Common.DB.child("users").child(Common.currentUser.getUid()).child("storeName").setValue(newStore.getDisplayName());
    }

    private boolean validateCreateStore() {
        if (edtName.getText().toString().isEmpty()) {
            Snackbar.make(getWindow().getDecorView(), "Complete : Nombre Tienda", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (edtDescription.getText().toString().isEmpty()) {
            Snackbar.make(getWindow().getDecorView(), "Complete : Descripcion", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (uriLogoImg == null) {
            Snackbar.make(getWindow().getDecorView(), "Seleccione logotipo tienda", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (uriBackgroundImg == null) {
            Snackbar.make(getWindow().getDecorView(), "Selecione imagen fondo tienda", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadImage() {
        if (uriLogoImg != null && uriBackgroundImg != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();
            StorageReference storageReference;
            storageReference = FirebaseStorage.getInstance().getReference();
            //Subiendo Logo
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(uriLogoImg)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override

                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //mDialog.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (newStore != null) {
                                        newStore.setLogoImgUrl(uri.toString());
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(CreateStore.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        }
                    })
            ;
            //Subiendo Logo
            imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder2 = storageReference.child("images/" + imageName);
            imageFolder2.putFile(uriBackgroundImg)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override

                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFolder2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDialog.dismiss();
                                    if (newStore != null) {
                                        newStore.setBackgroundImgUrl(uri.toString());
                                        newStore.setDescription(edtDescription.getText().toString());
                                        newStore.setDisplayName(edtName.getText().toString());
                                        newStore.setStoreId(Common.currentUser.getUid());
                                        verifyLongLat();
                                        Common.DB.child("stores").child(Common.currentUser.getUid()).setValue(newStore);
                                        Common.DB.child("users").child(Common.currentUser.getUid()).child("hasStore").setValue(1);
                                        Common.DB.child("users").child(Common.currentUser.getUid()).child("storeName").setValue(newStore.getDisplayName());
                                        finish();
                                    }

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(CreateStore.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    })
            ;
        }
    }

    private void verifyLongLat() {
        if (mLastLocation != null) {
            newStore.setLatitude(mLastLocation.getLatitude());
            newStore.setLongitude(mLastLocation.getLongitude());
        } else {
            newStore.setLatitude(-17.776624);
            newStore.setLongitude(-63.1955168);
        }
    }

    private void CameraOpen() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    /**
     * Method to display the location on UI
     */

    private void getLocation() {

        if (isPermissionGranted) {

            try {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }

    }
    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
    public void getAddress() {

        Address locationAddress = getAddress(latitude, longitude);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "\n" + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "\n" + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "\n" + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "\n" + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "\n" + country;


                txt_location.setText(currentLocation);
                txt_location.setVisibility(View.VISIBLE);

                //if (!btnProceed.isEnabled())
                 //   btnProceed.setEnabled(true);
            }

        }

    }
    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(CreateStore.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }


    /**
     * Method to verify google play services on the device
     */

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null)
        {
            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        }
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imagen1 = data.getData();
            CropImage.activity(imagen1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //imagen = result.getUri();
                if (flagLogoImgClicked) {
                    uriLogoImg = result.getUri();
                } else {
                    uriBackgroundImg = result.getUri();
                }
                updateImg();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void updateImg() {
        if (flagLogoImgClicked) {
            flagLogoImgClicked = false;
            imageViewLogo.setImageURI(uriLogoImg);
        } else if (flagBackgroundImgClicked) {
            flagBackgroundImgClicked = false;
            imageViewBackground.setImageURI(uriBackgroundImg);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
        //        + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    // Permission check functions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION", "GRANTED");
        isPermissionGranted = true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY", "GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION", "DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION", "NEVER ASK AGAIN");
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
