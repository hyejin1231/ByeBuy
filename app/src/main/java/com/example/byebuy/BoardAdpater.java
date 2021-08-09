package com.example.byebuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BoardAdpater  extends RecyclerView.Adapter<BoardAdpater.CustomViewHolder>{
    private ArrayList<board> arrayList;
    private Context context;

    public BoardAdpater(ArrayList<board> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        //  각 공지 사항 아이템들에 대한 매칭

        holder.tv_ntTitle.setText(arrayList.get(position).getNt_title());
        holder.tv_ntContent.setText(arrayList.get(position).getNt_content());
        holder.tv_ntDate.setText(arrayList.get(position).getNt_date());
        holder.tv_ntCategory.setText(arrayList.get(position).getNt_category());

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0 );
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ntTitle, tv_ntDate, tv_ntContent, tv_ntCategory;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_ntTitle = itemView.findViewById(R.id.tv_ntTitle);
            this.tv_ntContent = itemView.findViewById(R.id.tv_ntContent);
            this.tv_ntDate = itemView.findViewById(R.id.tv_ntDate);
            this.tv_ntCategory = itemView.findViewById(R.id.tv_ntcategory);
        }
    }
}
