package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

public class SubSellerInfo extends AppCompatActivity {

    TextView tv_info_sellerId,tv_info_estimate;
    ImageView img_info_smile,img_info_good,img_info_angry,img_info_diss,img_info_soso,img_btn_infoBack;
    String seller,unique,key;
    ImageView iv_info_profile;


    private RecyclerView SubSellerInfoCycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_product;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_seller_info);

        tv_info_estimate = findViewById(R.id.tv_info_estimate);
        tv_info_sellerId = findViewById(R.id.tv_info_sellerId);
        img_btn_infoBack = findViewById(R.id.img_btn_infoBack);
        iv_info_profile = findViewById(R.id.iv_info_profile);

        img_info_angry = findViewById(R.id.img_info_angry);
        img_info_diss = findViewById(R.id.img_info_diss);
        img_info_good = findViewById(R.id.img_info_good);
        img_info_smile = findViewById(R.id.img_info_smile);
        img_info_soso = findViewById(R.id.img_info_soso);


        SubSellerInfoCycler = findViewById(R.id.SubSellerInfoCycler);
        SubSellerInfoCycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        SubSellerInfoCycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("User");
        databaseReference_product = database.getReference("Product");


        Intent intent = getIntent();
        seller = intent.getStringExtra("seller");
        unique = intent.getExtras().getString("unique");

        //프사 띄워주는 코드
        databaseReference.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }

                //이미지뷰에 선택된 이미지 로딩시키는 코드임
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();

                //이미지지파일이름 가져오는거...
                String path = (String) snapshot.child(key).child("photoUrl").getValue();


                if (path.equals("default")) {
                    Glide.with(SubSellerInfo.this)
                            .load(R.drawable.logo_main)
                            .into(iv_info_profile);

                }
                else {
                    storageReference.child("myprofile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(SubSellerInfo.this)
                                    .load(uri)
                                    .into(iv_info_profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SubSellerInfo.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_info_estimate.setText(snapshot.child(seller).child("estimate").getValue().toString());
                tv_info_sellerId.setText(snapshot.child(seller).child("id").getValue().toString());

                int estimate = Integer.parseInt(snapshot.child(seller).child("estimate").getValue().toString());

                if (estimate >= 0 && estimate <= 20) {
                    img_info_angry.setVisibility(View.VISIBLE);

                    img_info_smile.setVisibility(View.INVISIBLE);
                    img_info_good.setVisibility(View.INVISIBLE);
                    img_info_soso.setVisibility(View.INVISIBLE);
                    img_info_diss.setVisibility(View.INVISIBLE);
                }else if(estimate > 20 && estimate <= 40) {
                    img_info_diss.setVisibility(View.VISIBLE);

                    img_info_angry.setVisibility(View.INVISIBLE);
                    img_info_smile.setVisibility(View.INVISIBLE);
                    img_info_good.setVisibility(View.INVISIBLE);
                    img_info_soso.setVisibility(View.INVISIBLE);
                }else if(estimate > 40 && estimate <= 60){
                    img_info_soso.setVisibility(View.VISIBLE);

                    img_info_diss.setVisibility(View.INVISIBLE);
                    img_info_angry.setVisibility(View.INVISIBLE);
                    img_info_smile.setVisibility(View.INVISIBLE);
                    img_info_good.setVisibility(View.INVISIBLE);
                }else if(estimate > 60 && estimate <= 80) {
                    img_info_smile.setVisibility(View.VISIBLE);

                    img_info_diss.setVisibility(View.INVISIBLE);
                    img_info_angry.setVisibility(View.INVISIBLE);
                    img_info_good.setVisibility(View.INVISIBLE);
                    img_info_soso.setVisibility(View.INVISIBLE);
                }else {
                    img_info_good.setVisibility(View.VISIBLE);

                    img_info_smile.setVisibility(View.INVISIBLE);
                    img_info_soso.setVisibility(View.INVISIBLE);
                    img_info_diss.setVisibility(View.INVISIBLE);
                    img_info_angry.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_product.orderByChild("seller").equalTo(seller).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Product pd = child.getValue(Product.class);
                    arrayList.add(pd);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", String.valueOf(error.toException())); // 에러문 출력
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        SubSellerInfoCycler.setAdapter(adapter);


        img_btn_infoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}