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
 import com.vam.groceryapp.CartPage;
 import com.vam.groceryapp.Prevalent.Prevalent;
 import com.vam.groceryapp.R;
 import java.util.HashMap;
 import java.util.List;
 import io.paperdb.Paper;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ProductViewHolder>{

    private Context mCtx;
    private List<UserProduct> productList;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference userCartref;

    public CartProductAdapter(Context mCtx, List<UserProduct> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.cart_product_card,null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        final UserProduct product = productList.get(position);
        holder.tvName.setText(product.getpName());
       holder.tvPrice.setText(product.getpPrice()+"/"+product.getqType());
        userCartref = mDatabase.child(Prevalent.currentUser.getNumber()).child("Cart");

        Picasso.get().load(product.getpImage()).into(holder.imageView);
        Log.e("i",product.getpImage());
       // holder.imageView.setImageDrawable(mCtx.getDrawable(R.drawable.icon));

//        if(Prevalent.currentUser.getCart_hm().containsKey(product.getPid()))
//            holder.qty_btn.setNumber(Prevalent.currentUser.getCart_hm().get(product.getPid()));

        userCartref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = String.valueOf(product.getPid());
                String qty="0";
                if(dataSnapshot.hasChild(id))
                    qty = dataSnapshot.child(id).getValue(String.class);
                Log.e("qty",qty);
                holder.qty_btn.setNumber(qty);
                int total_price;
                total_price =Integer.valueOf(qty) * Integer.valueOf(product.getpPrice());
                holder.tvTotal.setText(String.valueOf(total_price));
                String finalp= (String) CartPage.tv_total.getText();
                finalp =String.valueOf(Integer.valueOf(finalp)+total_price);
                Log.e("final",finalp);
                CartPage.tv_total.setText(finalp);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.qty_btn.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartPage.getCartpageInstance().displayToast();
            }
        });


        holder.qty_btn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                HashMap<Integer,String> local_hm=new HashMap<Integer,String>();
                Log.e("id", product.getPid()+":"+newValue);

                local_hm=Prevalent.currentUser.getCart_hm();

                //update data in local_hm and set
                local_hm.put(product.getPid(), String.valueOf(newValue));
                if(newValue ==0){
                    local_hm.remove(product.getPid());
                }
                Prevalent.currentUser.setCart_hm(local_hm);
                Paper.book().write(Prevalent.Usercart,local_hm);
                Toast.makeText(mCtx, "Cart Update"+" "+product.getPid()+": "+Prevalent.currentUser.getCart_hm().get(product.getPid()), Toast.LENGTH_SHORT).show();

                //for setting cart values in firebase
                userCartref.setValue(null);
                HashMap<String,Object> samplemap=new HashMap<>();
                for (int name : local_hm.keySet()) {
                    samplemap.put(String.valueOf(name),local_hm.get(name));
                }
                userCartref.updateChildren(samplemap);

                //for changing the total values
                int qty_change = newValue-oldValue;
                int new_total = Integer.valueOf((String) CartPage.tv_total.getText())+(qty_change*Integer.valueOf(product.pPrice));
                String new_price =String.valueOf( Integer.valueOf((String) holder.tvTotal.getText())+(qty_change*Integer.valueOf(product.pPrice)));
                CartPage.tv_total.setText(String.valueOf(new_total));
                holder.tvTotal.setText(new_price);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView tvName,tvPrice,tvTotal;
        ElegantNumberButton qty_btn;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            qty_btn = itemView.findViewById(R.id.qty_btn);
            imageView = itemView.findViewById(R.id.usertv_image);
            tvTotal = itemView.findViewById(R.id.product_total);
            tvName = itemView.findViewById(R.id.usertv_name);
            tvPrice = itemView.findViewById(R.id.usertv_price);
        }
    }
}
