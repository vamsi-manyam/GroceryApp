package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddNewProduct extends AppCompatActivity {

    Button addproduct;
    EditText pprice,pname,pdesc;
    Spinner category_select,qtypeselect;
    ImageView image;
    private static final int GALLERY_PICK=1;
    private Uri Imageuri;
    private StorageReference mFolder;
    String category="",qtype="";
    DatabaseReference mRef;
    String name,price,desc;

    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        mRef = FirebaseDatabase.getInstance().getReference();
        loadingbar = new ProgressDialog(this);


        pprice=findViewById(R.id.et_price);
        pdesc=findViewById(R.id.et_pdesc);
        pname=findViewById(R.id.et_pname);
        addproduct=findViewById(R.id.addproduct_btn);
        image=findViewById(R.id.upload_image);
        qtypeselect= findViewById(R.id.qtype_select);
        category_select= findViewById(R.id.category_select);
        mFolder= FirebaseStorage.getInstance().getReference().child("Product_Images");
        final List<String> categories = new ArrayList<>();
        List<String> qtypes = new ArrayList<>();
        categories.add("Cooking Essentials");
        categories.add("Packaged Food");
        categories.add("Beauty and Grooming");
        categories.add("Household Supplies");
        categories.add("Miscellaneous");
        categories.add("Others");
        qtypes.add("Pkt");
        qtypes.add("Kg");
        qtypes.add("Grams");
        qtypes.add("Qty");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categories);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,qtypes);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        
        category_select.setAdapter(adapter);
        category_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item_value= parent.getItemAtPosition(position).toString();
                Toast.makeText(AddNewProduct.this, item_value, Toast.LENGTH_SHORT).show();
                category=item_value;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        qtypeselect.setAdapter(adapter2);
        qtypeselect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item_value= parent.getItemAtPosition(position).toString();
                Toast.makeText(AddNewProduct.this, item_value, Toast.LENGTH_SHORT).show();
                qtype=item_value;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateData();
            }
        });

    }

    private void openGallery() {
        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,GALLERY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==GALLERY_PICK && resultCode==RESULT_OK && data!=null){
            Imageuri = data.getData();
            image.setImageURI(Imageuri);

        }
    }


    private void validateData() {
         name= pname.getText().toString();
         price = pprice.getText().toString();
         desc = pdesc.getText().toString();

        if(category==""){
            Toast.makeText(this, "Product Category is missing ", Toast.LENGTH_SHORT).show();
        }
        else if(Imageuri==null){
            Toast.makeText(this, "Image is necessary", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(desc) ) {
            Toast.makeText(this, "Product Name is missing ", Toast.LENGTH_SHORT).show();
        }
        else if(qtype==""){
            Toast.makeText(this, "Product Qty Type is missing ", Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(this, "Uploadingg", Toast.LENGTH_SHORT).show();

            uploadImage();

        }


    }

    private void uploadImage() {

        loadingbar.setTitle("Add New Product");
        loadingbar.setMessage("Please wait,product is being uploaded");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        //Imageuri.getLastPathSegment()
        final StorageReference imageName = mFolder.child(String.valueOf(System.currentTimeMillis()));

        imageName.putFile(Imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AddNewProduct.this, "uploadedd", Toast.LENGTH_SHORT).show();
                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        addProductLink(uri);

                    }

                    private void addProductLink(Uri uri) {
                        final DatabaseReference allProducts = mRef.child("AllProducts");
                        final DatabaseReference pCategory = mRef.child("ProductsCategory").child(category);


                        final HashMap<String,Object> mMap = new HashMap<>();
                        mMap.put("PName",name);
                        mMap.put("Price",price);
                        mMap.put("Desc",desc);
                        mMap.put("QType",qtype);
                        mMap.put("imageurl", String.valueOf(uri));



                        allProducts.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final long pn = dataSnapshot.getChildrenCount();//pn is nothing but the pid-1;
                                allProducts.child(String.valueOf(pn+1)).updateChildren(mMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.e("p","AllProducts link added");
                                            //now add the pid to the appropriate category
                                            pCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    long n = dataSnapshot.getChildrenCount();
                                                    pCategory.child(String.valueOf(n+1)).setValue(String.valueOf(pn+1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Log.e("p","2nd linking also done");
                                                                loadingbar.dismiss();
                                                                Toast.makeText(AddNewProduct.this, "Product Has been added,please check the category section", Toast.LENGTH_SHORT).show();
                                                                //Intent intent = new Intent(AddNewProduct.this,AddProductsPage.class);
                                                                //intent.putExtra("Category",category);
                                                                //startActivity(intent);
                                                                pname.setText("");
                                                                pprice.setText("");
                                                                pdesc.setText("");
                                                                category="";
                                                                qtype="";
                                                                image.setImageDrawable(getDrawable(R.drawable.addimage));
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    loadingbar.dismiss();
                                                    //error occured while getting datasnapshot of ProductsCategory
                                                    Toast.makeText(AddNewProduct.this, "Error Occured :"+String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                        else {
                                            loadingbar.dismiss();
                                            //error occured while uploading the link to AllProducts section
                                            Toast.makeText(AddNewProduct.this, "Error Occured in allproducts", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                loadingbar.dismiss();
                                //error occured while getting the data snapshot of AllProducts
                                Toast.makeText(AddNewProduct.this, "Error Occured :"+String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingbar.dismiss();
                Toast.makeText(AddNewProduct.this, "Error While Uploading Image,Please try again", Toast.LENGTH_SHORT).show();
                Log.e("s", String.valueOf(e));
            }
        });
    }
}
