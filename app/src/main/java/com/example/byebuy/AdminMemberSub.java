package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMemberSub extends AppCompatActivity {

    String userUid;
    String userid;
    int warn;
    TextView tv_adMemSub_userID,tv_adMemSub_estimate;
    ImageView img_adMemSub_angry,img_adMemSub_diss,img_adMemSub_good,img_adMemSub_soso,img_adMemSub_smile,img_adMemSub_back;
    Button btn_adMemSub_warn;


    private RecyclerView AdMemberCycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_member_sub);

        tv_adMemSub_userID = findViewById(R.id.tv_adMemSub_userID);
        tv_adMemSub_estimate = findViewById(R.id.tv_adMemSub_estimate);
        img_adMemSub_angry = findViewById(R.id.img_adMemSub_angry);
        img_adMemSub_diss = findViewById(R.id.img_adMemSub_diss);
        img_adMemSub_good = findViewById(R.id.img_adMemSub_good);
        img_adMemSub_soso = findViewById(R.id.img_adMemSub_soso);
        img_adMemSub_smile = findViewById(R.id.img_adMemSub_smile);
        img_adMemSub_back = findViewById(R.id.img_adMemSub_back);
        btn_adMemSub_warn = findViewById(R.id.btn_adMemSub_warn);

        AdMemberCycler = findViewById(R.id.AdMemberCycler);
        AdMemberCycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        AdMemberCycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("User");
        databaseReference_product = database.getReference("Product");

        Intent intent = getIntent();
        userUid = intent.getExtras().getString("uid");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tv_adMemSub_estimate.setText(snapshot.child(userUid).child("estimate").getValue().toString());
                tv_adMemSub_userID.setText(snapshot.child(userUid).child("id").getValue().toString());

                int estimate = Integer.parseInt(snapshot.child(userUid).child("estimate").getValue().toString());

                userid = snapshot.child(userUid).child("id").getValue().toString();

                if (estimate >= 0 && estimate <= 20) {
                    img_adMemSub_angry.setVisibility(View.VISIBLE);

                    img_adMemSub_smile.setVisibility(View.INVISIBLE);
                    img_adMemSub_good.setVisibility(View.INVISIBLE);
                    img_adMemSub_soso.setVisibility(View.INVISIBLE);
                    img_adMemSub_diss.setVisibility(View.INVISIBLE);
                }else if(estimate > 20 && estimate <= 40) {
                    img_adMemSub_diss.setVisibility(View.VISIBLE);

                    img_adMemSub_angry.setVisibility(View.INVISIBLE);
                    img_adMemSub_smile.setVisibility(View.INVISIBLE);
                    img_adMemSub_good.setVisibility(View.INVISIBLE);
                    img_adMemSub_soso.setVisibility(View.INVISIBLE);
                }else if(estimate > 40 && estimate <= 60){
                    img_adMemSub_soso.setVisibility(View.VISIBLE);

                    img_adMemSub_diss.setVisibility(View.INVISIBLE);
                    img_adMemSub_angry.setVisibility(View.INVISIBLE);
                    img_adMemSub_smile.setVisibility(View.INVISIBLE);
                    img_adMemSub_good.setVisibility(View.INVISIBLE);
                }else if(estimate > 60 && estimate <= 80) {
                    img_adMemSub_smile.setVisibility(View.VISIBLE);

                    img_adMemSub_diss.setVisibility(View.INVISIBLE);
                    img_adMemSub_angry.setVisibility(View.INVISIBLE);
                    img_adMemSub_good.setVisibility(View.INVISIBLE);
                    img_adMemSub_soso.setVisibility(View.INVISIBLE);
                }else {
                    img_adMemSub_good.setVisibility(View.VISIBLE);

                    img_adMemSub_smile.setVisibility(View.INVISIBLE);
                    img_adMemSub_soso.setVisibility(View.INVISIBLE);
                    img_adMemSub_diss.setVisibility(View.INVISIBLE);
                    img_adMemSub_angry.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_product.orderByChild("seller").equalTo(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                Log.e("AdminMemberSub", String.valueOf(error.toException())); // 에러문 출력
            }
        });

        adapter = new AdminProductAdapter(arrayList,this);
        AdMemberCycler.setAdapter(adapter);


        img_adMemSub_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_adMemSub_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(AdminMemberSub.this) // TestActivity 부분에는 현재 Activity의 이름 입력.
                        .setMessage(userid + "회원에게 경고 1회 적용하겠습니까?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String warnString = snapshot.child(userUid).child("warn").getValue().toString().replace("경고","").replace("회","");

                                        if (warnString.equals("")) {
                                            warn = 0;
                                        }else {
                                            warn = Integer.parseInt(warnString);
                                        }

                                        warn++;

                                        snapshot.getRef().child(userUid).child("warn").setValue("경고"+warn + "회");
                                        Toast.makeText(getApplicationContext(), userid+"회원에게 경고1회 적용되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(getApplicationContext(), AdminMember.class);
                                        startActivity(intent1);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AdminMemberSub.this, "취소", Toast.LENGTH_SHORT).show();
                    }
                }).show();

            }
        });



    }
}