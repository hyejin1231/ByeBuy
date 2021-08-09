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

public class CheckBerryboxPhoto extends AppCompatActivity {

    ImageView iv_chphoto_profile,img_btn_chphoto_back;
    TextView tv_chphoto_name,tv_chphoto_place,tv_chphoto_seller,tv_chphoto_reserver;
    Button btn_chphoto_find;

    private FirebaseDatabase database;
    private DatabaseReference  databaseReference_User, databaseReference_Berrybox;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_berrybox_photo);

        iv_chphoto_profile = findViewById(R.id.iv_chphoto_profile);
        img_btn_chphoto_back = findViewById(R.id.img_btn_chphoto_back);

        tv_chphoto_name = findViewById(R.id.tv_chphoto_name);
        tv_chphoto_place = findViewById(R.id.tv_chphoto_place);
        tv_chphoto_seller = findViewById(R.id.tv_chphoto_seller);
        tv_chphoto_reserver = findViewById(R.id.tv_chphoto_reserver);
        btn_chphoto_find = findViewById(R.id.btn_chphoto_find);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference_User = database.getReference("User");
        databaseReference_Berrybox = database.getReference("BerryBox");

        Intent intent = getIntent();

        final String place = intent.getExtras().getString("place");
        String title = intent.getExtras().getString("title");
        final String seller = intent.getExtras().getString("seller");
        final String reserver = intent.getExtras().getString("reserver");
        String unique = intent.getExtras().getString("unique");

      tv_chphoto_name.setText(title);
      tv_chphoto_place.setText(place);

      // 판매자 uid -> id로 바꾸기
      databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              tv_chphoto_seller.setText(snapshot.child(seller).child("id").getValue().toString());
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });

        // 예약자 uid -> id로 바꾸기
        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_chphoto_reserver.setText(snapshot.child(reserver).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_Berrybox.orderByChild("box_id").equalTo(place).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }

                //이미지뷰에 선택된 이미지 로딩시키는 코드임
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();

                // 이미지 파일 이름 가져오기
                String path = (String) snapshot.child(key).child("box_photourl").getValue();

                if (path.equals("")) {
                    Toast.makeText(CheckBerryboxPhoto.this, "판매자가 아직 물건을 넣지 않았습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    storageReference.child("img").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(CheckBerryboxPhoto.this)
                                    .load(uri)
                                    .into(iv_chphoto_profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CheckBerryboxPhoto.this, "실패", Toast.LENGTH_SHORT).show();
                            Glide.with(CheckBerryboxPhoto.this)
                                    .load(e)
                                    .into(iv_chphoto_profile);
                        }
                    });
                }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_chphoto_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), berrybox.class);
                intent.putExtra("request", "상품 찾기");
                intent.putExtra("reserver", reserver);
                intent.putExtra("place", place);
                v.getContext().startActivity(intent);
            }
        });


        // 뒤로가기 버튼
        img_btn_chphoto_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}