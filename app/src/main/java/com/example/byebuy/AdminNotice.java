package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminNotice extends AppCompatActivity {

    ImageView btn_writeNotice;

    private RecyclerView AdNoticeRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<board> arrayList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notice);

        AdNoticeRecycler = findViewById(R.id.AdMemRecycler);
        AdNoticeRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        AdNoticeRecycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Notice");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    board bd2  = snapshot1.getValue(board.class);
                    arrayList.add(bd2);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        adapter = new AdminNoticeAdpater(arrayList, this);
        AdNoticeRecycler.setAdapter(adapter);



        btn_writeNotice = findViewById(R.id.btn_writeNotice);

        btn_writeNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteNotice.class);
                startActivity(intent);
            }
        });
    }
}