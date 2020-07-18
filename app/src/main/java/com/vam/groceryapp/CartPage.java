package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vam.groceryapp.Model.CartProductAdapter;
import com.vam.groceryapp.Model.UserProduct;
import com.vam.groceryapp.Model.UserProductAdapter;
import com.vam.groceryapp.Prevalent.Prevalent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartPage extends AppCompatActivity {

    public TextView tv_shopname;
    public static TextView tv_total;
    private String ShopName;
    Button order_btn;
    RecyclerView recyclerView;
    CartProductAdapter adapter;
    List<UserProduct> productList;
    private DatabaseReference allproducts,myRef;
    DataSnapshot allproductssnapshot;
    HashMap<Integer,String> local_hm=new HashMap<Integer,String>();
    ImageButton back_btn;
    private ProgressDialog loadingbar;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference userCartref;
    private static CartPage CartPageInst;


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "back button pressed", Toast.LENGTH_SHORT).show();
        Intent backintent = new Intent(CartPage.this,HomePage.class);
        startActivity(backintent);
       // super.onBackPressed();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_page);

        CartPageInst = this;
        //for the popup address

        order_btn = findViewById(R.id.order_btn);
        back_btn = findViewById(R.id.back_btn);
        tv_shopname= findViewById(R.id.tv_shopname);
        tv_total = findViewById(R.id.tv_total);
        Intent intent=getIntent();
        ShopName = intent.getStringExtra("shopname");
        Log.e("shopname",ShopName);
        //Log.e("myshop", String.valueOf(Prevalent.currentUser.getCart_hm().size()));
        tv_shopname.setText(ShopName);
        local_hm=Prevalent.currentUser.getCart_hm();

        loadingbar = new ProgressDialog(this);
        loadingbar.setTitle("Create Account");
        loadingbar.setMessage("Please wait,while we are checking credentials");
        loadingbar.setCanceledOnTouchOutside(true);
        loadingbar.show();


        userCartref = mDatabase.child("9154243772").child("Cart");

        myRef = FirebaseDatabase.getInstance().getReference();
        allproducts = myRef.child("AllProducts");

        productList=new ArrayList<>();
        recyclerView =findViewById(R.id.cart_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //for snapping
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);

        allproducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allproductssnapshot = dataSnapshot;
                loadCartproducts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cart_size = Prevalent.currentUser.getCart_hm().size();
                Log.e("size", String.valueOf(cart_size));

                if(cart_size<=0){
                    Toast.makeText(CartPage.this, "Please add items before proceeding", Toast.LENGTH_SHORT).show();
                }
                else{
                    String total = tv_total.getText().toString();
                    Intent intent = new Intent(CartPage.this,Order_Page.class);
                    intent.putExtra("total",total);
                    intent.putExtra("shopname",ShopName);
                    startActivity(intent);
                }

            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backintent = new Intent(CartPage.this,HomePage.class);
                startActivity(backintent);
            }
        });

    }

    public void loadCartproducts() {
        //cart products are already present in currentUser.Cart_hm

        //loadCartproducts from database to local_carthm


        addtoProductlist();
    }

    private void addtoProductlist() {

        for(Map.Entry m:local_hm.entrySet()){
            Log.e("x",m.getKey()+"printt"+m.getValue());
            String pname,desc,qty,price,img;
            int id = (int) m.getKey();
            Log.e("number","helloo");
            pname = allproductssnapshot.child(String.valueOf(id)).child("PName").getValue(String.class);
            price = allproductssnapshot.child(String.valueOf(id)).child("Price").getValue(String.class);
            desc = allproductssnapshot.child(String.valueOf(id)).child("Desc").getValue(String.class);
            img = allproductssnapshot.child(String.valueOf(id)).child("imageurl").getValue(String.class);
            qty = allproductssnapshot.child(String.valueOf(id)).child("QType").getValue(String.class);

            productList.add(new UserProduct(Integer.valueOf(id),pname,desc,qty,price,img));
        }

        userCartref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot pid : dataSnapshot.getChildren()){
                   String  pname = allproductssnapshot.child(String.valueOf(pid.getKey())).child("PName").getValue(String.class);
                    Log.e("pidsss",pid.getKey()+": "+pname);
                   String img = allproductssnapshot.child(String.valueOf(pid.getKey())).child("imageurl").getValue(String.class);
                    productList.add(new UserProduct(Integer.valueOf(pid.getKey()),pname,"desc","qty","price",img));
                    Log.e("size", String.valueOf(productList.size()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loadHomepage();

    }

    private void loadHomepage() {
        adapter = new CartProductAdapter(this,productList);
        recyclerView.setAdapter(adapter);
       // this.tv_total.setText("aunty");
        loadingbar.dismiss();

    }

    public static CartPage getCartpageInstance(){

        return CartPageInst;
    }
    public  void displayToast(){
        Toast.makeText(this, "Toast displayed from cartproduct adapter", Toast.LENGTH_SHORT).show();
        Log.e("fromcartadapter",tv_total.getText().toString());
    }

    public static void sayhello(){
       // CartPage.displayToast();

        Log.e("woring","hello");
    }
}
