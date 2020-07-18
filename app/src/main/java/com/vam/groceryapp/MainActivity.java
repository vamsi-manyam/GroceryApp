package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vam.groceryapp.Model.User;
import com.vam.groceryapp.Prevalent.Prevalent;

import java.util.HashMap;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    ProgressDialog loadingbar;
    private static final int Internet_PERMISSION_CODE = 100;
    // private static final int STORAGE_PERMISSION_CODE = 101;
    String parentDb = "Users";
    int x=0;
    public  HashMap<Integer,String> local_hm=new HashMap<Integer,String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login_btn = findViewById(R.id.main_login_btn);
        Button join_btn = findViewById(R.id.main_signup_btn);
        Paper.init(this);
        loadingbar = new ProgressDialog(this);



        checkPermission(Manifest.permission.INTERNET, Internet_PERMISSION_CODE);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupPage.class);
                startActivity(intent);
            }
        });

        String phone = Paper.book().read(Prevalent.Userphonekey);
        String pwd = Paper.book().read(Prevalent.Userpasskey);
        local_hm = Paper.book().read(Prevalent.Usercart);


        if(phone!="" && pwd!=""){

            if(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)){

                loadingbar.setTitle("Already Logged In");
                loadingbar.setMessage("Please wait...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();


                    allowacces(phone,pwd);


            }
        }



    }
    private void allowacces(final String number, final String pwd) {
        if (x != 2) {
             if (x == 0)
                parentDb = "Users";
            else if (x == 1)
                parentDb = "Shops";



        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference().child(parentDb);

        Log.e("e", "one");
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(number).exists()) {
                    String num = dataSnapshot.child(number).child("Phone").getValue(String.class);
                    String pass = dataSnapshot.child(number).child("Password").getValue(String.class);
                    String name = dataSnapshot.child(number).child("Name").getValue(String.class);
                    User myuser = new User(name, num, pass,local_hm);

                    if (pwd.equals(pass)) {
                       if(parentDb=="Users"){
                           Toast.makeText(MainActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                           //Log.e("pass", myuser.getName());
                           loadingbar.dismiss();

                           Prevalent.currentUser = myuser;

                           Intent myint = new Intent(MainActivity.this, HomePage.class);
                           startActivity(myint);
                       }
                       else {
                           Toast.makeText(MainActivity.this, "Shop owner login Successfull", Toast.LENGTH_SHORT).show();
                           loadingbar.dismiss();

                           Intent myint = new Intent(MainActivity.this,OwnerMainpage.class);
                           myint.putExtra("Shop",number);
                           startActivity(myint);
                       }
                    } else {
                        loadingbar.dismiss();
                        //Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        x++;
                        allowacces(number, pwd);

                    }
                } else {
                    loadingbar.dismiss();
                    //Toast.makeText(MainActivity.this, "Account Not Found With that number", Toast.LENGTH_SHORT).show();
                    x++;
                    allowacces(number, pwd);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("e", "error occured");
            }
        });
     }
    }

    private void checkPermission(String permission, int permission_code) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, permission_code);
        } else {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Internet_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Internet Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please grant Internet Permission", Toast.LENGTH_SHORT).show();
               // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, Internet_PERMISSION_CODE);
            }
            }
        }
}
