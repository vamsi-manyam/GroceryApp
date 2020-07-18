package com.vam.groceryapp.Model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vam.groceryapp.R;

import java.util.List;

public class OwnerProductAdapter extends RecyclerView.Adapter<OwnerProductAdapter.OwnerProductViewHolder> {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference myShopCategory ;


    Context mCtx;
    List<OwnerProduct> ownerProductList;
    String category,shopid;
    int count;

    public OwnerProductAdapter(Context mCtx, List<OwnerProduct> ownerProductList,String shopid,String category,int count) {
        this.mCtx = mCtx;
        this.ownerProductList = ownerProductList;
        this.category=category;
        this.shopid=shopid;
        this.count=count;
        myShopCategory = myRef.child("Shops").child(shopid).child("Products").child(category);
    }



    @NonNull
    @Override
    public OwnerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.add_each_product,null);
        return new OwnerProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OwnerProductViewHolder holder, int position) {
        final OwnerProduct product = ownerProductList.get(position);

        holder.Name.setText(product.getPName());
        String pricetext = "Rs "+product.getPrice()+" / "+product.getQType();
        holder.Price.setText(pricetext);
      //  holder.imgIcon.setImageDrawable(mCtx.getResources().getDrawable(product.getImg(),null));

        Picasso.get().load(product.getImg()).into(holder.imgIcon);
        holder.checkBox.setChecked(product.isContains());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "Clicked"+" "+product.getPid(), Toast.LENGTH_SHORT).show();
                Toast.makeText(mCtx, ""+count, Toast.LENGTH_SHORT).show();
                myShopCategory.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        count=(int)dataSnapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(holder.checkBox.isChecked())
                {
                    myShopCategory.child(String.valueOf(count+1)).setValue(String.valueOf(product.getPid()));
                }
                else{
                    final String[] x = new String[1];
                    myShopCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(int i=0;i<count;i++){
                                String a = dataSnapshot.child(String.valueOf(i+1)).getValue(String.class);
                                String b = (String.valueOf(product.getPid()));
                                if(a.equals(b))
                                {   Log.e("strings",dataSnapshot.child(String.valueOf(i+1)).getValue(String.class)+" "+String.valueOf(product.getPid()));
                                    x[0] =String.valueOf(i+1);
                                    Log.e("did",x[0]);
                                    String last_val= dataSnapshot.child(String.valueOf(count)).getValue(String.class);
                                    myShopCategory.child(x[0]).setValue(last_val);
                                    myShopCategory.child(String.valueOf(count)).removeValue();
                                    break;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Log.e("k","delete from category");
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return ownerProductList.size();

    }

    class OwnerProductViewHolder extends RecyclerView.ViewHolder{

        ImageView imgIcon;
        TextView Name,Price;
        CheckBox checkBox;

        public OwnerProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIcon = itemView.findViewById(R.id.imageIcon);
            Name = itemView.findViewById(R.id.ProductNameOwner);
            Price = itemView.findViewById(R.id.ProductPriceOwner);
            checkBox = itemView.findViewById(R.id.checkBoxOwner);
        }
    }
}
