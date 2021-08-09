package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PointRecharge extends AppCompatActivity {

    TextView tv_myPoint,tv_pointName;
    ImageView img_btnRecharge,img_btn_sendPoint,img_pointBack,img_btn_change;

    private RecyclerView PointRecycler,DepositRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter3;
    private RecyclerView.LayoutManager layoutManager3;
    private ArrayList<Point> arrayList;
    private ArrayList<Point> arrayList3;
    private  ArrayList<String> arrayList1;
    private  ArrayList<String> arrayList4;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference_Point, databaseReference_User, databaseReference_Product;

    String key,key1,abcd,key2;
    private String currentNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_recharge);

        PointRecycler = findViewById(R.id.PointRecycler);
        PointRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        PointRecycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        DepositRecycler = findViewById(R.id.DepositRecycler);
        DepositRecycler.setHasFixedSize(true);
        layoutManager3 = new LinearLayoutManager(this);
        DepositRecycler.setLayoutManager(layoutManager3);
        arrayList3 = new ArrayList<>();
        arrayList4 = new ArrayList<>();

        tv_pointName = findViewById(R.id.tv_pointName);
        tv_myPoint = findViewById(R.id.tv_myPoint);
        img_btn_sendPoint = findViewById(R.id.img_btn_sendPoint);
        img_btnRecharge = findViewById(R.id.img_btnRecharge);
        img_pointBack = findViewById(R.id.img_pointBack);
        img_btn_change = findViewById(R.id.img_btn_change);


        database = FirebaseDatabase.getInstance();
        databaseReference_User = database.getReference("User");
        databaseReference_Point = database.getReference("Point");
        databaseReference_Product = database.getReference("Product");

        arrayList1 = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUid = user.getUid();

        databaseReference_User.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    key = child.getKey();
                }
                tv_pointName.setText(snapshot.child(key).child("nickName").getValue().toString() + " 포인트");
                tv_myPoint.setText(snapshot.child(key).child("point").getValue().toString()+" 원");
                currentNickname = tv_pointName.getText().toString().replace(" 포인트", "");
                databaseReference_Point.orderByChild("depoister").equalTo(currentNickname).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList3.clear();
                        for (DataSnapshot child: snapshot.getChildren()){
                            Point p = child.getValue(Point.class);
                            arrayList3.add(0,p);
                        }

                        adapter3.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        databaseReference_Point.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot child: snapshot.getChildren()){
                    Point p = child.getValue(Point.class);
                    arrayList.add(0,p);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        // 디비에 있는 모든 닉네임 배열에 저장
        databaseReference_User.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList1.clear();
                for (DataSnapshot child: snapshot.getChildren()){
                    User user1 = child.getValue(User.class);
                    arrayList1.add(user1.getNickName());
                }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 디비에 있는 모든 구매키 배열에 저장
        databaseReference_Product.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList4.clear();
                for (DataSnapshot child: snapshot.getChildren()) {
                    Product pd = child.getValue(Product.class);
                    arrayList4.add(pd.getUnique());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        adapter = new PointAdpater(arrayList, this);
        adapter3 = new PointAdpater(arrayList3, this);
        PointRecycler.setAdapter(adapter);
        DepositRecycler.setAdapter(adapter3);


        img_pointBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final EditText et=  new EditText(PointRecharge.this);
        // 충전하기
        img_btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PointRecharge.this);
                builder.setTitle("충전하기");
                builder.setMessage("충천할 포인트를 입력하세요.");
                builder.setView(et);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int money = Integer.parseInt(et.getText().toString());

                        databaseReference_User.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child: snapshot.getChildren()) {
                                    key1 = child.getKey();
                                }

                                int presentPoint = Integer.parseInt(snapshot.child(key1).child("point").getValue().toString());

                                snapshot.getRef().child(key1).child("point").setValue(money + presentPoint);
                                tv_myPoint.setText(money+presentPoint + " 원");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        String pointway = "recharge";
                        String sender = "";
                        String depoister = "";
                        String uid = currentUid;

                        Point point = new Point(uid,pointway,money,depoister,sender);

                        databaseReference_Point.push().setValue(point);

                    Toast.makeText(getApplicationContext(), "충전 완료", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        final EditText et2=  new EditText(PointRecharge.this);
        final EditText et3=  new EditText(PointRecharge.this);
        final EditText et4 = new EditText(PointRecharge.this);
        // 송금하기
        img_btn_sendPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentNickname = tv_pointName.getText().toString().replace(" 포인트", "");
                AlertDialog.Builder builder = new AlertDialog.Builder(PointRecharge.this);
                builder.setTitle("송금하기");
                builder.setMessage("송금할 분의 닉네임을 입력하세요.");
                builder.setView(et2);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String name = et2.getText().toString();

                        if (arrayList1.contains(name) == true) {

                            AlertDialog.Builder builder4 = new AlertDialog.Builder(PointRecharge.this);
                            builder4.setTitle("송금하기");
                            builder4.setMessage("구매한 물품의 구매키를 입력하세요.");
                            builder4.setView(et4);
                            builder4.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String uniqueKey = et4.getText().toString();

                                    if(arrayList4.contains(uniqueKey) == true){
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(PointRecharge.this);
                                        builder2.setTitle("송금하기");
                                        builder2.setMessage("송금할 포인트를 입력하세요.");
                                        builder2.setView(et3);
                                        builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final int money = Integer.parseInt(et3.getText().toString());
                                                databaseReference_User.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot child: snapshot.getChildren()) {
                                                            key2 = child.getKey();
                                                        }

                                                        int presentPoint = Integer.parseInt(snapshot.child(key2).child("point").getValue().toString());

                                                        snapshot.getRef().child(key2).child("point").setValue(presentPoint-money);
                                                        tv_myPoint.setText(presentPoint-money + " 원");
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                                String pointway = "send";
                                                String sender = currentNickname;
                                                String depoister = name;
                                                String uid = currentUid;

                                                Point point = new Point(uid,pointway,money,depoister,sender);

                                                databaseReference_Point.push().setValue(point);

                                                databaseReference_User.orderByChild("nickName").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot child : snapshot.getChildren()) {
                                                            abcd = child.getKey();
                                                        }
                                                        int presentPoint = Integer.parseInt(snapshot.child(abcd).child("point").getValue().toString());

                                                        snapshot.getRef().child(abcd).child("point").setValue(presentPoint + money);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                  databaseReference_Product.orderByChild("unique").equalTo(uniqueKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                                        for (DataSnapshot child : snapshot.getChildren()) {
                                                            key = child.getKey();
                                                        }
                                                        snapshot.getRef().child(key).child("reserver").setValue(currentUid);
                                                        snapshot.getRef().child(key).child("status").setValue("reservation");
                                                        Toast.makeText(PointRecharge.this, "예약중", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(PointRecharge.this, "취소", Toast.LENGTH_SHORT).show(); // 실행할 코드
                                                    }
                                                });
                                            }
                                        });
                                        builder2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        builder2.show();
                                    }

                                }
                            });
                            builder4.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder4.show();


                        }else {
                            Toast.makeText(getApplicationContext(), "닉네임을 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

            }
        });


        // 환전하기
        img_btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PointRecharge.this);
                builder.setTitle("환전하기");
                builder.setMessage("환전할 포인트를 입력하세요.");
                builder.setView(et3);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int money = Integer.parseInt(et3.getText().toString());

                        databaseReference_User.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child: snapshot.getChildren()) {
                                    key1 = child.getKey();
                                }

                                int presentPoint = Integer.parseInt(snapshot.child(key1).child("point").getValue().toString());

                                snapshot.getRef().child(key1).child("point").setValue(presentPoint-money);
                                tv_myPoint.setText(presentPoint-money + " 원");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        String pointway = "exchange";
                        String sender = "";
                        String depoister = "";
                        String uid = currentUid;

                        Point point = new Point(uid,pointway,money,depoister,sender);

                        databaseReference_Point.push().setValue(point);

                        Toast.makeText(getApplicationContext(), "환전 완료", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });



    }
}
























