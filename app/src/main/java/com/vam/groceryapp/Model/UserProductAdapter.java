package com.vam.groceryapp.Model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vam.groceryapp.Prevalent.Prevalent;
import com.vam.groceryapp.R;

import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class UserProductAdapter extends RecyclerView.Adapter<UserProductAdapter.ProductViewHolder>{

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference userCartref;
    private Context mCtx;
    private List<UserProduct> productList;

    public UserProductAdapter(Context mCtx, List<UserProduct> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.buy_product_card,null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        if(position>=productList.size()-1){
            holder.tvName.setText("YOU Reached the END");
            return;
        }
        final UserProduct product = productList.get(position);
        Log.e("productpos", String.valueOf(position));
        holder.tvName.setText(product.getpName());
        holder.tvDesc.setText(product.getpDesc());
        holder.tvPrice.setText(product.getpPrice()+"/"+product.getqType());
        userCartref = mDatabase.child(Prevalent.currentUser.getNumber()).child("Cart");
        Picasso.get().load(product.getpImage()).into(holder.imageView);
       // holder.imageView.setImageDrawable(mCtx.getDrawable(R.drawable.icon));

//        if(Prevalent.currentUser.getCart_hm().containsKey(product.getPid()))
//        holder.qty_btn.setNumber(Prevalent.currentUser.getCart_hm().get(product.getPid()));
        userCartref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = String.valueOf(product.getPid());
                String qty="0";
                if(dataSnapshot.hasChild(id))
                 qty = dataSnapshot.child(id).getValue(String.class);
                Log.e("qty",qty);
                holder.qty_btn.setNumber(qty);

                //for setting up the cart_hm for rendering it in cartpage
                if(qty!="0"){
                    HashMap<Integer,String> local_hm=new HashMap<Integer,String>();
                    local_hm=Prevalent.currentUser.getCart_hm();
                    local_hm.put(Integer.valueOf(id),qty);

                    Log.e("sizeinadap", String.valueOf(local_hm.size())+" "+id+" :"+qty);

                    Prevalent.currentUser.setCart_hm(local_hm);
                    Paper.book().write(Prevalent.Usercart,local_hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.qty_btn.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Integer,String> local_hm=new HashMap<Integer,String>();
                Log.e("user",Prevalent.currentUser.getNumber());
                userCartref.setValue(null);

                local_hm=Prevalent.currentUser.getCart_hm();
                //update data in local_hm and set
                local_hm.put(product.getPid(),holder.qty_btn.getNumber());
                if(Integer.valueOf(holder.qty_btn.getNumber())==0){
                    Log.e("became","became zero");
                    local_hm.remove(product.getPid());

                }
                Prevalent.currentUser.setCart_hm(local_hm);
                Paper.book().write(Prevalent.Usercart,local_hm);
                 HashMap<String,Object> samplemap=new HashMap<>();
                for (int name : local_hm.keySet()) {
                    samplemap.put(String.valueOf(name),local_hm.get(name));
                }
                 userCartref.updateChildren(samplemap);
                Toast.makeText(mCtx, "endooo", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvName,tvPrice,tvDesc;
        ElegantNumberButton qty_btn;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            qty_btn = itemView.findViewById(R.id.qty_btn);
            imageView = itemView.findViewById(R.id.usertv_image);
            tvDesc = itemView.findViewById(R.id.usertv_desc);
            tvName = itemView.findViewById(R.id.usertv_name);
            tvPrice = itemView.findViewById(R.id.usertv_price);
        }
    }
}
