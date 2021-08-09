package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;
    private ArrayList<String> spinner_arrayList;
    ArrayAdapter<String> spinner_arrayAdapter;

    ImageView img_btn_category,img_btn_hotclick;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference2;

    String key;
    String uids;
    EditText edit_Search;
    ImageView img_btnWrite, img_btnMypage,img_btnNotice, img_btnSearch,img_btnChat,img_btnHome;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();
    Spinner spinner1;
    int getoption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_btn_category = findViewById(R.id.img_btn_category);
        img_btn_hotclick = findViewById(R.id.img_btn_hotclick);

        img_btnNotice = findViewById(R.id.img_SC_btnNotice);
        img_btnSearch = findViewById(R.id.img_SC_btnSearch);
        img_btnHome = findViewById(R.id.img_SC_home);
        edit_Search = findViewById(R.id.edit_SC_Search);
        img_btnWrite = findViewById(R.id.img_SC_btnWrite);
        img_btnMypage = findViewById(R.id.img_SC_btnMypage);
        img_btnChat = findViewById(R.id.img_SC_btnChat);

        recyclerView =  findViewById(R.id.SCrecyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // Product 객체를 담을 어레이 리스트
        spinner1 = findViewById(R.id.spinner1);
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Product"); // DB 테이블 연동
        databaseReference2 = database.getReference("User"); // DB 테이블 연동

        uids = FirebaseAuth.getInstance().getUid();

        spinner_arrayList = new ArrayList<>();
        spinner_arrayList.add("전체");
        spinner_arrayList.add("판매중인 상품");
        spinner_arrayList.add("판매종료 상품");
        spinner_arrayList.add("예약중인 상품");

        spinner_arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinner_arrayList);
        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner1.setAdapter(spinner_arrayAdapter);

        //스피너 선택안하면 기본..
        getoption =0;
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),spinner_arrayList.get(i)+" 내역", Toast.LENGTH_SHORT).show();
                getoption = i;
                if(getoption==0) { //전체 (디폴트)
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 리스트가 존재하지 않게 초기화
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터리스트를 추출해내는것
                                Product product = snapshot.getValue(Product.class); // 만들어뒀던 product 객체에 데이터를 담는다.
                                arrayList.add(product); // 담은 데이터를 배열 리스트에 넣고 리사이클러뷰로 보낼 준비
                            }
                            adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // DB 가져오던 중 에러 발생시
                            Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });

                }else if(getoption==1){ //판매중
                    //리사이클러뷰 띄워주는 코드
                    databaseReference.orderByChild("status").equalTo("selling").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 리스트가 존재하지 않게 초기화
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터리스트를 추출해내는것
                                Product product = snapshot.getValue(Product.class); // 만들어뒀던 product 객체에 데이터를 담는다.
                                arrayList.add(product); // 담은 데이터를 배열 리스트에 넣고 리사이클러뷰로 보낼 준비

                            }
                            adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // DB 가져오던 중 에러 발생시
                            Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });

                }else if(getoption==2) { //판매종료
                    databaseReference.orderByChild("status").equalTo("complete").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 리스트가 존재하지 않게 초기화
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터리스트를 추출해내는것
                                Product product = snapshot.getValue(Product.class); // 만들어뒀던 product 객체에 데이터를 담는다.
                                arrayList.add(product); // 담은 데이터를 배열 리스트에 넣고 리사이클러뷰로 보낼 준비

                            }
                            adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });
                }
                else if(getoption==3){ // 예약중
                    databaseReference.orderByChild("status").equalTo("reservation").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear(); // 기존 배열 리스트가 존재하지 않게 초기화
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터리스트를 추출해내는것
                                Product product = snapshot.getValue(Product.class); // 만들어뒀던 product 객체에 데이터를 담는다.
                                arrayList.add(product); // 담은 데이터를 배열 리스트에 넣고 리사이클러뷰로 보낼 준비

                            }
                            adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });

                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // 파이어베이스 데이터베이스 데이터 받아오는 곳
                        arrayList.clear(); // 기존 배열 리스트가 존재하지 않게 초기화
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터리스트를 추출해내는것
                            Product product = snapshot.getValue(Product.class); // 만들어뒀던 product 객체에 데이터를 담는다.
                            arrayList.add(product); // 담은 데이터를 배열 리스트에 넣고 리사이클러뷰로 보낼 준비

                        }
                        adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // DB 가져오던 중 에러 발생시
                        Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                });
            }
        });


        //프사 띄워주는 코드
        databaseReference2.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }

                //다혜다혜
                //이미지뷰에 선택된 이미지 로딩시키는 코드임
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();

                //이미지지파일이름 가져오는거...
                String path = (String) snapshot.child(key).child("photoUrl").getValue();


                if (path.equals("default")) {
                    Glide.with(MainActivity.this)
                            .load(R.drawable.logo_main)
                            .into(img_btnMypage);

                }
                else {
                    storageReference.child("myprofile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(MainActivity.this)
                                    .load(uri)
                                    .into(img_btnMypage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "실패", Toast.LENGTH_SHORT).show();
//                                    Glide.with(MyPage.this)
//                                            .load(e)
//                                            .into(iv_profile);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new CustomAdapter(arrayList,this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어뎁터 연결


        img_btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("uid" , uids);
                startActivity(intent);
            }
        });

        img_btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Sell.class);
                intent.putExtra("uid" , uids);
                startActivity(intent);
            }
        });

        img_btnMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Mypage.class);
                intent.putExtra("uid" , uids);

                //문제 해결
                startActivity(intent);
//                finish();
            }
        });

        img_btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                startActivity(intent);
            }
        });

        img_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = edit_Search.getText().toString();
                Intent intent = new Intent(getApplicationContext(), SearchResult.class);

                intent.putExtra("search", search);

                startActivity(intent);
            }
        });

        img_btnChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChatList.class);
                startActivity(intent);
            }
        });

        img_btn_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Category.class);
                startActivity(intent);
            }
        });


        img_btn_hotclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), HotClickView.class);
                startActivity(intent2);
            }
        });






    }// end oncreate


}






















