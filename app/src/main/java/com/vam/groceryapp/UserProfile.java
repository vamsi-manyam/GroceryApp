package com.vam.groceryapp;

import android.app.Dialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vam.groceryapp.Prevalent.Prevalent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class UserProfile extends AppCompatActivity {

    Button usersave_btn,changeaddressbtn;
    EditText etusername;
    TextView tv_useraddress;
    Dialog dialog;
    //these are the ones in dialog popup
    EditText address_address,address_name,address_number,address_city,address_state,address_pincode;

    DatabaseReference rootRef,userRef;
    HashMap<String,Object> userprofilemap = new HashMap<>();
    String userid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userid = "7661937739";
        userid = Prevalent.currentUser.getNumber();
        rootRef  = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users").child(userid);


        etusername = findViewById(R.id.etuser_name);
        usersave_btn = findViewById(R.id.usersave_btn);
        changeaddressbtn = findViewById(R.id.changeaddress_btn);
        tv_useraddress = findViewById(R.id.tv_useraddress);


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.address_popup);
        address_address = (EditText) dialog.findViewById(R.id.address_address);
        address_number = (EditText) dialog.findViewById(R.id.address_number);
        address_city = (EditText) dialog.findViewById(R.id.address_city);
        address_pincode = (EditText) dialog.findViewById(R.id.address_pincode);
        address_state = (EditText) dialog.findViewById(R.id.address_state);
        address_name = (EditText) dialog.findViewById(R.id.address_name);

        Button save_address = (Button) dialog.findViewById(R.id.save_address);

        save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = address_name.getText().toString()+","
                        +address_address.getText().toString()+" ,"
                        +address_city.getText().toString()+" ,"
                        +address_state.getText().toString()+","
                        +address_pincode.getText().toString();
                Toast.makeText(UserProfile.this,address , Toast.LENGTH_SHORT).show();
                tv_useraddress.setText(address);
                dialog.dismiss();
            }
        });


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name,number,address;
                name = dataSnapshot.child("Name").getValue(String.class);
                number = dataSnapshot.child("Phone").getValue(String.class);
                address = dataSnapshot.child("Address").getValue(String.class);

//                Log.e("num",number);
                etusername.setText(name);
                tv_useraddress.setText(address);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changeaddressbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


        usersave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserData();
            }

            private void saveUserData() {
                //loader

                //validate inputboxes;
                String name,number,address;
                name= etusername.getText().toString();
                address = tv_useraddress.getText().toString();


                userRef.child("Name").setValue(name);
                userRef.child("Address").setValue(address);
                //save user details
                Toast.makeText(UserProfile.this, "Details Saved successfully", Toast.LENGTH_SHORT).show();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
