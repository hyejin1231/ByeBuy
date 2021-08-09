package com.example.byebuy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminNoticeAdpater extends RecyclerView.Adapter<AdminNoticeAdpater.CustomViewHolder> {

    private ArrayList<board> arrayList;
    private Context context;

    public AdminNoticeAdpater(ArrayList<board> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adnotice_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.tv_ntTitle.setText(arrayList.get(position).getNt_title());
        holder.tv_ntContent.setText(arrayList.get(position).getNt_content());
        holder.tv_ntDate.setText(arrayList.get(position).getNt_date());
        holder.tv_ntCategory.setText(arrayList.get(position).getNt_category());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ntTitle, tv_ntDate, tv_ntContent, tv_ntCategory;


        public CustomViewHolder(@NonNull final View itemView) {
            super(itemView);


            this.tv_ntTitle = itemView.findViewById(R.id.tv_ntTitle);
            this.tv_ntContent = itemView.findViewById(R.id.tv_ntContent);
            this.tv_ntDate = itemView.findViewById(R.id.tv_ntDate);
            this.tv_ntCategory = itemView.findViewById(R.id.tv_ntcategory);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    Intent intent = new Intent(view.getContext(), SubAdNotice.class);

                    intent.putExtra("adTitle", arrayList.get(position).getNt_title());
                    intent.putExtra("adDate", arrayList.get(position).getNt_date());
                    intent.putExtra("adContent", arrayList.get(position).getNt_content());
                    intent.putExtra("adCategory", arrayList.get(position).getNt_category());

                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
