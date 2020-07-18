package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignupPage extends AppCompatActivity {

    TextView et_pwd,et_name,et_number;

    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        Button register_btn=(Button) findViewById(R.id.register_btn);
        et_name = (TextView)findViewById(R.id.signup_et_name);
        et_number = (TextView)findViewById(R.id.signup_et_number);
        et_pwd = (TextView)findViewById(R.id.signup_et_pwd);




        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private void createAccount() {
        String name = et_name.getText().toString();
        String num = et_number.getText().toString();
        String pwd = et_pwd.getText().toString();

        loadingbar = new ProgressDialog(this);

        if(TextUtils.isEmpty(name))
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        else
            if(TextUtils.isEmpty(num))
                Toast.makeText(this, "Please enter number", Toast.LENGTH_SHORT).show();
            else if(TextUtils.isEmpty(pwd))
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            else{
                loadingbar.setTitle("Create Account");
                loadingbar.setMessage("Please wait,while we are checking credentials");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                validateNumber(name,num,pwd);

            }
    }

    private void validateNumber(final String name,final String num,final String pwd) {
        Log.e("det",name+" "+num+" "+pwd);

        final HashMap <String,Object> userdatamap = new HashMap<>();
        userdatamap.put("Phone",num);
        userdatamap.put("Name",name);
        userdatamap.put("Password",pwd);

        final DatabaseReference Rootref = FirebaseDatabase.getInstance().getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(num).exists())){
                    Rootref.child("Users").child(num).updateChildren(userdatamap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        loadingbar.dismiss();
                                        Toast.makeText(SignupPage.this, "Account Created Succesfully,Please Login", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupPage.this,LoginPage.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        loadingbar.dismiss();
                                        Toast.makeText(SignupPage.this, "Error, Please try again", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(SignupPage.this, task.toString(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }
                else {
                    loadingbar.dismiss();
                    Toast.makeText(SignupPage.this, "An account with this number already exists", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


}
