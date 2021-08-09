package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.util.Date;

public class Sub extends AppCompatActivity {

    ImageView img_sub_diss,img_sub_good,img_sub_soso,img_sub_angry,img_sub_smile;
    ImageView tv_image;
    TextView tv_title;
    TextView tv_price;
    TextView edit_detail;
    TextView tv_count;
    TextView tv_category;
    TextView tv_stationPlace;
    ImageView img_btnBackMain,img_btnLike;


    Button btn_price,btn_sub_infoGo;
    TextView tv_subDate, tv_sub_sellerInfo;;
    String key,key1,key2,unique;
    String Message;
    String destinationUID;
    String seller;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String currentUid = user.getUid();
    int like_check=1;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_like,databaseReference_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        tv_sub_sellerInfo = findViewById(R.id.tv_sub_sellerInfo);
        tv_category = findViewById(R.id.tv_category);
        tv_image = findViewById(R.id.tv_image);
        tv_title = findViewById(R.id.tv_title);
        tv_price = findViewById(R.id.tv_price);
        edit_detail = findViewById(R.id.edit_detail);
        tv_count = findViewById(R.id.tv_count);
        img_btnBackMain = findViewById(R.id.img_btnBackMain);
        btn_price = findViewById(R.id.btn_price);
        tv_subDate = findViewById(R.id.tv_subDate);
        img_btnLike = findViewById(R.id.img_btnLike);
        btn_sub_infoGo = findViewById(R.id.btn_sub_infoGo);
        tv_stationPlace = findViewById(R.id.tv_stationPlace);

        img_sub_smile = findViewById(R.id.img_sub_smile);
        img_sub_angry = findViewById(R.id.img_sub_angry);
        img_sub_diss = findViewById(R.id.img_sub_diss);
        img_sub_good = findViewById(R.id.img_sub_good);
        img_sub_soso = findViewById(R.id.img_sub_soso);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Product");
        databaseReference_User = database.getReference("User");
        databaseReference_like = database.getReference("Like");

        Intent intent = getIntent();
        unique = intent.getExtras().getString("unique");

        Glide.with(this).asBitmap()
                .load(intent.getExtras().getString("image"))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        tv_image.setImageBitmap(resource);
                    }
                });

        tv_count.setText(intent.getExtras().getString("count"));

        databaseReference.orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }
                tv_stationPlace.setText(snapshot.child(key).child("place").getValue().toString());
                tv_price.setText(snapshot.child(key).child("price").getValue().toString());
                tv_title.setText(snapshot.child(key).child("title").getValue().toString());
                edit_detail.setText(snapshot.child(key).child("detail").getValue().toString());
                tv_subDate.setText(snapshot.child(key).child("date").getValue().toString());
                tv_category.setText(snapshot.child(key).child("category").getValue().toString());
                seller = snapshot.child(key).child("seller").getValue().toString();


                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();

                String path = snapshot.child(key).child("image").getValue().toString();

                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(getApplicationContext())
                                .load(uri)
                                .into(tv_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "실패", Toast.LENGTH_SHORT).show();
                    }
                });

//                long now = System.currentTimeMillis();
//                Date today = new Date(now);
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                String date = simpleDateFormat.format(today);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_sub_sellerInfo.setText(snapshot.child(seller).child("id").getValue().toString());

                int estimate = Integer.parseInt(snapshot.child(seller).child("estimate").getValue().toString());

                if (estimate >= 0 && estimate <= 20) {
                    img_sub_angry.setVisibility(View.VISIBLE);

                    img_sub_smile.setVisibility(View.INVISIBLE);
                    img_sub_good.setVisibility(View.INVISIBLE);
                    img_sub_soso.setVisibility(View.INVISIBLE);
                    img_sub_diss.setVisibility(View.INVISIBLE);
                }else if(estimate > 20 && estimate <= 40) {
                    img_sub_diss.setVisibility(View.VISIBLE);

                    img_sub_angry.setVisibility(View.INVISIBLE);
                    img_sub_smile.setVisibility(View.INVISIBLE);
                    img_sub_good.setVisibility(View.INVISIBLE);
                    img_sub_soso.setVisibility(View.INVISIBLE);
                }else if(estimate > 40 && estimate <= 60){
                    img_sub_soso.setVisibility(View.VISIBLE);

                    img_sub_diss.setVisibility(View.INVISIBLE);
                    img_sub_angry.setVisibility(View.INVISIBLE);
                    img_sub_smile.setVisibility(View.INVISIBLE);
                    img_sub_good.setVisibility(View.INVISIBLE);
                }else if(estimate > 60 && estimate <= 80) {
                    img_sub_smile.setVisibility(View.VISIBLE);

                    img_sub_diss.setVisibility(View.INVISIBLE);
                    img_sub_angry.setVisibility(View.INVISIBLE);
                    img_sub_good.setVisibility(View.INVISIBLE);
                    img_sub_soso.setVisibility(View.INVISIBLE);
                }else {
                    img_sub_good.setVisibility(View.VISIBLE);

                    img_sub_smile.setVisibility(View.INVISIBLE);
                    img_sub_soso.setVisibility(View.INVISIBLE);
                    img_sub_diss.setVisibility(View.INVISIBLE);
                    img_sub_angry.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_sub_infoGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SubSellerInfo.class);
                intent.putExtra("seller", seller);
                startActivity(intent);
            }
        });

        img_btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Sub.this, MainActivity.class);
                startActivity(intent1);
                finish();


                // moveTaskToBack(false);
            }
        });

        databaseReference_like.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();

                    if (snapshot.child(key).child("id").getValue().equals(currentUid)
                            && snapshot.child(key).child("unique").getValue().equals(unique)) {
                        like_check *=0;
                    }
                    //안존재하면...
                    else {
                        like_check *=1;
                    }
                }

                //이미 관심리스트에 존재하면...
                if (like_check==0) {
                    img_btnLike.setImageResource(R.drawable.fullheart);
                }
                //안존재하면...
                else if(like_check==1) {
                    img_btnLike.setImageResource(R.drawable.emptyheart);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "관심상품 등록실패", Toast.LENGTH_SHORT).show();
            }
        });


        //클릭 => 관심상품 등록
        img_btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            key1 = child.getKey();

                            if (like_check==0) {
                                Toast.makeText(getApplicationContext(), "이미 관심상품으로 등록되어있습니다.", Toast.LENGTH_SHORT).show();
                            }
                            else if(like_check==1) {
                                Like like_add = new Like(currentUid, unique);
                                databaseReference_like.push().setValue(like_add);
                                Toast.makeText(getApplicationContext(), "관심상품 등록", Toast.LENGTH_SHORT).show();
                                img_btnLike.setImageResource(R.drawable.fullheart);
                                like_check=0;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "관심상품 등록실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        img_btnLike.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                databaseReference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            key2 = child.getKey();

                            if (((snapshot.child(key2).child("unique").getValue()).equals(unique)) &&
                                    ((snapshot.child(key2).child("id").getValue()).equals(currentUid))) {
                                snapshot.getRef().child(key2).removeValue();
                                Toast.makeText(getApplicationContext(), "관심상품 취소", Toast.LENGTH_SHORT).show();
                                img_btnLike.setImageResource(R.drawable.emptyheart);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });
                return false;
            }
        });



        //권이 추 가 부분 상세구매에서 채팅 연동 완료. uid 정상적 출력, for문 잘못 달아서 여태 오류 뜬 것 으로 추정
        btn_price.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (seller.equals(currentUid)) {
                    Toast.makeText(getApplicationContext(), "본인 물품 구매 불가 ", Toast.LENGTH_SHORT).show(); // 실행할 코드
                }else {
                    databaseReference.orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Message = child.getKey();
                                destinationUID = snapshot.child(Message).child("seller").getValue().toString();
                            }
                            Toast.makeText(getApplicationContext(), destinationUID, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                            intent.putExtra("destinationUID", destinationUID);
                            startActivity(intent);
                            finish();
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