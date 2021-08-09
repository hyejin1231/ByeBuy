package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Estimate extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference, databaseReference2;
    TextView tv_es_buyer, tv_es_seller,tv_es_count,tv_es_tittle;
    SeekBar seekBar;
    Button btn_es_register;
    ImageView img_face_smile,img_face_angry,img_face_disapp,img_face_good,img_face_soso,img_btn_es_back;
    String key,key2;
    String keyUser, keyTitle;

    public int number = 0;
    int UserInputNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);
        Intent intent = getIntent();

        tv_es_tittle = findViewById(R.id.tv_es_tittle);
        img_btn_es_back = findViewById(R.id.img_btn_es_back);
        tv_es_buyer = findViewById(R.id.tv_es_buyer);
        tv_es_seller = findViewById(R.id.tv_es_seller);
        seekBar = findViewById(R.id.seekBar);
        tv_es_count = findViewById(R.id.tv_es_count);
        btn_es_register = findViewById(R.id.btn_es_register);
        img_face_smile = findViewById(R.id.img_face_smile);
        img_face_angry = findViewById(R.id.img_face_angry);
        img_face_disapp = findViewById(R.id.img_face_disapp);
        img_face_good = findViewById(R.id.img_face_good);
        img_face_soso = findViewById(R.id.img_face_soso);

        String buyer22= intent.getExtras().getString("buyer2");
        String seller22 =intent.getExtras().getString("seller2") ;

        final String seller = intent.getExtras().getString("seller2");

        database = FirebaseDatabase.getInstance(); //파이어벵스 데이터베이스 연동
        databaseReference = database.getReference("User");
        databaseReference2 = database.getReference("Product");

        databaseReference2.orderByChild("seller").equalTo(seller22).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    keyTitle = child.getKey();
                }

                tv_es_tittle.setText(snapshot.child(keyTitle).child("title").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.orderByChild("uid").equalTo(buyer22).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()){
                    keyUser = child.getKey();
                }

                tv_es_buyer.setText(snapshot.child(keyUser).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.orderByChild("uid").equalTo(seller22).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()){
                    keyUser = child.getKey();
                }

                tv_es_seller.setText(snapshot.child(keyUser).child("id").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tv_es_count.setText("판매자 점수: " + progress);

                UserInputNumber = progress;
                if (progress >= 0 && progress <= 20) {
                    img_face_angry.setVisibility(View.VISIBLE);

                    img_face_smile.setVisibility(View.INVISIBLE);
                    img_face_good.setVisibility(View.INVISIBLE);
                    img_face_soso.setVisibility(View.INVISIBLE);
                    img_face_disapp.setVisibility(View.INVISIBLE);
                }else if(progress > 20 && progress <= 40) {
                    img_face_disapp.setVisibility(View.VISIBLE);

                    img_face_angry.setVisibility(View.INVISIBLE);
                    img_face_smile.setVisibility(View.INVISIBLE);
                    img_face_good.setVisibility(View.INVISIBLE);
                    img_face_soso.setVisibility(View.INVISIBLE);
                }else if(progress > 40 && progress <= 60){
                    img_face_soso.setVisibility(View.VISIBLE);

                    img_face_disapp.setVisibility(View.INVISIBLE);
                    img_face_angry.setVisibility(View.INVISIBLE);
                    img_face_smile.setVisibility(View.INVISIBLE);
                    img_face_good.setVisibility(View.INVISIBLE);
                }else if(progress > 60 && progress <= 80) {
                    img_face_smile.setVisibility(View.VISIBLE);

                    img_face_disapp.setVisibility(View.INVISIBLE);
                    img_face_angry.setVisibility(View.INVISIBLE);
                    img_face_good.setVisibility(View.INVISIBLE);
                    img_face_soso.setVisibility(View.INVISIBLE);
                }else {
                    img_face_good.setVisibility(View.VISIBLE);

                    img_face_smile.setVisibility(View.INVISIBLE);
                    img_face_soso.setVisibility(View.INVISIBLE);
                    img_face_disapp.setVisibility(View.INVISIBLE);
                    img_face_angry.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                number = seekBar.getProgress();
//                tv_es_count.setText("onStart TrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                tv_es_count.setText("onStop TrackingTouch");
                number = seekBar.getProgress();
            }
        });


        btn_es_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(Estimate.this)
                        .setMessage("판매자 평가를 완료하겠습니까?")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                databaseReference.orderByChild("uid").equalTo(seller).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            key = child.getKey();
                                        }

                                        String estimateString = snapshot.child(key).child("estimate").getValue().toString();
                                        String estimateUserString = snapshot.child(key).child("estimateUser").getValue().toString();

                                        int estimateUserCount = Integer.parseInt(estimateUserString);
                                        estimateUserCount++;

                                        int estimateCount = Integer.parseInt(estimateString);
                                        int BuyerEstimate = UserInputNumber;

                                        int average = (estimateCount + BuyerEstimate) / 2;

                                        snapshot.getRef().child(seller).child("estimate").setValue(average);
                                        snapshot.getRef().child(seller).child("estimateUser").setValue(estimateUserCount);

                                        Toast.makeText(Estimate.this, "판매자 평가 완료 ", Toast.LENGTH_SHORT).show();

                                        databaseReference2.orderByChild("seller").equalTo(seller).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot child : snapshot.getChildren()) {
                                                    key2 = child.getKey();
                                                }

                                                snapshot.getRef().child(key2).child("estiStatus").setValue("end");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Estimate.this, "취소", Toast.LENGTH_SHORT).show(); // 실행할 코드
                    }
                }).show();


            }





        });

        img_btn_es_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}


















