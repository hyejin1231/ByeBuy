package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelecCategory extends AppCompatActivity {

    RecyclerView SCrecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();

    EditText edit_SC_Search;
    ImageView img_SC_btnWrite,img_SC_btnNotice, img_SC_btnSearch,img_SC_btnChat,img_SC_btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selec_category);

        img_SC_btnNotice = findViewById(R.id.img_SC_btnNotice);
        img_SC_btnSearch = findViewById(R.id.img_SC_btnSearch);
        edit_SC_Search = findViewById(R.id.edit_SC_Search);
        img_SC_btnWrite = findViewById(R.id.img_SC_btnWrite);
        img_SC_btnChat = findViewById(R.id.img_SC_btnChat);
        img_SC_btnHome = findViewById(R.id.img_SC_btnHome);

        SCrecyclerView = findViewById(R.id.SCrecyclerView);
        SCrecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        SCrecyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance(); //파이어벵스 데이터베이스 연동
        databaseReference = database.getReference("Product");

        Intent intent = getIntent();
        String category = intent.getExtras().getString("category");

        databaseReference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot child: snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    arrayList.add(product);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SelectCategory", String.valueOf(error.toException())); // 에러문 출력
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        SCrecyclerView.setAdapter(adapter);


        img_SC_btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Sell.class);
                intent.putExtra("uid" , currentUid);
                startActivity(intent);
                finish();
            }
        });


        img_SC_btnNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Notice.class);
                startActivity(intent);
            }
        });

        img_SC_btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = edit_SC_Search.getText().toString();
                Intent intent = new Intent(getApplicationContext(), SearchResult.class);

                intent.putExtra("search", search);

                startActivity(intent);
            }
        });

        img_SC_btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });



        img_SC_btnChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChatList.class);
                startActivity(intent);
            }
        });

    }
}