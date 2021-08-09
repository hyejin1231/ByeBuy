package com.example.byebuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Category extends AppCompatActivity {

    ImageView img_btn_digital,img_btn_furni,img_btn_food
            ,img_btn_wcloth,img_btn_wgoods,img_btn_kids,
            img_btn_mcloths,img_btn_game,img_btn_beauty,
            img_btn_pet,img_btn_ticket,img_btn_etc,img_btn_cateBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        img_btn_digital = findViewById(R.id.img_btn_digital); //디지털/가전
        img_btn_furni = findViewById(R.id.img_btn_furni); //가구/인테리어
        img_btn_food = findViewById(R.id.img_btn_food); //생활/가공식품
        img_btn_wcloth = findViewById(R.id.img_btn_wcloth); //여성의류
        img_btn_wgoods = findViewById(R.id.img_btn_wgoods); //여성잡화
        img_btn_kids = findViewById(R.id.img_btn_kids); // 유아용/아동도서
        img_btn_mcloths  = findViewById(R.id.img_btn_mcloths); //남성패선/잡화
        img_btn_game = findViewById(R.id.img_btn_game); // 게임/취미
        img_btn_beauty = findViewById(R.id.img_btn_beauty); //뷰티/미용
        img_btn_pet  = findViewById(R.id.img_btn_pet); //반려동물용품
        img_btn_ticket = findViewById(R.id.img_btn_ticket); //도서/티켓/음반
        img_btn_etc = findViewById(R.id.img_btn_etc); //기타중고물품
        img_btn_cateBack = findViewById(R.id.img_btn_cateBack);

        img_btn_digital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"디지털/가전" );
                startActivity(intent);
            }
        });


        img_btn_furni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"가구/인테리어" );
                startActivity(intent);
            }
        });

        img_btn_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"생활/가공식품" );
                startActivity(intent);
            }
        });

        img_btn_wcloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"여성의류" );
                startActivity(intent);
            }
        });

        img_btn_wgoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"여성잡화" );
                startActivity(intent);
            }
        });

        img_btn_kids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"유아용/아동도서" );
                startActivity(intent);
            }
        });

        img_btn_mcloths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"남성패션/잡화" );
                startActivity(intent);
            }
        });

        img_btn_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"게임/취미" );
                startActivity(intent);
            }
        });

        img_btn_beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"뷰티/미용" );
                startActivity(intent);
            }
        });

        img_btn_pet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"반려동물용품" );
                startActivity(intent);
            }
        });

        img_btn_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"도서/티켓/음반" );
                startActivity(intent);
            }
        });

        img_btn_etc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelecCategory.class);
                intent.putExtra("category" ,"기타중고물품" );
                startActivity(intent);
            }
        });

        img_btn_cateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}