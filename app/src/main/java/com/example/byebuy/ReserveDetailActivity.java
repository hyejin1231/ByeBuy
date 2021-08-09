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

public class ReserveDetailActivity extends AppCompatActivity {

    ImageView iv_rv_profile;
    TextView tv_rv_name,tv_rv_seller,tv_rv_category,tv_rv_reserver,tv_rv_detail,tv_rv_place,tv_rv_alarm;
    Button btn_rv_check,btn_rv_find;
    ImageView img_btn_rv_back;
    String test,key;
    String seller, reserver;
    String keyUser;
    String placeStatus;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference_Product, databaseReference_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_detail);

        img_btn_rv_back = findViewById(R.id.img_btn_rv_back);
        iv_rv_profile = findViewById(R.id.iv_rv_profile);
        tv_rv_name = findViewById(R.id.tv_rv_name);
        tv_rv_seller = findViewById(R.id.tv_rv_seller);
        tv_rv_category = findViewById(R.id.tv_rv_category);
        tv_rv_reserver = findViewById(R.id.tv_rv_reserver);
        tv_rv_detail = findViewById(R.id.tv_rv_detail);
        btn_rv_check = findViewById(R.id.btn_rv_check);
        btn_rv_find = findViewById(R.id.btn_rv_find);
        tv_rv_place = findViewById(R.id.tv_rv_place);
        tv_rv_alarm = findViewById(R.id.tv_rv_alarm);

        database = FirebaseDatabase.getInstance(); //파이어벵스 데이터베이스 연동
        databaseReference_Product = database.getReference("Product");
        databaseReference_User= database.getReference("User");

        Intent intent = getIntent();
        final String place = intent.getExtras().getString("place");
        String image =intent.getExtras().getString("image");
        String price = intent.getExtras().getString("price");
        final String title = intent.getExtras().getString("title");
        final String unique =intent.getExtras().getString("unique");

        test = intent.getExtras().getString("unique");
        databaseReference_Product.orderByChild("unique").equalTo(test).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }

                tv_rv_category.setText(snapshot.child(key).child("category").getValue().toString());
                tv_rv_detail.setText(snapshot.child(key).child("detail").getValue().toString());
                tv_rv_place.setText(snapshot.child(key).child("place").getValue().toString());

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();
                String path = snapshot.child(key).child("image").getValue().toString();
                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Toast.makeText(context, "성공", Toast.LENGTH_SHORT).show();

                        Glide.with(getApplicationContext())
                                .load(uri)
                                .override(300,300)
                                .into(iv_rv_profile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });

                placeStatus = snapshot.child(key).child("placestatus").getValue().toString();

                if(placeStatus.equals("x")) {
                    tv_rv_alarm.setVisibility(View.VISIBLE);
                    btn_rv_find.setVisibility(View.INVISIBLE);
                    btn_rv_check.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tv_rv_name.setText(intent.getExtras().getString("title"));
        seller = intent.getExtras().getString("seller");
        reserver = intent.getExtras().getString("reserver");

        databaseReference_User.orderByChild("uid").equalTo(seller).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()){
                    keyUser = child.getKey();
                }
                tv_rv_seller.setText(snapshot.child(keyUser).child("id").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_User.orderByChild("uid").equalTo(reserver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()){
                    keyUser = child.getKey();
                }
                tv_rv_reserver.setText(snapshot.child(keyUser).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        btn_rv_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CheckBerryboxPhoto.class);
                intent.putExtra("place", place);
                intent.putExtra("title", title);
                intent.putExtra("seller", seller);
                intent.putExtra("reserver", reserver);
                intent.putExtra("unique", unique);
                v.getContext().startActivity(intent);
            }
        });

        btn_rv_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), berrybox.class);
                intent.putExtra("request", "상품 찾기");
                intent.putExtra("reserver", reserver);
                intent.putExtra("place", place);
                v.getContext().startActivity(intent);
            }
        });



        img_btn_rv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}