package com.vam.groceryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import org.w3c.dom.Text;

import cdflynn.android.library.checkview.CheckView;

public class OrderDetails_Page extends AppCompatActivity {

    Button backHome;
    TextView tv_OrderId;
    String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details__page);

        orderId = getIntent().getStringExtra("OrderId");

        tv_OrderId = findViewById(R.id.tv_Orderid);
        backHome = findViewById(R.id.backhome_btn);
//LottieAnimationView animation = findViewById(R.id.progressBar);
//animation.cancelAnimation();

        tv_OrderId.setText(orderId);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetails_Page.this,HomePage.class);
                startActivity(intent);
            }
        });


    }
}
