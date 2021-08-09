package com.example.byebuy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.byebuy.fragment.ChatFragment;

public class ChatList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        getSupportFragmentManager().beginTransaction().replace(R.id.FL_Chat_list,new ChatFragment()).commit();
    }
}