package com.example.byebuy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LikeListAdapter  extends RecyclerView.Adapter <LikeListAdapter.CustomViewHolder3>{

    private ArrayList<Product> arrayList;
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    int viewCount;
    String abcd,abcde;
    String key,key1,key2,test;
    String currentSeller;
    String destinationUID;
    String uniqueTest;
    String btnLike;

    public LikeListAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like_list,parent,false);
        LikeListAdapter.CustomViewHolder3 holder3 = new LikeListAdapter.CustomViewHolder3(view);
        return holder3;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder3 holder, final int position) {

        holder.tv_productTitle.setText(arrayList.get(position).getTitle());
        holder.tv_productPrice.setText(arrayList.get(position).getPrice() + "원");

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Product"); // DB 테이블 연동


        test = arrayList.get(position).getUnique();
        final FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
        final StorageReference storageReference = storage.getReference();



        databaseReference.orderByChild("unique").equalTo(test).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    uniqueTest = child.getKey();

                    long now = System.currentTimeMillis();
                    Date today = new Date(now);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String date = simpleDateFormat.format(today);
                    String deadline = (String) snapshot.child(uniqueTest).child("deadline").getValue();

                    String image = arrayList.get(position).getImage();


                    //String path = (String)snapshot.child(uniqueTest).child("image").getValue();
                    String path = (String) snapshot.child(uniqueTest).child("image").getValue();

                    holder.tv_viewCnt.setText(snapshot.child(uniqueTest).child("count").getValue().toString());

                    storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Glide.with(holder.itemView)
                                    .load(uri)
                                    .into(holder.iv_productImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if(snapshot.child(uniqueTest).child("status").getValue().toString().equals("selling")) {
                        holder.tv_alaram.setText("판매중");
                    }else if(snapshot.child(uniqueTest).child("status").getValue().toString().equals("complete")) {
                        holder.tv_alaram.setText("판매 종료");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return(arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder3 extends RecyclerView.ViewHolder {

        ImageView iv_productImage;
        TextView tv_productTitle;
        TextView tv_productPrice;
        TextView tv_viewCnt;
        Button btn_Buynow;
        TextView tv_alaram;

        private FirebaseDatabase database;
        private DatabaseReference databaseReference,databaseReference_like;


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        public CustomViewHolder3(@NonNull final View itemView) {
            super(itemView);
            this.tv_productTitle = itemView.findViewById(R.id.tv_productTitle);
            this.tv_productPrice = itemView.findViewById(R.id.tv_productPrice);
            this.iv_productImage = itemView.findViewById(R.id.iv_productImage);
            this.tv_viewCnt = itemView.findViewById(R.id.tv_viewCnt);
            this.btn_Buynow = itemView.findViewById(R.id.btn_Buynow);
            this.tv_alaram = itemView.findViewById(R.id.tv_alaram);


            database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
            databaseReference = database.getReference("Product"); // DB 테이블 연동
            databaseReference_like = database.getReference("Like");


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int position = getAdapterPosition();
                    abcd = arrayList.get(position).getUnique();
                    viewCount = arrayList.get(position).getCount() + 1;


                    databaseReference.orderByChild("unique").equalTo(abcd).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot child : snapshot.getChildren()) {
                                key = child.getKey();
                            }

                            snapshot.getRef().child(key).child("count").setValue(viewCount);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Intent intent = new Intent(v.getContext(), LikeDetailActivity.class);
                    intent.putExtra("unique", arrayList.get(position).getUnique());
                    intent.putExtra("count", String.valueOf(viewCount));
                    intent.putExtra("image", arrayList.get(position).getImage());
                    intent.putExtra("title", arrayList.get(position).getTitle());
                    intent.putExtra("price", arrayList.get(position).getPrice());
                    intent.putExtra("detail", arrayList.get(position).getDetail());
                    intent.putExtra("btnLike",btnLike);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    itemView.getContext().startActivity(intent);


                }
            });


            btn_Buynow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    int position = getAdapterPosition();
                    String Seller = arrayList.get(position).getSeller();

                    if (arrayList.get(getAdapterPosition()).getSeller().equals(currentUid)) {
                        Toast.makeText(itemView.getContext(), "본인 물품 구매 불가", Toast.LENGTH_SHORT).show(); // 실행할 코드
                    }else {

                        databaseReference.orderByChild("seller").equalTo(Seller).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    key2 = child.getKey();
                                    destinationUID = snapshot.child(key2).child("seller").getValue().toString();
//                                Toast.makeText(view.getContext(),destinationUID,Toast.LENGTH_SHORT).show();
                                }
                                assert key2 != null;
                                Intent intent = new Intent(view.getContext(), MessageActivity.class);
                                intent.putExtra("destinationUID", destinationUID);
                                view.getContext().startActivity(intent);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            });




        }
    }

}
