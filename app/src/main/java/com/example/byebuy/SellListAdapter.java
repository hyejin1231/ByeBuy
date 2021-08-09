package com.example.byebuy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class SellListAdapter  extends RecyclerView.Adapter<SellListAdapter.CustomViewHolder>{

    private ArrayList<Product> arrayList;
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference, databaseReference_User;

    String test;
    String key;
    String key2;

    public SellListAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sell_list, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getImage())
//                .into(holder.iv_sell_image);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Product"); // DB 테이블 연동
        databaseReference_User = database.getReference("User");

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
                                .into(holder.iv_sell_image);
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
        holder.tv_sell_title.setText("제품명 " + arrayList.get(position).getTitle());
        holder.tv_sell_price.setText("가격 " + String.valueOf(arrayList.get(position).getPrice()) + "원");

        holder.tv_sell_place.setText("보관함위치 "+ arrayList.get(position).getPlace());

        final String seller = arrayList.get(position).getSeller();
        final String buyer = arrayList.get(position).getBuyer();
        final String reserver = arrayList.get(position).getReserver();
        String status = arrayList.get(position).getStatus();

        if (status.equals("selling")){
            holder.tv_sell_status.setText("판매 중!!");
            holder.tv_sell_buyer.setText("구매자 ");
            holder.tv_sell_status.setTextColor(Color.parseColor("#EC1313"));
        }else if(status.equals("complete")) {
            holder.tv_sell_status.setText("판매 종료!!");

            holder.tv_sell_placeSatus.setText("구매자 물건 가져감");
            holder.tv_sell_placeSatus.setVisibility(View.VISIBLE);
            holder.btn_sell_putProduct.setVisibility(View.INVISIBLE);

            databaseReference_User.orderByChild("uid").equalTo(buyer).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot child: snapshot.getChildren()){
                        key2 = child.getKey();
                    }
                    holder.tv_sell_buyer.setText("구매자 " + snapshot.child(key2).child("id").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            holder.tv_sell_status.setTextColor(Color.parseColor("#1838EC"));

        }else if(status.equals("reservation")) {
            holder.tv_sell_status.setText("예약 중!!");

            if (arrayList.get(position).getPlacestatus().equals("판매자 물건 넣음")){
                holder.tv_sell_placeSatus.setText(arrayList.get(position).getPlacestatus());
                holder.tv_sell_placeSatus.setVisibility(View.VISIBLE);
                holder.btn_sell_putProduct.setVisibility(View.INVISIBLE);
            }else if (arrayList.get(position).getPlacestatus().equals("x")) {
                holder.tv_sell_placeSatus.setText("아직 물건 안넣음");
                holder.tv_sell_placeSatus.setVisibility(View.VISIBLE);
            }

            databaseReference_User.orderByChild("uid").equalTo(reserver).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child: snapshot.getChildren()){
                        key2 = child.getKey();
                    }
                    holder.tv_sell_buyer.setText("예약자 " + snapshot.child(key2).child("id").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.tv_sell_status.setTextColor(Color.parseColor("#EC1313"));
        }




        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tv_sell_seller.setText("판매자 " + snapshot.child(seller).child("id").getValue().toString());
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
        ImageView iv_sell_image;
        TextView tv_sell_title;
        TextView tv_sell_price;
        TextView tv_sell_seller;
        TextView tv_sell_buyer;
        TextView tv_sell_place;
        TextView tv_sell_status;
        Button btn_sell_putProduct;
        TextView tv_sell_placeSatus;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_sell_image = itemView.findViewById(R.id.iv_sell_image);
            this.tv_sell_title = itemView.findViewById(R.id.tv_sell_title);
            this.tv_sell_price = itemView.findViewById(R.id.tv_sell_price);
            this.tv_sell_seller = itemView.findViewById(R.id.tv_sell_seller);
            this.tv_sell_buyer = itemView.findViewById(R.id.tv_sell_buyer);
            this.tv_sell_status = itemView.findViewById(R.id.tv_sell_status);
            this.tv_sell_place= itemView.findViewById(R.id.tv_sell_place);
            this.tv_sell_placeSatus = itemView.findViewById(R.id.tv_sell_placeSatus);
            this.btn_sell_putProduct = itemView.findViewById(R.id.btn_sell_putProduct);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    //상품이 판매중인 경우
                    if((arrayList.get(position).getStatus()).equals("selling")){
                        Intent intent_selling = new Intent(view.getContext(),SellingDetailActivity.class);

                        intent_selling.putExtra("iv_sd_profile",arrayList.get(position).getImage());//
                        intent_selling.putExtra("tv_sd_price", String.valueOf(arrayList.get(position).getPrice()));
                        intent_selling.putExtra("tv_sd_seller", arrayList.get(position).getSeller());
                        intent_selling.putExtra("tv_sd_buyer", arrayList.get(position).getBuyer());
                        intent_selling.putExtra("tv_sd_name", arrayList.get(position).getTitle());
                        intent_selling.putExtra("unique", arrayList.get(position).getUnique());

                        view.getContext().startActivity(intent_selling);
                    }
                    else if((arrayList.get(position).getStatus()).equals("complete")){ //상품이 판매완료인 경우
                        Intent intent_complete = new Intent(view.getContext(), CompleteDetailActivity.class);

                        intent_complete.putExtra("iv_sd_profile",arrayList.get(position).getImage());
                        intent_complete.putExtra("tv_sd_price", String.valueOf(arrayList.get(position).getPrice()));
                        intent_complete.putExtra("tv_sd_seller", arrayList.get(position).getSeller());
                        intent_complete.putExtra("tv_sd_buyer", arrayList.get(position).getBuyer());
                        intent_complete.putExtra("tv_sd_name", arrayList.get(position).getTitle());
                        intent_complete.putExtra("unique",arrayList.get(position).getUnique());

                        view.getContext().startActivity(intent_complete);
                    }else if((arrayList.get(position).getStatus()).equals("reservation")){ // 상품이 예약중인 경우
                        Intent intent = new Intent(view.getContext(), SellListReserveDetailActivity.class);
                        intent.putExtra("place", arrayList.get(position).getPlace());
                        intent.putExtra("image",arrayList.get(position).getImage());
                        intent.putExtra("price", String.valueOf(arrayList.get(position).getPrice()));
                        intent.putExtra("seller", arrayList.get(position).getSeller());
                        intent.putExtra("reserver", arrayList.get(position).getReserver());
                        intent.putExtra("title", arrayList.get(position).getTitle());
                        intent.putExtra("unique",arrayList.get(position).getUnique());
                        view.getContext().startActivity(intent);
                    }

                }
            });

            btn_sell_putProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), berrybox.class);
                    intent.putExtra("request", "상품 넣기");
                    intent.putExtra("seller", arrayList.get(position).getSeller());
                    intent.putExtra("reserver", arrayList.get(position).getReserver());
                    v.getContext().startActivity(intent);
                }
            });


        }
    }
}
