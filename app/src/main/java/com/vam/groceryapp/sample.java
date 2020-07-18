package com.vam.groceryapp;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vam.groceryapp.Model.ShopAdapter;
import com.vam.groceryapp.Model.ShopModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class sample extends AppCompatActivity{

    RecyclerView srecyclerView;
    ShopAdapter sadapter;
    List<ShopModel> shopslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        shopslist = new ArrayList<>();
        srecyclerView = findViewById(R.id.shops_recyclerv);
        srecyclerView.setHasFixedSize(true);
        srecyclerView.setLayoutManager(new LinearLayoutManager(this));

        shopslist.add(new ShopModel("new shop","8919131367","addresss is here","100km"));
        shopslist.add(new ShopModel("new shop","8919131367","addresss is here","3km"));
        shopslist.add(new ShopModel("new shop","8919131367","addresss is here","0km"));


        sadapter = new ShopAdapter(this,shopslist);
        srecyclerView.setAdapter(sadapter);
    }
}