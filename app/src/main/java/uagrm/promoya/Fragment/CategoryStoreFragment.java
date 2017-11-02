package uagrm.promoya.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;
import uagrm.promoya.Common.Common;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Category;
import uagrm.promoya.Model.Store;
import uagrm.promoya.MyStore;
import uagrm.promoya.ProductList;
import uagrm.promoya.R;
import uagrm.promoya.ViewHolder.CategoryViewHolder;

/**
 * Created by Mako on 1/13/2017.
 */
public class CategoryStoreFragment extends Fragment{
    public static final String PRODUCT_CHILD = "Category";

    Toolbar toolbar;

    //Button
    FloatingActionButton fab;

    //AlertDialog
    MaterialEditText edtName;
    Button btnUpload,btnSelect;

    //Model
    Category newCategory;

    //FIREBASE
    FirebaseDatabase db;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    //Recycler
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    Uri saveUri;

    View rootView;

    public CategoryStoreFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.app_bar_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootView=view;
        //Init Firebase
        db = FirebaseDatabase.getInstance();
        categories = db.getReference(PRODUCT_CHILD).child(Common.currentUser.getUid());
        System.out.println("Current User :" +Common.currentUser.getUid() );
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //tolbar
        setToolBar(view);
        setHasOptionsMenu(true);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                showDialog();
            }
        });

        //Init View
        recycler_menu = (RecyclerView)view.findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();


    }
    private void loadMenu() {
        adapter=new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.category_item,
                CategoryViewHolder.class,
                categories
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getActivity().getApplicationContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                //final  Category clickItem =model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get CategoryId and send to new Activity
                        Intent productList = new Intent(getContext(),ProductList.class);
                        //Because CategoryId is key, so we just get key of this item
                        productList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(productList);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recycler_menu.setAdapter(adapter);
    }


    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Agregar Nueva Categoria");
        alertDialog.setMessage("Porfavor llenar toda la informacion");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View add_category_layout = inflater.inflate(R.layout.add_new_category_layout, null);

        edtName = add_category_layout.findViewById(R.id.edtName);
        btnSelect = add_category_layout.findViewById(R.id.btnSelect);
        btnUpload = add_category_layout.findViewById(R.id.btnUpload);

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Let user select image from gallery and save Uri of this image
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(); //Let user select image from gallery and save Uri of this image
            }
        });

        alertDialog.setView(add_category_layout);
        alertDialog.setIcon(R.drawable.ic_add_black_24dp);

        //Set button
        //alertDialog.show();
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();

                //HERE , just create new category
                if (newCategory != null) {
                    categories.push().setValue(newCategory);
                    /*Snackbar.make(drawer, "Nueva Categoria" + newCategory.getName() + "fue Aniadida", Snackbar.LENGTH_SHORT)
                            .show();*/
                }


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    private void setToolBar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Categorias");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        /*ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_action_menu);
        ab.setDisplayHomeAsUpEnabled(true);*/
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Seleccionar Imagen"),Common.PICK_IMAGE_REQUEST);
    }
    private void uploadImage() {
        if(saveUri !=null){
            System.out.println("entroUploadImage");
            final ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(getContext(),"Subida !!!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newCategory = new Category(edtName.getText().toString(),uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress =(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Upload"+progress+"%");
                        }
                    })
            ;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data!= null && data.getData()!=null){
            saveUri = data.getData();
            btnSelect.setText("Imagen Seleccionada");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdatDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {

        //primero, tenemos que conseguir todas las comidas de la categoria
        DatabaseReference foods = db.getReference();
        Query foodInCategory = foods.orderByChild("menuId").equalTo(key);
        foodInCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    postSnapShot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        categories.child(key).removeValue();
        /*Snackbar.make(drawer,"Categoria" + newCategory.getName() +"Eliminada Correctamente",Snackbar.LENGTH_SHORT)
                .show();*/
    }

    private void showUpdatDialog(final String key, final Category item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Actualizar Categoria");
        alertDialog.setMessage("Porfavor llenar toda la informacion");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_category_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //Set default name
        edtName.setText(item.getName());

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Let user select image from gallery and save Uri of this image
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item); //Let user select image from gallery and save Uri of this image
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_add_black_24dp);

        //Set button
        //alertDialog.show();
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();

                item.setName(edtName.getText().toString());
                categories.child(key).setValue(item);
                /*Snackbar.make(drawer,"Categoria" + newCategory.getName() +"Actualizada Correctamente",Snackbar.LENGTH_SHORT)
                        .show();*/



            }
        });alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void changeImage(final Category item) {
        if(saveUri !=null){
            final ProgressDialog mDialog = new ProgressDialog(getContext());
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(getActivity(),"Subida !!!",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress =(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Upload"+progress+"%");
                        }
                    })
            ;
        }

    }
}
