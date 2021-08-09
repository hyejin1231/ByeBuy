package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResult extends AppCompatActivity {

    private RecyclerView SearchRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;
    private ArrayList<Product> arrayList2;
    TextView tv_SearchTitle;
    ImageView img_btnBack;
    String resultString;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        SearchRecycler = findViewById(R.id.SearchRecycler);
        SearchRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        SearchRecycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();

        tv_SearchTitle = findViewById(R.id.tv_SearchTitle);
        img_btnBack = findViewById(R.id.img_btnBack);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Product");

        Intent intent = getIntent();

        tv_SearchTitle.setText(" 검색한 결과 : " + intent.getExtras().getString("search"));

        final String searchT = intent.getExtras().getString("search");

        databaseReference.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product pd = child.getValue(Product.class);
                    arrayList2.add(pd);
                }
                for (int i = 0; i< arrayList2.size(); i++ ) {

                    if (arrayList2.get(i).getTitle().matches(".*" + searchT +".*")) {
                        resultString = arrayList2.get(i).getTitle();
//            Toast.makeText(SearchResult.this, resultString, Toast.LENGTH_SHORT).show();
                    }

                }

                databaseReference.orderByChild("title").equalTo(resultString).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Product pd = child.getValue(Product.class);
                            arrayList.add(pd);
                        }
                        adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
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





        adapter = new CustomAdapter(arrayList,this);
        SearchRecycler.setAdapter(adapter); //리사이클러뷰에 어뎁터 연결


        img_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}





















