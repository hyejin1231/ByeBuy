package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth Auth; //파이어베이스에 대한 권한 가져오기

    private EditText et_email;
    private EditText et_pw;
    private EditText et_name;
    private EditText et_age;
    private EditText et_pw_check;
    private Button btn_signup;
    private EditText et_nickname;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private static final String TAG = "SignActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_email = findViewById(R.id.et_email);
        et_pw = findViewById(R.id.et_pw);
        et_name = findViewById(R.id.name);
        btn_signup = findViewById(R.id.btn_signup);
        et_pw_check = findViewById(R.id.et_pw_check);
        et_nickname = findViewById(R.id.et_nickname);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("User"); // DB 테이블 연동
        Auth = FirebaseAuth.getInstance();

        //회원가입 버튼을 누르면
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_signup:
                        Log.e("클릭", "클릭");
                        signUp();
                        break;
                }
            }
        });
    }

    private void signUp() {
        final String email = et_email.getText().toString();
        final String password = et_pw.getText().toString();
        final String passwordCheck = et_pw_check.getText().toString();
        final String nickName = et_nickname.getText().toString();
        //final FirebaseUser user = Auth.getCurrentUser();

        databaseReference.orderByChild("nickName").equalTo(nickName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final String warn = "";
                // final String pw = password;
                final int estimate = 50;
                final int estimateUser = 1;

                String returnkey1 ="";
                String returnkey2 ="";
                String returnkey3 ="";
                String returnkey4 ="";
                String returnkey5 ="";



                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();
                    String key2 = (snapshot.child(key).child("nickName").getValue()).toString();

                    if(nickName.equals(key2)){
//                        Toast.makeText(SignUp.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                        returnkey1 = "no";
                    }else{
                        returnkey1 = "yes";
                    }


                }//for문

                //자꾸 회원가입할 때 5개씩 DB에 들어가서..(도대체 왜?)
                //returnkey로 값 받아와서 판단 후 DB에 넣어주는 방식으로 수정
                //어떤 에러도 발생하지 않으면...

                //왜 안들어가냐 진짜 빡치게
                //들어간당 ㅎㅎ
                if(returnkey1.equals("no")){
                    Toast.makeText(SignUp.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                }else if(password.length()<5){
                    Toast.makeText( SignUp.this, "비밀번호를 4자 이상으로 설정해주세요.", Toast.LENGTH_SHORT).show();
                }else if(!(password.equals(passwordCheck))){
                    Toast.makeText(SignUp.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                                final FirebaseUser user = Auth.getCurrentUser();
                                final String PhotoUrl = "default";
                                final int point = 0;
                                final String my_uid = user.getUid(); // uid 가져와서 user db에 저장
                                User user11 = new User(PhotoUrl, email, nickName,my_uid,warn,estimate,estimateUser,password,point);
                                databaseReference.child(my_uid).setValue(user11);
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);

                            }else if(email.equals("")){
                                Toast.makeText(SignUp.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            }else {
                                Toast.makeText(SignUp.this, "회원가입 실패.", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }

            }//dataonchange
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}