package com.example.byebuy.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.example.byebuy.ChatModel;
import com.example.byebuy.MessageActivity;
import com.example.byebuy.R;
import com.example.byebuy.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TreeMap;

public class ChatFragment extends Fragment {

    //다혜
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;



    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm");
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.chat_fragment_recyclerView);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        return view;
    }
    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<ChatModel> chatModels = new ArrayList<>();
        private String Uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public ChatRecyclerViewAdapter() {
            Uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            FirebaseDatabase.getInstance().getReference().child("ChatRooms").orderByChild("User/"+Uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatModels.clear();
                    for(DataSnapshot item : snapshot.getChildren()){
                        chatModels.add(item.getValue(ChatModel.class));
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);

            return new CustomViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final CustomViewHolder customViewHolder = ((CustomViewHolder)holder);
            String destinationUID = null;
            for(String user: chatModels.get(position).User.keySet()){
                if(!user.equals(Uid)){
                    destinationUID = user;
                    destinationUsers.add(destinationUID);
                }
            }
            assert destinationUID != null;
            FirebaseDatabase.getInstance().getReference().child("User").child(destinationUID).addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final User User = snapshot.getValue(User.class);
                        assert User != null;
                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                        StorageReference storageReference = storage.getReference();
                        String path = (String) User.getPhotoUrl();
                        if (path.equals("default")) {
                            Glide.with(customViewHolder.itemView.getContext())
                                    .load(R.drawable.logo_main)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(customViewHolder.imageView);
                            customViewHolder.textView_title.setText(User.getNickName());
                        }else{
                            storageReference.child("myprofile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(customViewHolder.itemView.getContext())
                                        .load(uri)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(customViewHolder.imageView);
                                customViewHolder.textView_title.setText(User.getNickName());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                               // Toast.makeText(ChatList.class, "실패", Toast.LENGTH_SHORT).show();
                                Glide.with(customViewHolder.itemView.getContext())
                                        .load(R.drawable.logo_main)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(customViewHolder.imageView);
                            }
                        });

                        }

                    }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            if(commentMap.keySet().toArray().length> 0){
            String LastMessageKey = (String) commentMap.keySet().toArray()[0];
            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println(chatModels.get(position).comments.get(LastMessageKey).readUsers.containsKey(UID));
            if((chatModels.get(position).comments.get(LastMessageKey).readUsers.containsKey(UID)) == false){
                customViewHolder.noticeimageView.setVisibility(View.VISIBLE);
                }else{
                customViewHolder.noticeimageView.setVisibility(View.INVISIBLE);
            }
            customViewHolder.textView_Last_Message.setText(Objects.requireNonNull(chatModels.get(position).comments.get(LastMessageKey)).message);

            simpleDateFormat.setTimeZone(SimpleTimeZone.getTimeZone("Asia/Seoul"));
            long unixTime = (long) Objects.requireNonNull(chatModels.get(position).comments.get(LastMessageKey)).timeStamp;
            Date date = new Date(unixTime);
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUID",destinationUsers.get(position));
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.fromtoleft);

                    startActivity(intent,activityOptions.toBundle());
                }
            });


        }}

        @Override
        public int getItemCount() {
            return chatModels.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_Last_Message;
            public TextView textView_timestamp;
            public ImageView noticeimageView;
            public CustomViewHolder(@NonNull View view) {
                super(view);

                imageView =view.findViewById(R.id.chatItem_imageView);
                textView_title=view.findViewById(R.id.chatItem_textView_title);
                textView_Last_Message = view.findViewById(R.id.chatItem_textView_LastMessage);
                textView_timestamp = view.findViewById(R.id.chatItem_textView_timestamp);
                noticeimageView = view.findViewById(R.id.chatItem_notice_imageview);
                noticeimageView.setVisibility(View.INVISIBLE);



            }
        }
    }

}
