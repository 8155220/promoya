package uagrm.promoya.Fragment.Store;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
import uagrm.promoya.ViewHolder.ClientViewHolder.ClientCategoryViewHolder;

/**
 * Created by Mako on 1/13/2017.
 */
public class StoreCategoryFragment extends Fragment{
    public static final String PRODUCT_CHILD = "Category";

    Toolbar toolbar;

    //Button
    FloatingActionButton fab;

    //AlertDialog
    MaterialEditText edtName;
    Button btnUpload,btnSelect;
    MaterialSpinner spinner;
    View add_category_layout;

    //Model
    Category newCategory;

    //FIREBASE
    FirebaseDatabase db;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    FirebaseRecyclerAdapter<Category, ClientCategoryViewHolder> clientAdapter;

    //Recycler
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    Uri saveUri;

    View rootView;

    //
    Store currentStore;
    public StoreCategoryFragment() {
    }


    public static StoreCategoryFragment newInstance(Store currentStore) {
        StoreCategoryFragment myFragment = new StoreCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("currentStore", currentStore);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            currentStore = (Store) getArguments().getSerializable("currentStore");
        }

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
        if(currentStore==null)
        {
            categories = db.getReference(PRODUCT_CHILD).child(Common.currentUser.getUid());
        } else {
            categories = db.getReference(PRODUCT_CHILD).child(currentStore.getStoreId());
        }
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //tolbar
        /*setToolBar(view);
        setHasOptionsMenu(true);*/

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (currentStore==null){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    //        .setAction("Action", null).show();
                    showDialog();
                }
            });
        } else fab.hide();

        //Init View
        recycler_menu = (RecyclerView)view.findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();


    }
    private void loadMenu() {
        if(currentStore==null)
        {
            adapter=new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                    Category.class,
                    R.layout.category_item,
                    CategoryViewHolder.class,
                    categories
            ) {
                @Override
                protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                    viewHolder.txtMenuName.setText(model.getName());
                    Picasso.with(getActivity().getApplicationContext()).load(model.getImage())
                            .into(viewHolder.imageView);
                    /*Glide.with(getActivity().getApplicationContext()).load(Common.currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.imageView);*/
                    //final  Category clickItem =model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //Get CategoryId and send to new Activity
                            Intent productList = new Intent(getContext(),ProductList.class);
                            //Because CategoryId is key, so we just get key of this item
                            // productList.putExtra("CategoryId",adapter.getRef(position).getKey());//ORIGINAL
                            productList.putExtra("currentCategory",model);
                            productList.putExtra("STORE",currentStore);
                            startActivity(productList);
                        }
                    });
                }
            };
            adapter.notifyDataSetChanged();
            recycler_menu.setAdapter(adapter);
        }else {
            clientAdapter =new FirebaseRecyclerAdapter<Category, ClientCategoryViewHolder>(
                    Category.class,
                    R.layout.category_item,
                    ClientCategoryViewHolder.class,
                    categories
            ) {
                @Override
                protected void populateViewHolder(ClientCategoryViewHolder viewHolder, final Category model, int position) {
                    viewHolder.txtMenuName.setText(model.getName());
                    Picasso.with(getActivity().getApplicationContext()).load(model.getImage())
                            .into(viewHolder.imageView);
                    /*Glide.with(getActivity().getApplicationContext()).load(Common.currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform())
                            .into(viewHolder.imageView);*/
                    //final  Category clickItem =model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //Get CategoryId and send to new Activity
                            Intent productList = new Intent(getContext(),ProductList.class);
                            productList.putExtra("currentCategory",model);
                            productList.putExtra("STORE",currentStore);
                            startActivity(productList);
                        }
                    });
                }
            };
            clientAdapter.notifyDataSetChanged();
            recycler_menu.setAdapter(clientAdapter);
        }

    }


    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Agregar Nueva Categoria");
        alertDialog.setMessage("Porfavor llenar toda la informacion");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        add_category_layout = inflater.inflate(R.layout.add_new_category_layout, null);

        edtName = add_category_layout.findViewById(R.id.edtName);
        btnSelect = add_category_layout.findViewById(R.id.btnSelect);
        btnUpload = add_category_layout.findViewById(R.id.btnUpload);

        spinner = (MaterialSpinner)add_category_layout.findViewById(R.id.spinner);
        spinner.setItems(
                "camaras digitales y fotografia"
                ,"computacion"
                ,"consolas y videojuegos"
                ,"electronica y audio video"
                ,"hogar y electrodomesticos"
        );
//        spinner.setArrowColor(R.color.colorAccent);
        spinner.setArrowColor(getResources().getColor(R.color.colorAccent));
        spinner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        spinner.setTextColor(getResources().getColor(R.color.white));


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
                if(saveUri==null)
                {
                    Snackbar.make(add_category_layout, "Debe Seleccionar Al menos una imagen", Snackbar.LENGTH_SHORT).show();
                     //Let user select image from gallery and save Uri of this image
                } else uploadImage();

            }
        });

        alertDialog.setView(add_category_layout);
        alertDialog.setIcon(R.drawable.ic_add_black_24dp);

        //Set button
        //alertDialog.show();
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                if(validate())
                    dialogInterface.dismiss();

                //HERE , just create new category
                if (newCategory != null) {
                    String key = categories.push().getKey();
                    newCategory.setCategoryId(key);
                    categories.child(key).setValue(newCategory);
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

    private boolean validate() {
        if(edtName.getText().toString().isEmpty())
        {
            Snackbar.make(getActivity().getWindow().getDecorView(), "Complete : Nombre Categoria", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(saveUri!=null)
        {
            Snackbar.make(getActivity().getWindow().getDecorView(), "Debe Seleccionar Al menos una imagen", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        /*if(!uploadAtLeastonePhoto){
            Snackbar.make(getWindow().getDecorView(), "Debe Seleccionar Subir Imagen", Snackbar.LENGTH_SHORT).show();
            return false;
        }*/
        return true;
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
                                    newCategory = new Category();
                                    newCategory.setName(edtName.getText().toString());
                                    newCategory.setImage(uri.toString());
                                    newCategory.setPrincipalCategory(spinner.getItems().get(spinner.getSelectedIndex()).toString());
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
        if(currentStore==null){
            if(item.getTitle().equals(Common.UPDATE)){
                showUpdatDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
            }
            else if(item.getTitle().equals(Common.DELETE)){
                deleteCategory(adapter.getRef(item.getOrder()).getKey());
            }
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

        //Spinner
        spinner = (MaterialSpinner)add_menu_layout.findViewById(R.id.spinner);
        spinner.setItems(
                "camaras digitales y fotografia"
                ,"computacion"
                ,"consolas y videojuegos"
                ,"electronica y audio video"
                ,"hogar y electrodomesticos"
        );
//        spinner.setArrowColor(R.color.colorAccent);
        spinner.setArrowColor(getResources().getColor(R.color.colorAccent));
        spinner.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        spinner.setTextColor(getResources().getColor(R.color.white));

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
                if(saveUri==null)
                {
                    Snackbar.make(add_category_layout, "Debe Seleccionar Al menos una imagen", Snackbar.LENGTH_SHORT).show();
                    //Let user select image from gallery and save Uri of this image
                } else changeImage(item);
                 //Let user select image from gallery and save Uri of this image
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
