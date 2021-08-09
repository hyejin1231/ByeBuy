package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LikelistActivity extends AppCompatActivity {

    ImageView img_btn_likeListBack;
    private FirebaseAuth Auth;
    private RecyclerView recyclerView3;
    private RecyclerView.Adapter adapter3;
    private RecyclerView.LayoutManager layoutManager3;
    private ArrayList<Product> arrayList3;
    private ArrayList<String> arrayList2;
    private FirebaseDatabase database3;
    private DatabaseReference databaseReference2,databaseReference3,databaseReference_like;
    String key2,key3,key4;
    String unique;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likelist);

        img_btn_likeListBack = findViewById(R.id.img_btn_likeListBack);
        recyclerView3 = findViewById(R.id.Like_recyclerView); //아이디 연결
        recyclerView3.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager3 = new LinearLayoutManager(this);
        recyclerView3.setLayoutManager(layoutManager3);
        arrayList3 = new ArrayList<>(); //product 객체를 담을 어레이 리스트 (어댑터 쪽으로
        arrayList2 = new ArrayList<>();

        database3 = FirebaseDatabase.getInstance();
        databaseReference3 = database3.getReference("Like");
        databaseReference2 = database3.getReference("Product");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUid = user.getUid();
        adapter3 = new LikeListAdapter(arrayList3, this);
        recyclerView3.setAdapter(adapter3); //리사이클러뷰에 어댑터 연결
        databaseReference_like = database3.getReference("Like");

        databaseReference3.orderByChild("id").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                arrayList3.clear();
                for (DataSnapshot child1 : snapshot1.getChildren()) {
                    key3 = child1.getKey();
                    key4 = (snapshot1.child(key3).child("unique").getValue()).toString();

                    databaseReference2.orderByChild("unique").equalTo(key4).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            for (DataSnapshot child2 : snapshot2.getChildren()) {
                                Product product = child2.getValue(Product.class); //만들어뒀던 User 객체에 데이터를 담는다.
                                arrayList3.add(product);
                            }
                            adapter3.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                adapter3.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        adapter3 = new LikeListAdapter(arrayList3, this);
        recyclerView3.setAdapter(adapter3);


        img_btn_likeListBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), .class);
//                startActivity(intent);
                finish();
            }
        });

    }
}