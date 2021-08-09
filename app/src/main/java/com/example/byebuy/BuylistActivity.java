package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuylistActivity extends AppCompatActivity {

    private FirebaseAuth Auth;
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter adapter2;
    private RecyclerView.LayoutManager layoutManager2;
    private ArrayList<Product> arrayList2;
    private ArrayList<Product> arrayList1;
    private  ArrayList<String> arrayList3;
    private FirebaseDatabase database2;
    private DatabaseReference databaseReference2;

    ImageView img_btn_buyBack,img_btn_RegCode ;
    EditText edit_input_buyCode;
    String key,key1;
    String compare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buylist);

        img_btn_buyBack = findViewById(R.id.img_btn_buyBack);
        edit_input_buyCode = findViewById(R.id.edit_input_buyCode);
//        img_btn_RegCode = findViewById(R.id.img_btn_RegCode);

        recyclerView2 = findViewById(R.id.Buy_recyclerView); //아이디 연결
        recyclerView2.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager2 = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager2);
        arrayList2 = new ArrayList<>(); //product 객체를 담을 어레이 리스트 (어댑터 쪽으로)
        arrayList1 = new ArrayList<>();
        arrayList3 = new ArrayList<>();

        database2 = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
        databaseReference2 = database2.getReference("Product");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUid = user.getUid();

        databaseReference2.orderByChild("buyer").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList2.clear(); //기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class); //만들어뒀던 User 객체에 데이터를 담는다.
                    arrayList2.add(product); //담은 데이터드을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter2.notifyDataSetChanged();//리스트 저장 및 새로고침
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter2 = new BuyListAdapter(arrayList2, this);
        recyclerView2.setAdapter(adapter2); //리사이클러뷰에 어댑터 연결

//        final String [] uniqueList = new String[100];
//
//        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                arrayList1.clear();
//                for (DataSnapshot child: snapshot.getChildren()) {
//                    Product pd = child.getValue(Product.class);
////                    arrayList1.add(pd);
//                    arrayList3.add(pd.getUnique());
//                }
//
////                for (int i = 0; i< arrayList1.size(); i++) {
////                    uniqueList[i] = arrayList1.get(i).getUnique();
////                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        img_btn_RegCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                final String BuyKey = edit_input_buyCode.getText().toString();
//
////                for (int i = 0; i< uniqueList.length; i++) {
////                    if (BuyKey.equals(uniqueList[i])){
//
//                if (arrayList3.contains(BuyKey) == true) {
//
//                    new AlertDialog.Builder(BuylistActivity.this)
//                            .setMessage("구매 Key를 등록하겠습니까?")
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    databaseReference2.orderByChild("unique").equalTo(BuyKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
//                                            for (DataSnapshot child : snapshot.getChildren()) {
//                                                key = child.getKey();
//                                            }
//                                            snapshot.getRef().child(key).child("buyer").setValue(currentUid);
//                                            snapshot.getRef().child(key).child("status").setValue("complete");
//                                            Toast.makeText(BuylistActivity.this, "구매 등록 완료", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                            Toast.makeText(BuylistActivity.this, "취소", Toast.LENGTH_SHORT).show(); // 실행할 코드
//                                        }
//                                    });
//                                }
//
//
//                            })
//
//                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(BuylistActivity.this, "취소", Toast.LENGTH_SHORT).show(); // 실행할 코드
//                                }
//                            }).show();
//                } // end if
//                else{
//                    Toast.makeText(BuylistActivity.this, "등록키 틀림", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//
//        });
//




        img_btn_buyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}