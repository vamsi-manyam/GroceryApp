package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vam.groceryapp.Model.OwnerProduct;
import com.vam.groceryapp.Model.OwnerProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AddProductsPage extends AppCompatActivity {

    String category;
    DatabaseReference myRef;
    String shopid= "8919131367";
    int count;
    RecyclerView recyclerView;
    OwnerProductAdapter productAdapter;
    List<OwnerProduct> productList;
     Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products_page);
        category=getIntent().getExtras().getString("Category");
        shopid = getIntent().getExtras().getString("Shop");


        productList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.ownerrecyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ctx=this;

        final List<String> shoplist = new ArrayList<String>();
        final HashMap<String,Boolean> allcategoryproducts = new HashMap<String,Boolean>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        myRef = database.getReference();

         DatabaseReference myShopCategory = myRef.child("Shops").child(shopid).child("Products").child(category);
         final DatabaseReference ProductCategory = myRef.child("ProductsCategory").child(category);
        final DatabaseReference allProducts   = myRef.child("AllProducts");

         //Store the ids of all the prodcuts in myShopCategory in a mylist


//
        myShopCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long n= dataSnapshot.getChildrenCount();
                count=(int)dataSnapshot.getChildrenCount();
                for(int i =0;i<n;i++){
                    shoplist.add(dataSnapshot.child(String.valueOf(i+1)).getValue(String.class));
                    //Log.e("c",shoplist.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }
);
//
        // Now iterate through all the ids in ProductCategory and check with the above mylist and render a listview:: if id is present in mylist enable checkbox

        ProductCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long n=dataSnapshot.getChildrenCount();

                for(int i=0;i<n;i++){
                    String pid = dataSnapshot.child(String.valueOf(i+1)).getValue(String.class);
                  // Log.e("pid",pid);
                    Log.e("c","inside");
                    //shoplist contains products of current category of this shop
                    if(shoplist.contains(pid)){
                        //Log.e("k","found");
                        allcategoryproducts.put(pid,true);
                    }
                    else
                    {
                        allcategoryproducts.put(pid,false);
                        //Log.e("k","not found");
                    }

                }
                loadProductList();
                loadRecycler();
            }



            private void loadProductList(){
                Log.e("c","outside");


                allProducts.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productList.clear();
                        //allcategoryproducts contains all the products of the entire category and the corresponding boolean if present in this shop

                        for(Map.Entry m:allcategoryproducts.entrySet()){
                            String id = String.valueOf(m.getKey());
                            //Log.e("p",id+" "+m.getValue());
                            String PName,Price,qtype,desc,imageurl;
                            PName = dataSnapshot.child(id).child("PName").getValue(String.class);
                            Price = dataSnapshot.child(id).child("Price").getValue(String.class);
                            qtype = dataSnapshot.child(id).child("QType").getValue(String.class);
                            desc = dataSnapshot.child(id).child("Desc").getValue(String.class);
                            imageurl = dataSnapshot.child(id).child("imageurl").getValue(String.class);
                            boolean ch = Boolean.valueOf(String.valueOf(m.getValue()));
                            Log.e("p",PName+" "+Price+" "+qtype+" "+ch+" "+imageurl);
                            productList.add(new OwnerProduct(Integer.valueOf(id),PName,Price,qtype,ch,imageurl,desc));
                            Log.e("s","new product added");
                        }
                        loadRecycler();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            private void loadRecycler() {
                productAdapter = new OwnerProductAdapter(ctx,productList,shopid,category,count);
                recyclerView.setAdapter(productAdapter);
                Log.e("s","sorry already sent");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });






       // Toast.makeText(this, category, Toast.LENGTH_SHORT).show();
    }


}
