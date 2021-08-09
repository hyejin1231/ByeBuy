package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class berrybox extends AppCompatActivity {

    Button btn_open;
    TextView tv_explain;
    ImageView box01,box02,box03,box04,box05,box06,box07, box08, box09, box10, box11, box12, box13, box14, box15, box16;
    private ArrayList<Integer> arrayList;
//private ArrayList<String> arrayList;
//    int min[] = new int[1];
    String key, key1,key2;
    static  int abcd;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference_BerryBox, databaseReference_Product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berrybox);

        btn_open = findViewById(R.id.btn_open);
        tv_explain = findViewById(R.id.tv_explain);
        box01 = findViewById(R.id.box1);
        box02 = findViewById(R.id.box2);
        box03 = findViewById(R.id.box3);
        box04 = findViewById(R.id.box4);
        box05 = findViewById(R.id.box5);
        box06 = findViewById(R.id.box6);
        box07 = findViewById(R.id.box7);
        box08 = findViewById(R.id.box8);
        box09 = findViewById(R.id.box9);
        box10 = findViewById(R.id.box10);
        box11 = findViewById(R.id.box11);
        box12 = findViewById(R.id.box12);
        box13 = findViewById(R.id.box13);
        box14 = findViewById(R.id.box14);
        box15 = findViewById(R.id.box15);
        box16 = findViewById(R.id.box16);
        arrayList = new ArrayList<>();



        database = FirebaseDatabase.getInstance(); //파이어벵스 데이터베이스 연동
        databaseReference_BerryBox = database.getReference("BerryBox");
        databaseReference_Product = database.getReference("Product");

        final Intent intent = getIntent();

        final String box_buyer = intent.getExtras().getString("reserver");
        final String box_seller = intent.getExtras().getString("seller");
        final String box_place = intent.getExtras().getString("place");


//        int a = this.findbox();
//        this.effectbox(a);


        if (intent.getExtras().getString("request").equals("상품 넣기")) {
            findbox();
            tv_explain.setText( "표시된 베리박스를 확인하고\n  버튼을 눌러 물건을 넣으세요");
        }else if (intent.getExtras().getString("request").equals("상품 찾기")){
            effectbox(Integer.parseInt(box_place.replace("가천대역_","")));
            tv_explain.setText( "표시된 베리박스를 확인하고\n  버튼을 눌러 물건을 찾아가세요");
        }


        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btn_open.getText().toString().equals("OPEN")) {
                    Toast.makeText(berrybox.this, "베리박스의 문을 성공적으로 열었습니다.", Toast.LENGTH_SHORT).show();
                    btn_open.setText("CLOSE");

                    if(intent.getExtras().getString("request").equals("상품 넣기")){
                        tv_explain.setText( "물건을 넣은 후,\n  CLOSE 버튼을 누르세요");
//                        final int BoxMinInter = findbox();
                        Toast.makeText(berrybox.this, String.valueOf(min2), Toast.LENGTH_SHORT).show();
                        final int BoxMinInter = min2;
                        final String BoxMinString = "가천대역_" + BoxMinInter;

                        databaseReference_BerryBox.orderByChild("box_id").equalTo(BoxMinString).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child: snapshot.getChildren()){
                                    key = child.getKey();
                                }

                                snapshot.getRef().child(key).child("box_seller").setValue(box_seller);
                                snapshot.getRef().child(key).child("box_buyer").setValue(box_buyer);
                                snapshot.getRef().child(key).child("box_signal").setValue(2);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        databaseReference_Product.orderByChild("seller").equalTo(box_seller).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child: snapshot.getChildren()){
                                    key1 = child.getKey();
                                }
                                snapshot.getRef().child(key1).child("place").setValue(BoxMinString);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }else if (intent.getExtras().getString("request").equals("상품 찾기")){
                        tv_explain.setText( "물건을 찾은 후,\n  CLOSE 버튼을 누르세요");
                         abcd = Integer.parseInt(intent.getExtras().getString("place").replace("가천대역_",""));
                        effectbox(abcd);
                        databaseReference_BerryBox.orderByChild("box_id").equalTo(box_place).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child: snapshot.getChildren()){
                                    key = child.getKey();
                                }
                                snapshot.getRef().child(key).child("box_signal").setValue(2);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                }else if (btn_open.getText().toString().equals("CLOSE")) {
//                    final int BoxMinInter = findbox();
                    final int BoxMinInter = min2;
                    final String BoxMinString = "가천대역_" + BoxMinInter;

                    if(intent.getExtras().getString("request").equals("상품 넣기")) {

                        databaseReference_BerryBox.orderByChild("box_id").equalTo(BoxMinString).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child : snapshot.getChildren()) {
                                    key = child.getKey();
                                }

                                snapshot.getRef().child(key).child("box_signal").setValue(0);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        databaseReference_Product.orderByChild("seller").equalTo(box_seller).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child: snapshot.getChildren()){
                                    key1 = child.getKey();
                                }
                                snapshot.getRef().child(key1).child("placestatus").setValue("판매자 물건 넣음");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }else if (intent.getExtras().getString("request").equals("상품 찾기")){
                        abcd = Integer.parseInt(intent.getExtras().getString("place").replace("가천대역_",""));
                        effectbox(abcd);
                        databaseReference_BerryBox.orderByChild("box_id").equalTo(box_place).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child: snapshot.getChildren()){
                                    key = child.getKey();
                                }
                                snapshot.getRef().child(key).child("box_buyer").setValue("");
                                snapshot.getRef().child(key).child("box_seller").setValue("");
                                snapshot.getRef().child(key).child("box_photourl").setValue("");
                                snapshot.getRef().child(key).child("box_signal").setValue(1);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        final String presentReserver = intent.getExtras().getString("reserver");

                        databaseReference_Product.orderByChild("place").equalTo(box_place).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot child: snapshot.getChildren()){
                                    key2 = child.getKey();
                                }

                                snapshot.getRef().child(key2).child("reserver").removeValue();
                                snapshot.getRef().child(key2).child("placestatus").setValue("구매자 물건 찾음");
                                snapshot.getRef().child(key2).child("buyer").setValue(presentReserver);
                                snapshot.getRef().child(key2).child("status").setValue("complete");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    Toast.makeText(berrybox.this, "베리박스의 문을 닫았습니다. 이용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });



    }
           static int min2;
       public void findbox() {
           // 디비에 있는 모든 신호 배열에 저장
           databaseReference_BerryBox.orderByChild("box_signal").equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   arrayList.clear();
                   for (DataSnapshot child: snapshot.getChildren()){
                       berry b = child.getValue(berry.class);
                       arrayList.add(Integer.parseInt(b.getBox_id().replace("가천대역_","")));
                   }

//                   for (int i = 0; i< arrayList.size(); i++ ) {
//                       Toast.makeText(berrybox.this, String.valueOf(arrayList.get(i)), Toast.LENGTH_SHORT).show();
//                   }
                   min2 = arrayList.get(0);
                   for(int i = 0; i< arrayList.size(); i++) {
                       if(min2> arrayList.get(i)) {
                           min2 =  arrayList.get(i);
                       }
                   }
                        effectbox(min2);
                       Toast.makeText(berrybox.this, String.valueOf(min2), Toast.LENGTH_SHORT).show();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });

//           for(int i = 0; i< arrayList.size(); i++) {
////               if(min2> arrayList.get(i)) {
////                   min2 =  arrayList.get(i);
////               }
//               Toast.makeText(berrybox.this, String.valueOf(arrayList.get(i)), Toast.LENGTH_SHORT).show();
//           }
//           Toast.makeText(berrybox.this, String.valueOf(min2), Toast.LENGTH_SHORT).show();

//           return min2;
       }

       public void effectbox(int a) {
//           int minBoxId =  this.findbox();
           int minBoxId = a;
//           Toast.makeText(berrybox.this, String.valueOf(min2), Toast.LENGTH_SHORT).show();
//           int minBoxId = min2;
           String BoxId = "box"+ minBoxId;

           if (BoxId.equals("box1")) {
               box01.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box2")) {
               box02.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box3")) {
               box03.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box4")) {
               box04.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box5")) {
               box05.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box6")) {
               box06.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box7")) {
               box07.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box8")) {
               box08.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box9")) {
               box09.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box10")) {
               box10.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box11")) {
               box11.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box12")) {
               box12.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box13")) {
               box13.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box14")) {
               box14.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box15")) {
               box15.setBackgroundResource(R.drawable.selected_box);
           }else if(BoxId.equals("box16")) {
               box16.setBackgroundResource(R.drawable.selected_box);
           }

       }
}














