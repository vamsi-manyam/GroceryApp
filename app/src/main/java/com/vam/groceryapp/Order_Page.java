package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vam.groceryapp.Prevalent.Prevalent;

import java.util.HashMap;

public class Order_Page extends AppCompatActivity {

    Button add_address,home_address,place_order;
    Dialog dialog;
    TextView tv_address,tv_total,tv_finalcost,tv_shopid;
    EditText address_address,address_name,address_number,address_city,address_state,address_pincode;
    String shopname;
    HashMap <String,Object> Orderdatamap = new HashMap<>();
    DatabaseReference rootRef,orderRef;
    LottieAnimationView loadingView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__page);

        rootRef  = FirebaseDatabase.getInstance().getReference();
        orderRef = rootRef.child("Orders");

        int delivery = 0;
        final String total = getIntent().getStringExtra("total");
        shopname = getIntent().getStringExtra("shopname");
        Log.e("total", total+shopname);
        Log.e("cart size", String.valueOf(Prevalent.currentUser.getCart_hm().size()));

        loadingView = findViewById(R.id.loadinganim);
        loadingView.setVisibility(View.INVISIBLE);

        tv_shopid = findViewById(R.id.tv_shopid);
        tv_total = findViewById(R.id.tvtotal_cost);
        tv_finalcost = findViewById(R.id.tv_finalcost);
        add_address = findViewById(R.id.add_address);
        home_address = findViewById(R.id.home_address);
        tv_address = findViewById(R.id.tv_address);
        place_order = findViewById(R.id.order_cnfmbtn);

        tv_total.setText(total);
        tv_finalcost.setText(total);
        tv_shopid.setText(shopname);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.address_popup);
        address_address = (EditText) dialog.findViewById(R.id.address_address);
        address_number = (EditText) dialog.findViewById(R.id.address_number);
        address_city = (EditText) dialog.findViewById(R.id.address_city);
        address_pincode = (EditText) dialog.findViewById(R.id.address_pincode);
        address_state = (EditText) dialog.findViewById(R.id.address_state);
        address_name = (EditText) dialog.findViewById(R.id.address_name);

        Button save_address = (Button) dialog.findViewById(R.id.save_address);


        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        home_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = address_name.getText().toString()+","
                        +address_address.getText().toString()+" ,"
                        +address_city.getText().toString()+" ,"
                        +address_state.getText().toString()+","
                        +address_pincode.getText().toString();
                Toast.makeText(Order_Page.this,address , Toast.LENGTH_SHORT).show();
                tv_address.setText(address);
                dialog.dismiss();
            }
        });

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                place_order.setEnabled(false);
                loadingView.setVisibility(View.VISIBLE);
                loadingView.playAnimation();
                Orderdatamap.put("UserId",Prevalent.currentUser.getNumber());
                Orderdatamap.put("DeliveryAddress",tv_address.getText().toString());
                Orderdatamap.put("ShopId",shopname);
                Orderdatamap.put("Total",total);
                Orderdatamap.put("Status","Pending");

                final DatabaseReference myCartref=rootRef.child("Users").child(Prevalent.currentUser.getNumber()).child("Cart");
                myCartref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("cartproductsinorderpage", String.valueOf(dataSnapshot.getValue()));
                        Orderdatamap.put("Products",dataSnapshot.getValue());
                        myCartref.removeValue();
                        Prevalent.currentUser.clearHashMap();
                        storeOrderinDB();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            private void storeOrderinDB() {


                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String orderId="null";
                        long n = dataSnapshot.getChildrenCount();
                        orderId = "OD"+shopname+(n+1);
                        orderRef.child(orderId).updateChildren(Orderdatamap);

                        DatabaseReference userOrderref=rootRef.child("Users").child(Prevalent.currentUser.getNumber()).child("MyOrders");
                        userOrderref.push().setValue(orderId);
                        rootRef.child("Shops").child(shopname).child("MyOrders").push().setValue(orderId);

                        Toast.makeText(Order_Page.this, "Order Placed", Toast.LENGTH_SHORT).show();
                        loadingView.cancelAnimation();

                        Intent intent = new Intent(Order_Page.this,OrderDetails_Page.class);
                        intent.putExtra("OrderId",orderId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Order_Page.this.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
    }


}
