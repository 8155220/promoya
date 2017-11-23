package uagrm.promoya;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Category;
import uagrm.promoya.Model.Notification.SenderTopic.Data;
import uagrm.promoya.Model.Product;
import uagrm.promoya.Model.Store;
import uagrm.promoya.ViewHolder.ClientViewHolder.ClientCategoryViewHolder;
import uagrm.promoya.ViewHolder.ClientViewHolder.ClientProductViewHolder;
import uagrm.promoya.ViewHolder.ProductViewHolder;
import uagrm.promoya.utils.Utils;

public class ProductList extends AppCompatActivity implements  View.OnClickListener{
    public static final String PRODUCT_CHILD = "Products";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;

    FloatingActionButton fab;

    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";
    Category currentCategory;
    Store currentStore;
    String key;

    FirebaseRecyclerAdapter<Product,ProductViewHolder> adapter;
    FirebaseRecyclerAdapter<Product,ClientProductViewHolder> clientAdapter;

    //Add new food
    ElegantNumberButton daysButton;
    EditText edt_discount;
    MaterialEditText edtName,edtDescription,edtPrice;
    Button btnUpload;
    Boolean uploadAtLeastonePhoto;
    Product newProduct;
    Uri saveUri;
    // AddMultiple Food
    LinearLayout layout_img;
    private List<Uri> listUri;
    private List<ImageView> listaImageView;

    ImageView img1,img2,img3,img4,img_remove_last;

    private static final int GALLERY_REQUEST = 1;

    MaterialSearchBar materialSearchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        loadExtras();
        //FIrebase
        db = FirebaseDatabase.getInstance();
        foodList = db.getReference(PRODUCT_CHILD);
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference();

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        fab = (FloatingActionButton)findViewById(R.id.fab);

        if (currentStore==null){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddFoodDialog();
                }
            });
        } else fab.hide();


        if(!currentCategory.getCategoryId().isEmpty()){
            loadListFood(currentCategory.getCategoryId());
        }

        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.disableSearch();

    }

    private void loadExtras() {
        if(getIntent()!=null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras.containsKey("currentCategory"))
            {
                //categoryId = getIntent().getStringExtra("CategoryId");
                currentCategory = (Category) getIntent().getExtras().getSerializable("currentCategory");
            }
            if(extras.containsKey("STORE"))
            {
                //categoryId = getIntent().getStringExtra("CategoryId");
                currentStore = (Store) getIntent().getExtras().getSerializable("STORE");
            }
        }
    }

    private void showAddFoodDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductList.this);
        alertDialog.setTitle("Agregar Nuevo Producto");
        //alertDialog.setMessage("Porfavor llenar toda la informacion");
        listUri = new ArrayList<>();
        listaImageView = new ArrayList<>();
        newProduct = new Product();
        uploadAtLeastonePhoto=false;

        LayoutInflater inflater = this.getLayoutInflater();
        final View add_menu_layout = inflater.inflate(R.layout.add_new_product_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        edt_discount = add_menu_layout.findViewById(R.id.edtDiscount);
        edt_discount.setVisibility(View.INVISIBLE);
        //Binding MultipleImg
        img1 = (ImageView) add_menu_layout.findViewById(R.id.img1);
        img2 = (ImageView) add_menu_layout.findViewById(R.id.img2);
        img3 = (ImageView) add_menu_layout.findViewById(R.id.img3);
        img4 = (ImageView) add_menu_layout.findViewById(R.id.img4);
        img_remove_last = (ImageView) add_menu_layout.findViewById(R.id.img_remove_last);
        layout_img = (LinearLayout) add_menu_layout.findViewById(R.id.layout_img);
        listaImageView.add(img1);
        listaImageView.add(img2);
        listaImageView.add(img3);
        listaImageView.add(img4);
        layout_img.setOnClickListener(this);
        img_remove_last.setOnClickListener(this);

        btnUpload = (Button)add_menu_layout.findViewById(R.id.btnUpload);


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(); //Let user select image from gallery and save Uri of this image
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_menu_store); //cambiar

        //Set button
        //alertDialog.show();
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                    if(validate())
                    {
                        key = foodList.push().getKey();
                        newProduct.setName(edtName.getText().toString());
                        newProduct.setDescription(edtDescription.getText().toString());
                        newProduct.setPrice(edtPrice.getText().toString());
                        newProduct.setCategoryId(currentCategory.getCategoryId());
                        newProduct.setPrincipalCategory(currentCategory.getPrincipalCategory());
                        newProduct.setDate(String.valueOf(System.currentTimeMillis()));
                        newProduct.setStoreId(Utils.getFirebaseUser().getUid());
                        newProduct.setProductId(key);
                        newProduct.setStoreName(Common.user.getStoreName());
                        foodList.child(key).setValue(newProduct);
                        sendNotificationToSuscriptors();
                        Snackbar.make(rootLayout,"El producto se añadira en breve", Snackbar.LENGTH_SHORT)
                                .show();
                        dialogInterface.dismiss();
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

    private void sendNotificationToSuscriptors() {
        Data data = new Data();
        data.setTitle("Tienda :"+newProduct.getStoreName());
        data.setBody("Agrego un nuevo producto");
        data.setImage(newProduct.getListImage().get(0));
        Common.sendNotificationTopic(Common.currentUser.getUid(),data);
    }

    private boolean validate() {
        if(edtName.getText().toString().isEmpty())
        {
            Snackbar.make(getWindow().getDecorView(), "Complete : Nombre Producto", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(edtDescription.getText().toString().isEmpty())
        {
            Snackbar.make(getWindow().getDecorView(), "Complete : Descripcion", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(edtPrice.getText().toString().isEmpty())
        {
            Snackbar.make(getWindow().getDecorView(), "Complete : Precio", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(listUri.size()==0)
        {
            Snackbar.make(getWindow().getDecorView(), "Debe Seleccionar Al menos una imagen", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(!uploadAtLeastonePhoto){
            Snackbar.make(getWindow().getDecorView(), "Debe Seleccionar Subir Imagen", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void uploadImage() {
        if(listUri.size()>0){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            for (int i = 0; i < listUri.size(); i++) {
                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("images/"+imageName);
                final int finalI = i;
                imageFolder.putFile(listUri.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override

                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if(newProduct!=null)
                                        {
                                            uploadAtLeastonePhoto=true;
                                            newProduct.addUrlImg(uri.toString());
                                            //foodList.child(key).child("listImage").setValue(newProduct.getListImage());
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Toast.makeText(ProductList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress =(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                            }
                        })
                ;
            }
        }
        else {
            Snackbar.make(getWindow().getDecorView(), "Selecciona al menos una imagen", Snackbar.LENGTH_SHORT).show();
        }
    }
    private void uploadImage(final Product item) {
        if(listUri.size()>0){
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Subiendo...");
            mDialog.show();

            for (int i = 0; i < listUri.size(); i++) {
                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("images/"+imageName);
                final int finalI = i;
                imageFolder.putFile(listUri.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override

                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mDialog.dismiss();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if(item!=null)
                                        {
                                            uploadAtLeastonePhoto=true;
                                            item.addUrlImg(uri.toString());
                                            //foodList.child(key).child("listImage").setValue(newProduct.getListImage());
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Toast.makeText(ProductList.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress =(100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                //mDialog.setMessage("Upload"+progress+"%");
                                //mDialog.setMessage("Subido "+ finalI+"/" +listUri.size());
                            }
                        })
                ;
            }
        }
        else {
            Snackbar.make(getWindow().getDecorView(), "Selecciona al menos una imagen", Snackbar.LENGTH_SHORT).show();
        }
    }
    private void loadListFood(String categoryId) {
        if(currentStore==null)
        {
            adapter = new FirebaseRecyclerAdapter<Product,ProductViewHolder>(
                    Product.class
                    ,R.layout.product_item,
                    ProductViewHolder.class,
                    foodList.orderByChild("categoryId").equalTo(categoryId)) { // like : select * from foods where menuid =

                @Override
                protected void populateViewHolder(ProductViewHolder viewHolder, final Product model, int position) {
                    viewHolder.product_name.setText(model.getName());
                    Picasso.with(getBaseContext())
                            .load(model.getListImage().get(0))
                            .into(viewHolder.product_image);
                    //final Product local = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //start new activity
                            //Toast.makeText(ProductList.this,local.getName(),Toast.LENGTH_SHORT).show();
                            Intent producDetail = new Intent(ProductList.this,ProductDetail.class);
                            //producDetail.putExtra("ProductId",adapter.getRef(position).getKey());
                            producDetail.putExtra("PRODUCT",model);
                            //SE PUEDE MEJORAR ESTO SI LE PASAMOS EL MODELO PRODUCTO IMPLEMENTANDO EL SERIALISABLE
                            startActivity(producDetail);
                        }
                    });
                }

            };

            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        } else {
            clientAdapter = new FirebaseRecyclerAdapter<Product,ClientProductViewHolder>(
                    Product.class
                    ,R.layout.product_item,
                    ClientProductViewHolder.class,
                    foodList.orderByChild("categoryId").equalTo(categoryId)) { // like : select * from foods where menuid =

                @Override
                protected void populateViewHolder(ClientProductViewHolder viewHolder, final Product model, int position) {
                    viewHolder.product_name.setText(model.getName());
                    Picasso.with(getBaseContext())
                            .load(model.getListImage().get(0))
                            .into(viewHolder.product_image);
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent producDetail = new Intent(ProductList.this,ProductDetail.class);
                            //producDetail.putExtra("ProductId",adapter.getRef(position).getKey());
                            producDetail.putExtra("PRODUCT",model);
                            //SE PUEDE MEJORAR ESTO SI LE PASAMOS EL MODELO PRODUCTO IMPLEMENTANDO EL SERIALISABLE
                            startActivity(producDetail);
                        }
                    });
                }

            };

            clientAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(clientAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //CropperImg
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
                listUri.add(result.getUri());
                updateImg();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

            if(item.getTitle().equals(Common.UPDATE))
            {
                showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
            } else if (item.getTitle().equals(Common.DELETE)){
                deleteFood(adapter.getRef(item.getOrder()).getKey());
            } else if (item.getTitle().equals(Common.OFFER)){
                showOfferProductDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
            }

        return super.onContextItemSelected(item);
    }



    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Product item) {
        //REMOVER la lista de urls aunque no se deberia
        item.clearUrl();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductList.this);
        alertDialog.setTitle("Editar Producto");
        //alertDialog.setMessage("Porfavor llenar toda la informacion");

        listUri = new ArrayList<>();
        listaImageView = new ArrayList<>();
        uploadAtLeastonePhoto=false;

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_product_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);

        //Binding MultipleImg
        img1 = (ImageView) add_menu_layout.findViewById(R.id.img1);
        img2 = (ImageView) add_menu_layout.findViewById(R.id.img2);
        img3 = (ImageView) add_menu_layout.findViewById(R.id.img3);
        img4 = (ImageView) add_menu_layout.findViewById(R.id.img4);
        img_remove_last = (ImageView) add_menu_layout.findViewById(R.id.img_remove_last);
        layout_img = (LinearLayout) add_menu_layout.findViewById(R.id.layout_img);
        listaImageView.add(img1);
        listaImageView.add(img2);
        listaImageView.add(img3);
        listaImageView.add(img4);
        layout_img.setOnClickListener(this);
        img_remove_last.setOnClickListener(this);


        //Set default value for view
        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());

        ;
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload);

        //Event for button
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changeImage(item); //Let user select image from gallery and save Uri of this image
                uploadImage(item);
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_menu);

        //Set button
        //alertDialog.show();
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();

                //update Information
                if(validate())
                {
                    item.setName(edtName.getText().toString());
                    item.setPrice(edtPrice.getText().toString());
                    item.setDescription(edtDescription.getText().toString());
                    //item.setCategoryId(currentCategory.getCategoryId());
                    //item.setPrincipalCategory(currentCategory.getPrincipalCategory());
                    item.setProductId(key);
                    foodList.child(key).setValue(item);
                    Snackbar.make(rootLayout,"Producto" + item.getName() +"fue editada", Snackbar.LENGTH_SHORT)
                            .show();
                }



            }
        });alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showOfferProductDialog(final String key, final Product item) {
        //REMOVER la lista de urls aunque no se deberia
        item.clearUrl();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductList.this);
        alertDialog.setTitle("Ofertar Producto");


        LayoutInflater inflater = this.getLayoutInflater();
        final View view_dialog = inflater.inflate(R.layout.add_new_offer_layout,null);

        edt_discount = view_dialog.findViewById(R.id.edt_discount);
        daysButton = (ElegantNumberButton) view_dialog.findViewById(R.id.days_button);


        //valite number discount
        edt_discount.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){
                String strEnteredVal = edt_discount.getText().toString();

                edt_discount.removeTextChangedListener(this);
                if(!strEnteredVal.equals("")){
                    int num=Integer.parseInt(strEnteredVal);
                    if(num<99 && num>0){
                        edt_discount.setText(""+num);
                        int pos = edt_discount.getText().length();
                        edt_discount.setSelection(pos);
                    }else{
                        Snackbar.make(view_dialog,"El rango es de 1-99", Snackbar.LENGTH_SHORT)
                                .show();
                        edt_discount.setText("");
                    }
                }
                edt_discount.addTextChangedListener(this);

            }
        });

            //Set default value for view


        alertDialog.setView(view_dialog);
        alertDialog.setIcon(R.drawable.ic_menu);

        //Set button
        //alertDialog.show();
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.dismiss();

                //update Information
                if(validateOffer())
                {
                    long actualTime= System.currentTimeMillis()+ (Integer.parseInt(daysButton.getNumber())*86400000);
                    foodList.child(key).child("offerExpire").setValue(actualTime);
                    foodList.child(key).child("discount").setValue(edt_discount.getText().toString());
                    Snackbar.make(rootLayout,"Producto " + item.getName() +"fue ofertado", Snackbar.LENGTH_SHORT)
                            .show();
                }

            }
        });alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private boolean validateOffer() {
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_remove_last:
                removeLastImg();
                break;
            case R.id.layout_img:
                if (listUri.size() < 4) {
                    CameraOpen();
                }
                break;
        }
    }

    private void CameraOpen() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    private void removeLastImg() {
        if(listUri.size()>0){
            listaImageView.get(listUri.size()-1).setImageResource(R.drawable.addimg);
            listUri.remove(listUri.size()-1);
            updateImg();
        }
    }

    private void updateImg() {
        if(listUri.size()>0) {
            for (int i = 0; i < listUri.size(); i++) {
                listaImageView.get(i).setImageURI(listUri.get(i));
            }
        }
    }
}
