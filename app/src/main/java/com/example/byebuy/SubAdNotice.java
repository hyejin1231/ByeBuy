package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubAdNotice extends AppCompatActivity {

    EditText edit_adnTitle, edit_adnContent;
    TextView tv_adnDate, tv_adnCategory;
    Button btn_adnDelete, btn_adnUpdate;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    String key,key1;
    String keyTitle, keyTitle1;

    String upTitle, upContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_ad_notice);

        edit_adnContent = findViewById(R.id.edit_adnContent);
        edit_adnTitle = findViewById(R.id.edit_adnTitle);
        tv_adnCategory = findViewById(R.id.tv_adnCatetgory);
        tv_adnDate = findViewById(R.id.tv_adnDate);
        btn_adnDelete = findViewById(R.id.btn_adnDelete);
        btn_adnUpdate = findViewById(R.id.btn_adnUpdate);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Notice");

        Intent intent = getIntent();

        edit_adnTitle.setText(intent.getExtras().getString("adTitle"));
        edit_adnContent.setText(intent.getExtras().getString("adContent"));
        tv_adnDate.setText(intent.getExtras().getString("adDate"));
        tv_adnCategory.setText(intent.getExtras().getString("adCategory"));

        keyTitle = intent.getExtras().getString("adTitle");
        keyTitle1= intent.getExtras().getString("adTitle");

        btn_adnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.orderByChild("nt_title").equalTo(keyTitle1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            key1 = child.getKey();
                        }

                        upTitle= edit_adnTitle.getText().toString();
                        upContent = edit_adnContent.getText().toString();


                        snapshot.getRef().child(key1).child("nt_title").setValue(upTitle);
                        snapshot.getRef().child(key1).child("nt_content").setValue(upContent);
                        Toast.makeText(getApplicationContext(),"공지사항 수정 완료",Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(getApplicationContext(), AdminNotice.class);
                        startActivity(intent1);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btn_adnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                databaseReference.orderByChild("nt_title").equalTo(keyTitle).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot child : snapshot.getChildren()) {
                            key = child.getKey();
                        }

                        snapshot.getRef().child(key).removeValue();
                        Toast.makeText(getApplicationContext(),"공지사항 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(getApplicationContext(), AdminNotice.class);
                        startActivity(intent1);
                        finish();



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),"공지사항 삭제실패",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}