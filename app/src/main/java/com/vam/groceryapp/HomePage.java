package com.vam.groceryapp;

import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vam.groceryapp.Model.ShopAdapter;
import com.vam.groceryapp.Model.ShopModel;
import com.vam.groceryapp.Model.UserProduct;
import com.vam.groceryapp.Model.UserProductAdapter;
import com.vam.groceryapp.Prevalent.Prevalent;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    UserProductAdapter adapter;
    List<UserProduct> productList = new ArrayList<>();
    DatabaseReference myRef,allproducts,crntShopref,allshopsRef;
    public String crntShop;
    final List<String> pids = new ArrayList<>();
    private static HomePage homePageInst;

    public DataSnapshot allproductssnapshot;
    private String category;
    LottieAnimationView mainloaderanim;


    //for shop list
    RecyclerView srecyclerView;
    ShopAdapter sadapter;
    List<ShopModel> shopslist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton cart_floatingbtn = findViewById(R.id.cart_floatingbtn);

        homePageInst=this;
        Paper.init(this);
        myRef = FirebaseDatabase.getInstance().getReference();


        cart_floatingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.e("before", String.valueOf(productList.size()));
                //productList.clear();
                Log.e("after", String.valueOf(productList.size()));
                //loadShopproducts();
                Intent intent = new Intent(HomePage.this,CartPage.class);
                intent.putExtra("shopname",crntShop);
                startActivity(intent);
            }
        });

        mainloaderanim = findViewById(R.id.mainloaderanim);
        mainloaderanim.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView profileimg = headerView.findViewById(R.id.profileimage);
        TextView username = headerView.findViewById(R.id.username);
        username.setText(Prevalent.currentUser.getName());

        Log.e("cart size", String.valueOf(Prevalent.currentUser.getCart_hm().size()));

        //setting up shop list
        shopslist = new ArrayList<>();
        srecyclerView = findViewById(R.id.shops_recyclerview);
        srecyclerView.setHasFixedSize(true);
        srecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        allshopsRef = myRef.child("Shops");
        allshopsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String sname,sphone,saddress,sid,sdist;
                    sid = ds.getKey();
                    sname= ds.child("ShopName").getValue(String.class);
                    sphone = ds.child("Phone").getValue(String.class);
                    saddress = ds.child("Address").getValue(String.class);
                    sdist = ds.child("LatLng").getValue(String.class);
                    //here calculate the dist b/w user loc and shop and if within range then add to shoplist
                    shopslist.add(new ShopModel(sname,sphone,saddress,sdist));
                }
                sadapter = new ShopAdapter(homePageInst,shopslist);
                srecyclerView.setAdapter(sadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        //for products list
        recyclerView =findViewById(R.id.userrecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //defining variable of the content_home_page
        allproducts = myRef.child("AllProducts");
        crntShop="8919131367";
        category = "Cooking Essentials";
       // loadMainHomePage();//this is main function which displays the list of products((())) This should be called whenever we change category or shop

        //load allproducts datasnapshot
        loadAllproductsSnapshot();


    }

    private void loadAllproductsSnapshot() {
        allproducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allproductssnapshot = dataSnapshot;
                Toast.makeText(HomePage.this, "all products have been loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void loadMainHomePage() {
        //loading progress bar
        mainloaderanim.setVisibility(View.VISIBLE);
        pids.clear();
        productList.clear();
        crntShopref = myRef.child("Shops").child(crntShop).child("Products").child(category);
        //already i have the datasnapshot of allproducts in allproductsdnapshot
        loadShopproducts();

    }

    private void loadShopproducts() {
        crntShopref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long n =dataSnapshot.getChildrenCount();
                Log.e("size", String.valueOf(n));
                for (long i =0;i<n;i++){
                    // Log.e("id",dataSnapshot.child(String.valueOf(i+1)).getValue(String.class));
                    pids.add(dataSnapshot.child(String.valueOf(i+1)).getValue(String.class));
                }
                //till here only ids of the current shop are being stored in pids
                //next we add these products into productlist by joining details from allproducts data
                addtoProductlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addtoProductlist() {

//        Log.e("allproducts",allproductssnapshot.toString());
        HashMap<Integer,String> local_hm=new HashMap<Integer,String>();

        //here for each id in current shop ids details are gathered from allproductsnapshot and stored to productList
        for(String id : pids){
            Log.e("ids",id);
            String pname,desc,qty,price,img;

                    pname = allproductssnapshot.child(String.valueOf(id)).child("PName").getValue(String.class);
                    price = allproductssnapshot.child(String.valueOf(id)).child("Price").getValue(String.class);
                    desc = allproductssnapshot.child(String.valueOf(id)).child("Desc").getValue(String.class);
                    img = allproductssnapshot.child(String.valueOf(id)).child("imageurl").getValue(String.class);
                    qty = allproductssnapshot.child(String.valueOf(id)).child("QType").getValue(String.class);

             productList.add(new UserProduct(Integer.valueOf(id),pname,desc,qty,price,img));
            //check if the id is present in my cart if there then keep it else delete
            if(Prevalent.currentUser.getCart_hm().containsKey(Integer.valueOf(id)) && Prevalent.currentUser.getCart_hm().get(Integer.valueOf(id))!="0"){
                Log.e("contains in new cart",id+" "+Prevalent.currentUser.getCart_hm().get(Integer.valueOf(id)));
                local_hm.put(Integer.valueOf(id),Prevalent.currentUser.getCart_hm().get(Integer.valueOf(id)));
            }

        }
        productList.add(new UserProduct(100,"endname","enddesc","endqty","endprice","endimg"));
        Prevalent.currentUser.setCart_hm(local_hm);
        Paper.book().write(Prevalent.Usercart,local_hm);

        loadlocalCart();
        setHomepagelist();


    }

    public void loadlocalCart(){
        Log.e("size of product category", String.valueOf(productList.size()));
        //iterate through all the products in product list (OR) iterate through the cart in database and load those ids to localcart_hm
        //the above line is not used because im adding the products to the Prevalant.usercart during setting up adapter
    }

    private void setHomepagelist() {
        mainloaderanim.setVisibility(View.INVISIBLE);
        adapter = new UserProductAdapter(this,productList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(this, "Are you sure you want to exit?", Toast.LENGTH_SHORT).show();
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action

        }
        else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(HomePage.this,UserProfile.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_slideshow) {

        }
        else if (id == R.id.nav_tools) {
            Intent intent = new Intent(HomePage.this,MyOrderList.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_share) {
            Intent intent = new Intent(HomePage.this,UserProfile.class);
            startActivity(intent);
        }
        else if (id == R.id.logout) {
            Paper.book().destroy();
            Intent intent = new Intent(HomePage.this,LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public static HomePage getHomePageInst(){
        return homePageInst ;
    }
}
