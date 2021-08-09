package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SellListReserveDetailActivity extends AppCompatActivity {

    ImageView iv_sellreserveDetail_profile,img_btn_sellreserveDetailBack;
    TextView tv_sellreserveDetail_name,tv_sellreserveDetail_seller,tv_sellreserveDetail_reserver,tv_sellreserveDetail_price
            ,tv_sellreserveDetail_place;
    Button btn_sellreserveDetaildel_put;

    String key;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference_User, databaseReference_Berrybox, databaseReference_Product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_list_reserve_detail);

        iv_sellreserveDetail_profile = findViewById(R.id.iv_sellreserveDetail_profile);
        img_btn_sellreserveDetailBack = findViewById(R.id.img_btn_sellreserveDetailBack);
        tv_sellreserveDetail_name = findViewById(R.id.tv_sellreserveDetail_name);
        tv_sellreserveDetail_seller = findViewById(R.id.tv_sellreserveDetail_seller);
        tv_sellreserveDetail_reserver = findViewById(R.id.tv_sellreserveDetail_reserver);
        tv_sellreserveDetail_price = findViewById(R.id.tv_sellreserveDetail_price);
        tv_sellreserveDetail_place = findViewById(R.id.tv_sellreserveDetail_place);
        btn_sellreserveDetaildel_put = findViewById(R.id.btn_sellreserveDetaildel_put);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference_User = database.getReference("User");
        databaseReference_Berrybox = database.getReference("BerryBox");
        databaseReference_Product = database.getReference("Product");

        Intent intent = getIntent();
        String place = intent.getExtras().getString("place");
        String image =intent.getExtras().getString("image");
        String price = intent.getExtras().getString("price");
        final String seller =intent.getExtras().getString("seller");
        final String reserver =intent.getExtras().getString("reserver");
        String title = intent.getExtras().getString("title");
        String unique =intent.getExtras().getString("unique");

        tv_sellreserveDetail_place.setText(place);
        tv_sellreserveDetail_price.setText(price);
        tv_sellreserveDetail_name.setText(title);

        databaseReference_Product.orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
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

                        Glide.with(getApplicationContext())
                                .load(uri)
                                .override(300,300)
                                .into(iv_sellreserveDetail_profile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // 판매자 uid -> id로 바꾸기
        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_sellreserveDetail_seller.setText(snapshot.child(seller).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 예약자 uid -> id로 바꾸기
        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_sellreserveDetail_reserver.setText(snapshot.child(reserver).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_sellreserveDetaildel_put.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), berrybox.class);
                intent.putExtra("request", "상품 넣기");
                intent.putExtra("seller", seller);
                intent.putExtra("reserver", reserver);
               v.getContext().startActivity(intent);
            }
        });


        img_btn_sellreserveDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}