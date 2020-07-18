package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginPage extends AppCompatActivity {

    ProgressDialog loadingbar;
    EditText et_num,et_pwd;
    TextView admin,not_admin;
    String parentDb = "Users";
    CheckBox rmbrMe;
    Button loginbtn;
    public HashMap<Integer,String> local_hm=new HashMap<Integer,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        et_num = findViewById(R.id.et_number);
        et_pwd= findViewById(R.id.et_pwd);
        loginbtn = findViewById(R.id.login_btn);
        rmbrMe = findViewById(R.id.checkBox);
        admin = findViewById(R.id.tv_admin);
        not_admin = findViewById(R.id.tv_notadmin);
        Paper.init(this);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login as Admin");
                admin.setVisibility(View.INVISIBLE);
                not_admin.setVisibility(View.VISIBLE);
                parentDb="Shops";
            }
        });

        not_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbtn.setText("Login");
                not_admin.setVisibility(View.INVISIBLE);
                admin.setVisibility(View.VISIBLE);
                parentDb="Users";
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {

        loadingbar = new ProgressDialog(this);
        String number = et_num.getText().toString();
        String pwd = et_pwd.getText().toString();

        if(TextUtils.isEmpty(number)){
            Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Login");
            loadingbar.setMessage("Please wait,Checking details");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            accessAccount(number,pwd);
        }

    }

    private void accessAccount(final String number, final String pwd) {

        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference().child(parentDb);


        Log.e("e","one");
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(number).exists()){
                    String num = dataSnapshot.child(number).child("Phone").getValue(String.class);
                    String pass = dataSnapshot.child(number).child("Password").getValue(String.class);
                    String name = dataSnapshot.child(number).child("Name").getValue(String.class);
                    User myuser = new User(name,num,pass,local_hm);

                    if(pwd.equals(pass)){
                        if(rmbrMe.isChecked()){
                            Paper.book().write(Prevalent.Userphonekey,number);
                            Paper.book().write(Prevalent.Userpasskey,pwd);
                            Paper.book().write(Prevalent.Usercart,local_hm);
                        }
                        if(parentDb=="Users"){

                            Toast.makeText(LoginPage.this, "Successfull", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();

                            Prevalent.currentUser = myuser;

                            Intent myint = new Intent(LoginPage.this,HomePage.class);
                            startActivity(myint);
                        }
                        else if(parentDb=="Shops"){

                            Toast.makeText(LoginPage.this, "Shop owner login Successfull", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();

                            Intent myint = new Intent(LoginPage.this,OwnerMainpage.class);
                            myint.putExtra("Shop",number);
                            startActivity(myint);
                        }
                    }
                    else {
                        loadingbar.dismiss();
                        Toast.makeText(LoginPage.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    loadingbar.dismiss();
                    Toast.makeText(LoginPage.this, "Account Not Found With that number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("e","error occured");
            }
        });
    }
}
//
//

