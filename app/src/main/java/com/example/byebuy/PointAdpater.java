package com.example.byebuy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PointAdpater extends RecyclerView.Adapter<PointAdpater.CustomViewHolder> {

    private ArrayList<Point> arrayList;
    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference_User;

    String key;
    String currentNickname;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String currentUid = user.getUid();

    public PointAdpater(ArrayList<Point> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PointAdpater.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pointlist,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PointAdpater.CustomViewHolder holder, final int position) {

        database = FirebaseDatabase.getInstance();
        databaseReference_User = database.getReference("User");

        databaseReference_User.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    key = child.getKey();
                }
                currentNickname = snapshot.child(key).child("nickName").getValue().toString();
        if (arrayList.get(position).getPointway().equals("recharge")) {
            holder.tv_pointWay.setText("포인트 충전");
            holder.tv_pointWay.setTextColor(Color.parseColor("#FF0000"));
            holder.tv_pointPrice.setText("+"+arrayList.get(position).getChangepoint());
            holder.tv_pointPayer.setVisibility(View.INVISIBLE);
        }
        else if(arrayList.get(position).getPointway().equals("depoist")) {
            holder.tv_pointWay.setText("포인트 입금");
            holder.tv_pointWay.setTextColor(Color.parseColor("#1838EC"));
            holder.tv_pointPrice.setText("+"+arrayList.get(position).getChangepoint());
        }
        else if(arrayList.get(position).getPointway().equals("send")){
            holder.tv_pointWay.setText("포인트 송금");
            holder.tv_pointWay.setTextColor(Color.parseColor("#1838EC"));
            holder.tv_pointPrice.setText("-"+arrayList.get(position).getChangepoint());
        }
        else if(arrayList.get(position).getPointway().equals("exchange")){
            holder.tv_pointWay.setText("포인트 환전");
            holder.tv_pointWay.setTextColor(Color.parseColor("#FF0000"));
            holder.tv_pointPrice.setText("-"+arrayList.get(position).getChangepoint());
        }

        // 입금받을때
        if (arrayList.get(position).getDepoister().equals("")) {
            holder.tv_pointPayer.setVisibility(View.INVISIBLE);
        }else if (arrayList.get(position).getDepoister().equals(currentNickname)){
            holder.tv_pointWay.setText("포인트 입금");
            holder.tv_pointWay.setTextColor(Color.parseColor("#1838EC"));
            holder.tv_pointPrice.setText("+"+arrayList.get(position).getChangepoint());
            holder.tv_pointPayer.setVisibility(View.VISIBLE);
            holder.tv_pointPayer.setText(arrayList.get(position).getSender()+"님이 입금");
        }

        // 내가 송금할때
        if (arrayList.get(position).getSender().equals("")) {
            holder.tv_pointPayer.setVisibility(View.INVISIBLE);
        }else if (arrayList.get(position).getSender().equals(currentNickname)){
            holder.tv_pointPayer.setVisibility(View.VISIBLE);
            holder.tv_pointPayer.setText(arrayList.get(position).getDepoister()+"에게 송금");
        }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_pointWay,tv_pointPrice,tv_pointPayer;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_pointWay = itemView.findViewById(R.id.tv_pointWay);
            this.tv_pointPayer = itemView.findViewById(R.id.tv_pointPayer);
            this.tv_pointPrice = itemView.findViewById(R.id.tv_pointPrice);
        }
    }
}















