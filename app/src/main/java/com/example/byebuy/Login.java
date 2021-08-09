package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private SignInButton btn_google; //구글 로그인 버튼
    private FirebaseAuth auth; //파이어 베이스 인증 객체
    private GoogleApiClient googleApiClient; //구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE = 100; //구글 로그인 결과 코드

    private EditText et_email_login;
    private EditText et_pw_login;
    private Button btn_signup_go;
    private Button btn_login;

    String key;
    String my_uid;


    private FirebaseDatabase database;
    private DatabaseReference databaseReference,databaseReference2;
    final FirebaseUser Cuser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_signup_go = findViewById(R.id.btn_go_signup);
        btn_login = findViewById(R.id.btn_login);
        et_email_login = findViewById(R.id.et_email_login);
        et_pw_login = findViewById(R.id.et_pw_login);

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("User"); // DB 테이블 연동
        databaseReference2 = database.getReference("Product");

        //로그인버튼을 눌렀을때
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_email_login.getText().toString();
                String pw = et_pw_login.getText().toString();

                // 혜진 코드 삽입, 권 관리자 1:1 채팅 메뉴 추가로 인한 id변경
                if(id.equals("admin@buy.com") && pw.equals("123456") ) {
                    Toast.makeText(Login.this, "관리자 로그인", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), AdminMain.class);
                    startActivity(intent);
                }
                Login();



            }
        });


        //회원가입 버튼을 눌렀을 때
        btn_signup_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);

            }
        });


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() { //구글 로그인 버튼을 클릭했을 때 이곳을 수행
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);


            }
        });

    } // end oncreate

    private void Login() {
        final String email = et_email_login.getText().toString();
        final String password = et_pw_login.getText().toString();

        if (email.equals("")) {
            Toast.makeText(Login.this, "이메일을 반드시 입력해주세요.", Toast.LENGTH_SHORT).show();
            //Toast.makeText(Login2.this, "로그인 실패", Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(Login.this, "비밀번호를 반드시 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        /*intent.putExtra("uid", Cuser.getUid());*/
                        startActivity(intent);

                    } else {
                        Toast.makeText(Login.this, "올바른 회원정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){ //인증결과가 성공적이면...
                GoogleSignInAccount account= result.getSignInAccount(); //account 라는 데이터는 구글 로그인 정보를 담고있습니다. (닉네임, 프로필 사진, url, 이메일주소 등...)
                resultLogin(account); //로그인 결과값 출력 수행하라는 메소드


            }
        }

    }

    private void resultLogin(final GoogleSignInAccount account) {
//        final FirebaseUser Cuser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) { //로그인이 성공했으면...
                            Toast.makeText(Login.this, "로그인 성공",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            // 혜진 코드 삽입
                            String checkId = account.getEmail();

                            databaseReference.orderByChild("id").equalTo(checkId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        key = child.getKey();
                                    }

                                    String myid = account.getEmail();
                                    String photoUrl = "default";
//                                    String photoUrl = String.valueOf(account.getPhotoUrl());
                                    String nickName = account.getDisplayName();
                                    my_uid = Cuser.getUid();
                                    String warn = "";
                                    int estimate = 50;
                                    int estimateUser = 1;
                                    int point = 0;
                                    String pw = "";
                                    if (key == null) {
                                        User user = new User(photoUrl, myid, nickName,my_uid,warn, estimate,estimateUser,pw,point);

                                        databaseReference.child(my_uid).setValue(user);

                                        Toast.makeText(getApplicationContext(),"user 등록 완료", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

//                            intent.putExtra("uid",Cuser.getUid());
//                            Intent intent1 = new Intent(getApplicationContext(), CustomAdapter.CustomViewHolder.class);
//                            intent1.putExtra("uid", Cuser.getUid());
                            startActivity(intent);


                            //로그인하면 모든 디비에 있는 물품들의 date값이 오늘로 바뀐다...
                            databaseReference2.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        String key = child.getKey();
                                        //String key3 = snapshot.getRef().child(key).child("nickName").toString();

                                        long now = System.currentTimeMillis();
                                        Date today = new Date(now);
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        String date = simpleDateFormat.format(today);
                                        snapshot.getRef().child(key).child("date").setValue(date);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        }else{ //로그인이 실패했으면...
                            Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }





    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

































