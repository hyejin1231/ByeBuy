package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModifyMyInfo extends AppCompatActivity {

    private FirebaseAuth Auth;

    TextView tv_modify_showemail;
    EditText et_modify_pw,et_modify_nickname,et_modify_pw_check;
    String key1,key2,key3,key4,key5,key6,key7;
    //String uids;
    Button btn_modify,btn_modify_default;
    ImageView iv_modify_profile,iv_btn_back;
    private Button btn_profilepic;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Uri filePath2;
    Uri uri;
    Bitmap img;
    String filename2;
    String key;
    boolean defaultornot = false;

    int checknum =1;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_my_info);

        tv_modify_showemail = findViewById(R.id.tv_modify_showemail);
        et_modify_pw = findViewById(R.id.et_modify_pw);
        et_modify_nickname = findViewById(R.id.et_modify_nickname);
        btn_modify = findViewById(R.id.btn_modify);
        et_modify_pw_check = findViewById(R.id.et_modify_pw_check);
        btn_profilepic = findViewById(R.id.btn_profilepic);
        iv_modify_profile = findViewById(R.id.iv_modify_profile);
        btn_modify_default = findViewById(R.id.btn_modify_default);
        iv_btn_back = findViewById(R.id.iv_btn_back);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("User"); // DB 테이블 연동

        iv_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Mypage.class);
                startActivity(intent);
            }
        });



        //기본이미지로 변경
        btn_modify_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_modify_profile.setImageResource(R.drawable.logo_main);

                filename2 = "default";
                defaultornot = true;
            }
        });

        //이미지뷰에 선택된 이미지 로딩시키는 코드임
        databaseReference.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();

                }

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://teamtest1-6b76d.appspot.com");
                StorageReference storageReference = storage.getReference();
                String path = snapshot.child(key).child("photoUrl").getValue().toString();

                if (path.equals("default")) {
                    filePath2=null;
                    Glide.with(ModifyMyInfo.this)
                            .load(R.drawable.logo_main)
                            .into(iv_modify_profile);
                } else {
                    storageReference.child("myprofile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(ModifyMyInfo.this)
                                    .load(uri)
                                    .into(iv_modify_profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ModifyMyInfo.this, "이미지 로딩실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key1 = child.getKey();
                }
                tv_modify_showemail.setText(snapshot.child(key1).child("id").getValue().toString());
                et_modify_pw.setText(snapshot.child(key1).child("pw").getValue().toString());
                et_modify_nickname.setText(snapshot.child(key1).child("nickName").getValue().toString());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //정보수정 버튼
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String password = et_modify_pw.getText().toString();
                final String passwordCheck = et_modify_pw_check.getText().toString();
                final String nickName = et_modify_nickname.getText().toString();
                //final FirebaseUser user = Auth.getCurrentUser();


                //1.비번체크
                databaseReference.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            key2 = child.getKey();
                        }

                        if (password.equals(passwordCheck)) { //auth쪽

                            //2.닉네임체크
                            databaseReference.orderByChild("uid").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    checknum =1;
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        key3 = child.getKey();
                                        key4 = snapshot.child(key3).child("nickName").getValue().toString();

                                        //초기 checknum은 1이고 하나라도 중복이면 0을 곱해서 결과가 0이 나오게....
                                        if (nickName.equals(key4) && !(snapshot.child(key3).child("uid").getValue().toString()).equals(currentUid)) {
                                            checknum *= 0;
                                        } else {
                                            checknum *= 1;

                                        }
                                    }
                                    if (checknum==0 ) {
                                        Toast.makeText(ModifyMyInfo.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();

                                    } else if(checknum==1) { //모든게 적절하게 입력됐을 경우..
                                        databaseReference.orderByChild("uid").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot child : snapshot.getChildren()) {
                                                    key7 = child.getKey();
                                                }

                                                if (filePath2 != null) { //filepath가 null이라는건 파일 선택을 안했다는거임...
                                                    final FirebaseStorage storage = FirebaseStorage.getInstance();
                                                    StorageReference storageRef = storage.getReferenceFromUrl("gs://byebuy-95a72.appspot.com").child("myprofile").child(filename2);
                                                    storageRef.putFile(filePath2)
                                                            //성공시
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    if (defaultornot == true) {
                                                                        databaseReference.child(key7).child("photoUrl").setValue("default");
                                                                        Toast.makeText(getApplicationContext(), "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                                                        databaseReference.child(key7).child("nickName").setValue(nickName);

                                                                        databaseReference.child(key2).child("pw").setValue(password); //디비안의 비번 바꿔줌
                                                                        user.updatePassword(password); //파베권한의 비번 바꿔줌

                                                                        Intent intent = new Intent(getApplicationContext(), Mypage.class);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        databaseReference.child(key7).child("photoUrl").setValue(filename2);
                                                                        Toast.makeText(getApplicationContext(), "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                                                        databaseReference.child(key7).child("nickName").setValue(nickName);

                                                                        databaseReference.child(key2).child("pw").setValue(password); //디비안의 비번 바꿔줌
                                                                        user.updatePassword(password); //파베권한의 비번 바꿔줌

                                                                        Intent intent = new Intent(getApplicationContext(), Mypage.class);
                                                                        startActivity(intent);
                                                                    }

                                                                }
                                                            })
                                                            //실패시
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                } else {//프사를 안바꾸고 싶을 때

                                                    if (defaultornot == true) {
                                                        Toast.makeText(getApplicationContext(), "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                                        databaseReference.child(key7).child("photoUrl").setValue("default");
                                                        databaseReference.child(key7).child("nickName").setValue(nickName);

                                                        databaseReference.child(key2).child("pw").setValue(password); //디비안의 비번 바꿔줌
                                                        user.updatePassword(password); //파베권한의 비번 바꿔줌

                                                        Intent intent = new Intent(getApplicationContext(), Mypage.class);
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                                        databaseReference.child(key7).child("nickName").setValue(nickName);

                                                        databaseReference.child(key2).child("pw").setValue(password); //디비안의 비번 바꿔줌
                                                        user.updatePassword(password); //파베권한의 비번 바꿔줌

                                                        Intent intent = new Intent(getApplicationContext(), Mypage.class);
                                                        startActivity(intent);
                                                    }


                                                }
//
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(getApplicationContext(), "회원정보 수정 실패", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "회원정보 수정 실패", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }else {
                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "회원정보 수정 실패", Toast.LENGTH_SHORT).show();
                    }


                });//레퍼런스




            }
        });//정보수정 버튼

        //여기부터 다혜
        //권한체크 후 권한요청
        btn_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
                Date now = new Date();
                filename2 = currentUid + formatter.format(now) + ".png";

            }
        });

    }

    //여기도 다혜
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        filePath2 = null;
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    filePath2 = data.getData();
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in2 = getContentResolver().openInputStream(filePath2);

                    img = BitmapFactory.decodeStream(in2);
//                    Bitmap img = BitmapFactory.decodeStream(in);
                    in2.close();
                    iv_modify_profile.setImageBitmap(img);
                    uri = data.getData();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }//1

    }

    // 이미지 경로 알아오는 함수..?
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "title", null);
//        String path = MediaStore.Images.Media.in
        return Uri.parse(path);
    }
}


























