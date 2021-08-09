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
import com.google.android.gms.common.api.GoogleApiClient;
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

public class Mypage extends AppCompatActivity {

    private FirebaseAuth auth; //파이어 베이스 인증 객체
    private GoogleApiClient mGoogleApiClient;

    private TextView tv_result; // 닉네임 text
    private ImageView iv_profile; // 이미지 뷰
    private TextView tv_id;
    private Button btn_customerChat;
    private ImageView img_MyWarn;
    private TextView tv_MyWarn;
    private TextView tv_Message;
    private TextView tv_point;
    private ImageView img_btnReserve;

    private TextView tv_participate;
    private  TextView tv_UserEstimateCount;
    private  ImageView img_UserFace_smile,img_UserFace_dis,img_UserFace_angry,img_UserFace_good,img_UserFace_soso;

    private ImageView btn_buylist;
    private ImageView btn_selllist;
    private ImageView btn_likelist;
    private Button btn_logout;
    private Button btn_modify;
    private Button btn_recharge;


    ImageView img_btnMyBack;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<User> arrayList;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("User"); // DB 테이블 연동

        tv_result = findViewById(R.id.tv_result);
        iv_profile = findViewById(R.id.iv_profile);
        tv_id = findViewById(R.id.tv_email_login);

        img_MyWarn = findViewById(R.id.img_MyWarn);
        tv_MyWarn = findViewById(R.id.tv_MyWarn);
        tv_Message = findViewById(R.id.tv_Message);
        tv_point = findViewById(R.id.tv_point);

        img_btnReserve = findViewById(R.id.img_btnReserve);
        tv_participate = findViewById(R.id.tv_participate);
        tv_UserEstimateCount = findViewById(R.id.tv_UserEstimateCount);
        img_UserFace_smile = findViewById(R.id.img_UserFace_smile);
        img_UserFace_dis = findViewById(R.id.img_UserFace_dis);
        img_UserFace_angry = findViewById(R.id.img_UserFace_angry);
        img_UserFace_good = findViewById(R.id.img_UserFace_good);
        img_UserFace_soso = findViewById(R.id.img_UserFace_soso);
        btn_modify = findViewById(R.id.btn_modify);
        btn_customerChat = findViewById(R.id.btn_customer_chat);
        btn_recharge = findViewById(R.id.btn_recharge);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        databaseReference.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
//                    key = uids;
                }

                //이미지뷰에 선택된 이미지 로딩시키는 코드임
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();

                //이미지지파일이름 가져오는거...
                String path = (String) snapshot.child(key).child("photoUrl").getValue();

                if (path.equals("default")) {
                    Glide.with(Mypage.this)
                            .load(R.drawable.logo_main)
                            .into(iv_profile);

                }
                else {
                    storageReference.child("myprofile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(Mypage.this)
                                    .load(uri)
                                    .into(iv_profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Mypage.this, "실패", Toast.LENGTH_SHORT).show();
                            Glide.with(Mypage.this)
                                    .load(e)
                                    .into(iv_profile);
                        }
                    });

                }


                tv_id.setText(snapshot.child(key).child("id").getValue().toString());
                tv_result.setText(snapshot.child(key).child("nickName").getValue().toString());
                tv_point.setText(snapshot.child(key).child("point").getValue().toString());

                tv_UserEstimateCount.setText(snapshot.child(key).child("estimate").getValue().toString());
                int count = Integer.parseInt(snapshot.child(key).child("estimateUser").getValue().toString()) -1 ;

                tv_participate.setText( count+ "명 참여");
                int progress = Integer.parseInt(snapshot.child(key).child("estimate").getValue().toString());

                if(snapshot.child(key).child("warn").getValue().toString().isEmpty()) {
                    tv_MyWarn.setVisibility(View.INVISIBLE);
                    img_MyWarn.setVisibility(View.INVISIBLE);
                } else if (snapshot.child(key).child("warn").getValue().toString().equals("경고4회")) {
                    img_MyWarn.setVisibility(View.VISIBLE);
                    tv_MyWarn.setText(snapshot.child(key).child("warn").getValue().toString());
                    tv_MyWarn.setVisibility(View.VISIBLE);
                    tv_Message.setVisibility(View.VISIBLE);
                }else if (snapshot.child(key).child("warn").getValue().toString().equals("경고5회")){
                    img_MyWarn.setVisibility(View.VISIBLE);
                    tv_MyWarn.setText(snapshot.child(key).child("warn").getValue().toString());
                    tv_MyWarn.setVisibility(View.VISIBLE);
                    tv_Message.setText("경고 5회로 강제 회원탈퇴 될 예정입니다.");
                    tv_Message.setVisibility(View.VISIBLE);
                }else {
                    img_MyWarn.setVisibility(View.VISIBLE);
                    tv_MyWarn.setText(snapshot.child(key).child("warn").getValue().toString());
                    tv_MyWarn.setVisibility(View.VISIBLE);
                }

                if (progress >= 0 && progress <= 20) {
                    img_UserFace_angry.setVisibility(View.VISIBLE);

                    img_UserFace_smile.setVisibility(View.INVISIBLE);
                    img_UserFace_good.setVisibility(View.INVISIBLE);
                    img_UserFace_soso.setVisibility(View.INVISIBLE);
                    img_UserFace_dis.setVisibility(View.INVISIBLE);
                }else if(progress > 20 && progress <= 40) {
                    img_UserFace_dis.setVisibility(View.VISIBLE);

                    img_UserFace_angry.setVisibility(View.INVISIBLE);
                    img_UserFace_smile.setVisibility(View.INVISIBLE);
                    img_UserFace_good.setVisibility(View.INVISIBLE);
                    img_UserFace_soso.setVisibility(View.INVISIBLE);
                }else if(progress > 40 && progress <= 60){
                    img_UserFace_soso.setVisibility(View.VISIBLE);

                    img_UserFace_dis.setVisibility(View.INVISIBLE);
                    img_UserFace_angry.setVisibility(View.INVISIBLE);
                    img_UserFace_smile.setVisibility(View.INVISIBLE);
                    img_UserFace_good.setVisibility(View.INVISIBLE);
                }else if(progress > 60 && progress <= 80) {
                    img_UserFace_smile.setVisibility(View.VISIBLE);

                    img_UserFace_dis.setVisibility(View.INVISIBLE);
                    img_UserFace_angry.setVisibility(View.INVISIBLE);
                    img_UserFace_good.setVisibility(View.INVISIBLE);
                    img_UserFace_soso.setVisibility(View.INVISIBLE);
                }else {
                    img_UserFace_good.setVisibility(View.VISIBLE);

                    img_UserFace_smile.setVisibility(View.INVISIBLE);
                    img_UserFace_soso.setVisibility(View.INVISIBLE);
                    img_UserFace_dis.setVisibility(View.INVISIBLE);
                    img_UserFace_angry.setVisibility(View.INVISIBLE);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        img_btnMyBack = findViewById(R.id.img_btnMyBack);
//        btn_main = findViewById(R.id.btn_main);
        btn_buylist = findViewById(R.id.btn_buylist);
        btn_selllist = findViewById(R.id.btn_selllist);
        btn_likelist = findViewById(R.id.btn_likelist);
        btn_logout = findViewById(R.id.btn_logout);


        auth = FirebaseAuth.getInstance();

        img_btnMyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btn_buylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuylistActivity.class);
                startActivity(intent);

            }
        });

        btn_selllist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SellistActivity.class);
                startActivity(intent);

            }
        });

        btn_likelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LikelistActivity.class);
                startActivity(intent);

            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();


                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                Toast.makeText(Mypage.this, "로그아웃",Toast.LENGTH_SHORT).show();
            }
        });

        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ModifyMyInfo.class);
                startActivity(intent);
            }
        });

        btn_customerChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MessageActivity.class);
                intent.putExtra("destinationUID","kaxeobbtm5QhLEMPxLjkzXByQY53");
                startActivity(intent);
            }
        });

        btn_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PointRecharge.class);
                startActivity(intent);
            }
        });

        img_btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReservelistActivity.class);
                startActivity(intent);
            }
        });


    }
}

















