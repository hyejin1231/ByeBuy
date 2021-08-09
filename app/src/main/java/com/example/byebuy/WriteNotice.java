package com.example.byebuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WriteNotice extends AppCompatActivity {

    EditText edit_ntCategory, edit_ntDate, edit_ntTitle, edit_ntContent;
    Button btn_ntRegister;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_notice);

        edit_ntCategory = findViewById(R.id.edit_ntCategory);
        edit_ntContent = findViewById(R.id.edit_ntContent);
        edit_ntDate = findViewById(R.id.edit_ntDate);
        edit_ntTitle = findViewById(R.id.edit_ntTtile);
        btn_ntRegister = findViewById(R.id.btn_ntRegister);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Notice");

        btn_ntRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edit_ntTitle.getText().toString();
                String date = edit_ntDate.getText().toString();
                String category = edit_ntCategory.getText().toString();
                String content = edit_ntContent.getText().toString();

                board bd = new board(title, content, date, category);

                databaseReference.push().setValue(bd);

                Toast.makeText(getApplicationContext(),"공지사항 등록 완료", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), AdminNotice.class);
                startActivity(intent);
                finish();
            }
        });
    }
}