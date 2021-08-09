package com.example.byebuy;

import android.content.Context;
import android.content.Intent;
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

public class ReserveListAdapter extends RecyclerView.Adapter<ReserveListAdapter.CustomViewHolder> {

    private ArrayList<Product> arrayList;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference_Product,databaseReference_User;

    String test,key;
    String placeStatus;

    public ReserveListAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReserveListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserve_list,parent,false);
       ReserveListAdapter.CustomViewHolder holder = new ReserveListAdapter.CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReserveListAdapter.CustomViewHolder holder, int position) {

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference_Product = database.getReference("Product"); // DB 테이블 연동

        test = arrayList.get(position).getUnique();
//        placeStatus = arrayList.get(position).getPlacestatus();

        databaseReference_Product.orderByChild("unique").equalTo(test).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                .into(holder.iv_reserve_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                    }
                });

                placeStatus = snapshot.child(key).child("placestatus").getValue().toString();
//                Toast.makeText(context, placeStatus, Toast.LENGTH_SHORT).show();

                if(placeStatus.equals("x")) {
                    holder.btn_findProduct.setVisibility(View.INVISIBLE);
                    holder.btn_checkProduct.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database = FirebaseDatabase.getInstance();
        databaseReference_User = database.getReference("User"); // DB 테이블 연동

        holder.tv_reserve_title.setText(arrayList.get(position).getTitle());
        holder.tv_reserve_price.setText(arrayList.get(position).getPrice());
        holder.tv_reserve_place.setText(arrayList.get(position).getPlace());
        final String getsellerget = arrayList.get(position).getSeller();
        final String getreserverget = arrayList.get(position).getReserver();


        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tv_reserve_seller.setText(snapshot.child(getsellerget).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tv_reserve_reserver.setText(snapshot.child(getreserverget).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_reserve_title,tv_reserve_price,tv_reserve_reserver,tv_reserve_seller,tv_reserve_place;
        ImageView iv_reserve_image;
        Button btn_checkProduct,btn_findProduct;

        public CustomViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.tv_reserve_place = itemView.findViewById(R.id.tv_reserve_place);
            this.tv_reserve_title = itemView.findViewById(R.id.tv_reserve_title);
            this.tv_reserve_price = itemView.findViewById(R.id.tv_reserve_price);
            this.tv_reserve_reserver = itemView.findViewById(R.id.tv_reserve_reserver);
            this.tv_reserve_seller = itemView.findViewById(R.id.tv_reserve_seller);
            this.iv_reserve_image = itemView.findViewById(R.id.iv_reserve_image);
            this.btn_checkProduct = itemView.findViewById(R.id.btn_checkProduct);
            this.btn_findProduct = itemView.findViewById(R.id.btn_findProduct);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Intent intent = new Intent(v.getContext(), ReserveDetailActivity.class);

                    intent.putExtra("image",arrayList.get(position).getImage());
                    intent.putExtra("price", String.valueOf(arrayList.get(position).getPrice()));
                    intent.putExtra("seller", arrayList.get(position).getSeller());
                    intent.putExtra("reserver", arrayList.get(position).getReserver());
                    intent.putExtra("title", arrayList.get(position).getTitle());
                    intent.putExtra("unique", arrayList.get(position).getUnique());

                    v.getContext().startActivity(intent);
                }
            });

            // 보관함에 담긴 상품 이미지 확인
            btn_checkProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), CheckBerryboxPhoto.class);
                    intent.putExtra("place", arrayList.get(position).getPlace());
                    intent.putExtra("title", arrayList.get(position).getTitle());
                    intent.putExtra("seller", arrayList.get(position).getSeller());
                    intent.putExtra("reserver", arrayList.get(position).getReserver());
                    intent.putExtra("unique", arrayList.get(position).getUnique());
                    v.getContext().startActivity(intent);
                }
            });

            // 상품 보관함에서 문 열기
            btn_findProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), berrybox.class);
                    intent.putExtra("request", "상품 찾기");
                    intent.putExtra("reserver", arrayList.get(position).getReserver());
                    intent.putExtra("place", arrayList.get(position).getPlace());
                    v.getContext().startActivity(intent);
                }
            });
        }
    } // end




}



















