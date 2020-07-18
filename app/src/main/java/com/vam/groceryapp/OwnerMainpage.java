package com.vam.groceryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerMainpage extends AppCompatActivity {
    String category;
    ImageView cooking,packaged,household,beauty,misc,other;
    FloatingActionButton addnew;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_mainpage);

        cooking = findViewById(R.id.cooking_essentials);
        packaged = findViewById(R.id.Packaged_Food);
        household = findViewById(R.id.Household_Supplies);
        beauty = findViewById(R.id.Beauty_Grooming);
        misc = findViewById(R.id.Miscellaneous);
        other = findViewById(R.id.Others);
        addnew = findViewById(R.id.addnew_btn);

        number = getIntent().getExtras().getString("Shop");
//        cooking.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

    addnew.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(OwnerMainpage.this, "clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OwnerMainpage.this,AddNewProduct.class);
            Intent samplint = new Intent(OwnerMainpage.this,sample.class);
            startActivity(intent);
           // startActivity(samplint);
        }
    });

    }

    public void categorySelect(View view) {

        switch (view.getId()){
            case R.id.cooking_essentials:  category="Cooking Essentials";
            break;
            case R.id.Packaged_Food:  category="Packaged Food";
                break;
            case R.id.Beauty_Grooming:  category="Beauty and Grooming";
                break;
            case R.id.Household_Supplies:  category="Household Supplies";
                break;
            case R.id.Miscellaneous:  category="Miscellaneous";
                break;
            case R.id.Others:  category="Others";
                break;
        }

        Intent intent = new Intent(OwnerMainpage.this,AddProductsPage.class);
                intent.putExtra("Category",category);
                intent.putExtra("Shop",number);
               startActivity(intent);


    }
}
