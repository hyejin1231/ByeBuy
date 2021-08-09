package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.byebuy.fragment.ChatFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MessageActivity extends AppCompatActivity {
    private String destinationUID;
    private String Uid;
    private String ChatRoomUid;
    private EditText editText;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    int peopleCount = 0;
    private User user;
    ChatFragment chatFragment;
    @SuppressLint("SimpleDateFormat")

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        Uid = user.getUid();
        destinationUID = getIntent().getStringExtra("destinationUID");
        final Button Button = findViewById(R.id.messageActivity_button);
        editText = findViewById(R.id.messageActivity_editText);
        recyclerView = findViewById(R.id.messageActivity_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CheckChatRoom();
        Button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.User.put(Uid, true);
                chatModel.User.put(destinationUID, true);
                //                Toast.makeText(MessageActivity.this,nullTextMeee,Toast.LENGTH_LONG).show();
                if (ChatRoomUid == null) {
                    //                    Toast.makeText(MessageActivity.this,nullTextMeeee,Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference().child("ChatRooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>(){
                        @Override
                        public void onSuccess(Void aVoid) {
                            CheckChatRoom();


                        }
                    });

                } else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.Uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    comment.message = editText.getText().toString();
                    comment.timeStamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(ChatRoomUid).child("comments").push().setValue(comment)
                            .addOnCompleteListener(new OnCompleteListener<Void>(){
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    editText.setText("");
                                }
                            });
//                    Toast.makeText(MessageActivity.this,notnullTextMessage,Toast.LENGTH_LONG).show();
                }

            }
        });
        CheckChatRoom();
    }

    private void CheckChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("ChatRooms").orderByChild("User/"+Uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if(chatModel.User.containsKey(destinationUID)){
                        ChatRoomUid = item.getKey();
                        recyclerView.setLayoutManager(new LinearLayoutManager((MessageActivity.this)));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                        //                        Toast.makeText(MessageActivity.this,nullTextMeee,Toast.LENGTH_LONG).show();
                    }//                        Toast.makeText(MessageActivity.this,"실패"+destinationUID,Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<ChatModel.Comment> comments;
        User User;
        public RecyclerViewAdapter(){
            comments = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("User").child(destinationUID)
                    .addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User = snapshot.getValue(User.class);
                            getMessageList();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



        }
        void getMessageList(){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(ChatRoomUid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear();
                    Map<String,Object> readUsersMap = new HashMap<>();
                    for(DataSnapshot item : snapshot.getChildren()){
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        assert comment_motify != null;
                        comment_motify.readUsers.put(Uid ,true);
                        readUsersMap.put(key,comment_motify);
                        comments.add(comment_origin);
                    }
                    if(comments.size() == 0){
                        return;
                    }
                    if(!comments.get(comments.size()-1).readUsers.containsKey(Uid)) {

                        FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(ChatRoomUid).child("comments").updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>(){
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size()-1);
                            }
                        });
                    }else {
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size()-1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MessageActivity.this, "저장 실패!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);


            return new MessageViewHolder(view);
        }

        @SuppressLint("RtlHardcoded")
        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            final MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);
            if(comments.get(position).Uid.equals(Uid)){
                               /* Glide.with(holder.itemView.getContext())
                        .load(User.getPhotoUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile); 이미지 생기면 받아올 수 있음*/
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.out_message_bg);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
//나
            }
            else{
                //상대방
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://byebuy-95a72.appspot.com");
                StorageReference storageReference = storage.getReference();
                String path = User.getPhotoUrl();
                if (path.equals("default")) {
                    Glide.with(holder.itemView.getContext())
                            .load(R.drawable.logo_main)
                            .apply(new RequestOptions().circleCrop())
                            .into(messageViewHolder.imageView_profile);
                }else{
                    storageReference.child("myprofile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(holder.itemView.getContext())
                                    .load(uri)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(messageViewHolder.imageView_profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Toast.makeText(ChatList.class, "실패", Toast.LENGTH_SHORT).show();
                            Glide.with(holder.itemView.getContext())
                                    .load(R.drawable.logo_main)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(messageViewHolder.imageView_profile);
                        }
                    });

                }
                messageViewHolder.textView_name.setText(User.getNickName());
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.in_message_bg);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_readCounter_Right);

            }
            messageViewHolder.textView_message.setTextSize(25);
            long unixTime = (long) comments.get(position).timeStamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_Timestamp.setText(time);

        }
        void setReadCounter(final int position, final TextView textView){
            if(peopleCount ==0) {
                FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(ChatRoomUid).child("User").addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) snapshot.getValue();
                        assert users != null;
                        peopleCount = users.size();
                        int count = peopleCount - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));


                        } else {
                            textView.setVisibility(View.INVISIBLE);
                            Bundle bundle = new Bundle();

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else{
                int count = peopleCount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));

                } else {
                    textView.setVisibility(View.INVISIBLE);
                }

            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder{
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_main;
            public LinearLayout linearLayout_destination;
            public TextView textView_Timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_Right;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = view.findViewById(R.id.messageItem_textView_message);

                textView_name = view.findViewById(R.id.messageItem_textView_name);
                imageView_profile = view.findViewById(R.id.messageItem_imageView_profile);
                linearLayout_destination = view.findViewById(R.id.messageItem_linear_Layout_destination);
                linearLayout_main = view.findViewById(R.id.messageItem_linear_Layout_main);
                textView_Timestamp = view.findViewById(R.id.messageItem_textView_timeStamp);
                textView_readCounter_left = view.findViewById(R.id.messageItem_textView_readCounter_left);
                textView_readCounter_Right = view.findViewById(R.id.messageItem_textView_readCounter_right);
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.fromtoright);

    }
}