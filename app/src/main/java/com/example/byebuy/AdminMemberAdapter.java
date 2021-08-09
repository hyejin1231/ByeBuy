package com.example.byebuy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminMemberAdapter  extends RecyclerView.Adapter<AdminMemberAdapter.CustomViewHolder>{

    private ArrayList<User> arrayList;
    private Context context;
    String aaa;
    String uid, uid1;
    String id;
    String key,key1;
    int WarnCount = 0;
    int warn;

    String userUid;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public AdminMemberAdapter(ArrayList<User> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminMemberAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admember_item,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdminMemberAdapter.CustomViewHolder holder, int position) {

        String abcd = arrayList.get(position).getId();

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
        final StorageReference storageReference = storage.getReference();

//        Glide.with(holder.itemView).load(arrayList.get(position).getPhotoUrl()).into(holder.img_admemImg); //프로필 uri를 이미지 뷰에 세팅
        holder.tv_admemuid.setText(arrayList.get(position).getUid());
        holder.tv_admemid.setText(arrayList.get(position).getId());
//        Toast.makeText(context, arrayList.get(position).getUid(),Toast.LENGTH_SHORT).show();

        uid1 = arrayList.get(position).getUid();
        databaseReference.orderByChild("uid").equalTo(uid1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key1 = child.getKey();
                }
                String AdWarnContent = snapshot.child(key1).child("warn").getValue().toString();

                String path = (String) snapshot.child(key1).child("photoUrl").getValue();

                if (path.equals("default")) {
                    Glide.with(holder.itemView)
                            .load(R.drawable.logo_main)
                            .into(holder.img_admemImg);

                }
                else {
                    storageReference.child("myprofile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(holder.itemView)
                                    .load(uri)
                                    .into(holder.img_admemImg);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
//                                    Glide.with(MyPage.this)
//                                            .load(e)
//                                            .into(iv_profile);
                        }
                    });

                }

                if(snapshot.child(key1).child("warn").getValue().toString().isEmpty()) {
                    holder.tv_AdMessage.setVisibility(View.INVISIBLE);
                    holder.img_AdMessage.setVisibility(View.INVISIBLE);
                } else if(snapshot.child(key1).child("warn").getValue().toString().equals("경고5회")){
                    holder.tv_AdMessage.setText(AdWarnContent);
                    holder.tv_AdMessage.setTextColor(Color.parseColor("#EC1313"));
                    holder.tv_AdMessage.setVisibility(View.VISIBLE);
                    holder.img_AdMessage.setVisibility(View.VISIBLE);
                    holder.btn_adMemDrop.setTextColor(Color.parseColor("#EC1313"));
                } else {
                    holder.tv_AdMessage.setText(AdWarnContent);
                    holder.tv_AdMessage.setTextColor(Color.parseColor("#1838EC"));
                    holder.tv_AdMessage.setVisibility(View.VISIBLE);
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

        ImageView img_AdMessage;
        TextView tv_AdMessage;
        ImageView img_admemImg;
        TextView tv_admemid, tv_admemuid;
        Button btn_adMemDrop, btn_adMemWarn;

        public CustomViewHolder(@NonNull final View itemView) {
            super(itemView);

            this.img_AdMessage = itemView.findViewById(R.id.img_AdMessage);
            this.tv_AdMessage = itemView.findViewById(R.id.tv_AdMessage);
            this.img_admemImg = itemView.findViewById(R.id.img_admemImg);
            this.tv_admemid = itemView.findViewById(R.id.tv_admemid);
            this.tv_admemuid = itemView.findViewById(R.id.tv_admemuid);
            this.btn_adMemDrop = itemView.findViewById(R.id.btn_adMemDrop);
            this.btn_adMemWarn = itemView.findViewById(R.id.btn_adMemWarn);

            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("User");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    userUid = arrayList.get(position).getUid();

                    Intent intent = new Intent(v.getContext(), AdminMemberSub.class);

                    intent.putExtra("uid", userUid);

                    v.getContext().startActivity(intent);
                }
            });


            btn_adMemDrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    uid = arrayList.get(position).getUid();

                    new AlertDialog.Builder(itemView.getContext()) // TestActivity 부분에는 현재 Activity의 이름 입력.
                            .setMessage("회원을 탈퇴시키겠습니까?")     // 제목 부분 (직접 작성)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                                public void onClick(DialogInterface dialog, int which) {

                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            snapshot.getRef().child(uid).removeValue();
                                            Toast.makeText(context, "해당 사용자가 탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
//                                    Intent intent = new Intent(itemView.getContext(), MainActivity.class);
//                                    itemView.getContext().startActivity(intent);
                                    Toast.makeText(itemView.getContext(), "확인 누름", Toast.LENGTH_SHORT).show(); // 실행할 코드
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {     // 버튼2 (직접 작성)
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(itemView.getContext(), "취소 누름", Toast.LENGTH_SHORT).show(); // 실행할 코드
                                }
                            })
                            .show();

                }


            });

            btn_adMemWarn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    uid = arrayList.get(position).getUid();
                    id = arrayList.get(position).getId();

                    new AlertDialog.Builder(itemView.getContext()) // TestActivity 부분에는 현재 Activity의 이름 입력.
                            .setMessage(id + "회원에게 경고 1회 적용하겠습니까?")     // 제목 부분 (직접 작성)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                                public void onClick(DialogInterface dialog, int which) {

                                    databaseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                key = child.getKey();
                                            }

                                            String warnString = snapshot.child(key).child("warn").getValue().toString().replace("경고","").replace("회","");
                                            if (warnString.equals("")) {
                                                warn = 0;
                                            }else {
                                                warn = Integer.parseInt(warnString);
                                            }
                                            warn++;
                                            snapshot.getRef().child(uid).child("warn").setValue("경고"+warn + "회");
                                            Toast.makeText(context, id+"회원에게 경고1회 적용되었습니다.", Toast.LENGTH_SHORT).show();

//                                            warncontent =  arrayList.get(position).getWarn();
//                                            Toast.makeText(context, warncontent, Toast.LENGTH_SHORT).show();


                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    String warncontent =  arrayList.get(position).getWarn().replace("경고", "").replace("회","");
                                    int warnint;
                                    if (warncontent.equals("")) {
                                        warnint = 0;
                                    }else  {
                                        warnint = Integer.parseInt(warncontent);
                                    }

                                    warnint++;
                                    String insertWarn = "경고" + warnint + "회";

//                                    Toast.makeText(context, warncontent, Toast.LENGTH_SHORT).show();
                                    if(insertWarn.equals("경고5회")){
                                        tv_AdMessage.setText(insertWarn);
                                        tv_AdMessage.setTextColor(Color.parseColor("#EC1313"));
                                        tv_AdMessage.setVisibility(View.VISIBLE);
                                        img_AdMessage.setVisibility(View.VISIBLE);
                                        btn_adMemDrop.setTextColor(Color.parseColor("#EC1313"));
                                    }
                                    else {
                                        tv_AdMessage.setText(insertWarn);
                                        tv_AdMessage.setTextColor(Color.parseColor("#1838EC"));
                                        tv_AdMessage.setVisibility(View.VISIBLE);
                                    }

//                                    Intent intent = new Intent(itemView.getContext(), MainActivity.class);
//                                    itemView.getContext().startActivity(intent);
//                                    Toast.makeText(itemView.getContext(), "확인 누름", Toast.LENGTH_SHORT).show(); // 실행할 코드
                                }

                            })

                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {     // 버튼2 (직접 작성)
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(itemView.getContext(), "취소", Toast.LENGTH_SHORT).show(); // 실행할 코드
                                }
                            })
                            .show();
                }
            });

        }




    }
}
