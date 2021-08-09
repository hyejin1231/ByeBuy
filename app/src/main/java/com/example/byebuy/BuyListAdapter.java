package com.example.byebuy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BuyListAdapter extends RecyclerView.Adapter<BuyListAdapter.CustomViewHolder> {

    private ArrayList<Product> arrayList;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_u;

    String test;
    String key;
    String bringselleremail,bringbuyeremail;
//    public interface RecyclerViewClickListener {
//        void onClick(View v, int position);
//    }

    public BuyListAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BuyListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_list, parent, false);
        BuyListAdapter.CustomViewHolder holder = new BuyListAdapter.CustomViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final BuyListAdapter.CustomViewHolder holder, int position) {
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getImage())
//                .into(holder.iv_buy_image);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Product"); // DB 테이블 연동

        test = arrayList.get(position).getUnique();
        databaseReference.orderByChild("unique").equalTo(test).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();
                String path = snapshot.child(key).child("image").getValue().toString();
                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Toast.makeText(context, "성공", Toast.LENGTH_SHORT).show();

                        Glide.with(holder.itemView)
                                .load(uri)
                                .into(holder.iv_buy_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database = FirebaseDatabase.getInstance();
        databaseReference_u = database.getReference("User"); // DB 테이블 연동
        holder.tv_buy_title.setText(arrayList.get(position).getTitle());
        holder.tv_buy_price.setText(String.valueOf(arrayList.get(position).getPrice()));
        final String getsellerget = arrayList.get(position).getSeller();
        final String getbuyerget = arrayList.get(position).getBuyer();

//        //다혜수정...

        databaseReference_u.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tv_buy_seller.setText(snapshot.child(getsellerget).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_u.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tv_buy_buyer.setText(snapshot.child(getbuyerget).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public int getItemCount() {
        //삼항 연사자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //implements View.OnClickListener
        ImageView iv_buy_image;
        TextView tv_buy_title;
        TextView tv_buy_price;
        TextView tv_buy_seller;
        TextView tv_buy_buyer;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_buy_image = itemView.findViewById(R.id.iv_buy_image);
            this.tv_buy_title = itemView.findViewById(R.id.tv_buy_title);
            this.tv_buy_price = itemView.findViewById(R.id.tv_buy_price);
            this.tv_buy_seller = itemView.findViewById(R.id.tv_buy_seller);
            this.tv_buy_buyer = itemView.findViewById(R.id.tv_buy_buyer);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    Intent intent_buy = new Intent(view.getContext(), BuyDetailActivity.class);

                    intent_buy.putExtra("iv_bd_profile",arrayList.get(position).getImage());//
                    intent_buy.putExtra("tv_bd_price", String.valueOf(arrayList.get(position).getPrice()));
                    intent_buy.putExtra("tv_bd_seller", arrayList.get(position).getSeller());
                    intent_buy.putExtra("tv_bd_buyer", arrayList.get(position).getBuyer());
                    intent_buy.putExtra("tv_bd_name", arrayList.get(position).getTitle());
                    intent_buy.putExtra("unique", arrayList.get(position).getUnique());

                    view.getContext().startActivity(intent_buy);
                }
            });


        }
    }
}
