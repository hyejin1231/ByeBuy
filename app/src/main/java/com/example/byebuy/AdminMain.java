package com.example.byebuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminMain extends AppCompatActivity {

    Button btn_member, btn_goods, btn_notice, btn_adminchat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        btn_goods = findViewById(R.id.btn_goods);
        btn_member = findViewById(R.id.btn_member);
        btn_notice = findViewById(R.id.btn_notice);
        btn_adminchat = findViewById(R.id.btn_admin_chat);

        btn_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminGoods.class);
                startActivity(intent);
            }
        });

        btn_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminMember.class);
                startActivity(intent);
            }
        });

        btn_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminNotice.class);
                startActivity(intent);
            }
        });

        btn_adminchat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChatList.class);
                startActivity(intent);
            }
        });
    }
}