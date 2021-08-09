package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Notice extends AppCompatActivity {


    private RecyclerView noticeRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<board> arrayList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    ImageView img_btn_noticeBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        noticeRecycler = findViewById(R.id.noticeRecycler);
        noticeRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        noticeRecycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // 객체 담을 arraylist
        img_btn_noticeBack = findViewById(R.id.img_btn_noticeBack);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Notice"); // notice 테이블 연동

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    board bd  = snapshot.getValue(board.class);
                    arrayList.add(bd);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapter = new BoardAdpater(arrayList, this);
        noticeRecycler.setAdapter(adapter);

        img_btn_noticeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}