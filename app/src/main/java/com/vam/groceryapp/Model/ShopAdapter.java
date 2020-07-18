package com.vam.groceryapp.Model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vam.groceryapp.HomePage;
import com.vam.groceryapp.Prevalent.Prevalent;
import com.vam.groceryapp.R;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopProductViewHolder> {

    private Context mCtx;
    private List<ShopModel> shopModelList;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference userCartref;

    public ShopAdapter(Context mCtx, List<ShopModel> shopModelList) {
        this.mCtx = mCtx;
        this.shopModelList = shopModelList;
    }

    @NonNull
    @Override
    public ShopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.shop_card,null);
        return new ShopProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopProductViewHolder holder, int position) {
        final ShopModel shopModel = shopModelList.get(position);
        userCartref = mDatabase.child(Prevalent.currentUser.getNumber()).child("Cart");

        holder.shop_name.setText(shopModel.getShopname());
        holder.shop_num.setText(shopModel.getShopphone());
        holder.shop_address.setText(shopModel.getShopaddress());
        holder.shop_dist.setText(String.valueOf(shopModel.getShopdist()));

        holder.shop_viewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String crntShop = shopModel.getShopphone();
               //delete the current usercart from database;
                userCartref.removeValue();
               HomePage homePageInst = HomePage.getHomePageInst();
                Toast.makeText(homePageInst, "new shop :"+crntShop, Toast.LENGTH_SHORT).show();
               homePageInst.crntShop=crntShop;
               homePageInst.loadMainHomePage();
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopModelList.size();
    }

    class ShopProductViewHolder extends  RecyclerView.ViewHolder{

        TextView shop_name,shop_num,shop_address,shop_dist;
        Button shop_viewbtn;

        public ShopProductViewHolder(@NonNull View itemView) {
            super(itemView);

            shop_name = itemView.findViewById(R.id.shop_name);
            shop_num = itemView.findViewById(R.id.shop_phone);
            shop_address = itemView.findViewById(R.id.shop_address);
            shop_dist = itemView.findViewById(R.id.shop_distance);
            shop_viewbtn = itemView.findViewById(R.id.shop_viewbtn);

        }
    }
}
