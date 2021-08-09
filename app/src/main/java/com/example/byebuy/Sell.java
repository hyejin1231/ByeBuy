package com.example.byebuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Sell extends AppCompatActivity {

    TextView tv_writeToday,tv_writeDeadline;
    Button btn_register,btn_gallery;
    EditText edit_price, edit_title, edit_detail;
    ImageView img_writeImage,img_btn_sellBack;
    Spinner spinner, placeSpinner ;
    Uri uri;
    Bitmap img;
    String uids;
    String filename;
    String category;
    String place;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private Uri filePath;

    private static final String TAG = "SellPage";

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String currentUid = user.getUid();

    Random random = new Random();
    long now = System.currentTimeMillis();
    Date today = new Date(now);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat format = new SimpleDateFormat("yyyy");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd");
    String date = simpleDateFormat.format(today);
    int year = Integer.parseInt(format.format(today));
    int month  = Integer.parseInt(dateFormat.format(today));
    int day = Integer.parseInt(dateFormat2.format(today));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        img_btn_sellBack = findViewById(R.id.img_btn_sellBack);
        tv_writeToday = findViewById(R.id.tv_writeToday);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_register = findViewById(R.id.btn_register);
        edit_detail = findViewById(R.id.edit_detail);
        edit_title = findViewById(R.id.edit_ntTtile);
        img_writeImage = findViewById(R.id.img_writeImage);
        edit_price = findViewById(R.id.edit_price);
        spinner = findViewById(R.id.spinner);
        placeSpinner = findViewById(R.id.placeSpinner);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Product"); // DB 테이블 연동

        tv_writeToday.setText(date);

        Intent intent = getIntent();
        uids = intent.getStringExtra("uid");


        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });

        final String[] ItemCategory = {"카테고리","디지털/가전", "가구/인테리어", "생활/가공식품", "여성의류","여성잡화","유아용/아동도서", "남성패션/잡화",
                "게임/취미", "뷰티/미용","반려동물용품","도서/티켓/음반", "기타중고물품"};

        final String[] StationPlace ={"지하철역","가천대역"};

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ItemCategory);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, StationPlace);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeSpinner.setAdapter(arrayAdapter2);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = ItemCategory[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        placeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                place = StationPlace[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        img_btn_sellBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        // register 버튼을 누르면 파이어베이스에 데이터 저장 가능??!!ㅠㅠ 제발..
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = edit_title.getText().toString();
                String detail = edit_detail.getText().toString();
                String price = edit_price.getText().toString();
                int count = 0;
                String status = "selling";


                String unique = "";

                if (title.equals("")) {
                    Toast.makeText(getApplicationContext(), "제목을 꼭 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (detail.equals("")){
                    Toast.makeText(getApplicationContext(), "상세설명을 꼭 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(price.equals("")) {
                    Toast.makeText(getApplicationContext(), "가격을 꼭 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(category.equals("카테고리")) {
                    Toast.makeText(getApplicationContext(), "카테고리를 꼭 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else if(place.equals("지하철역")) {
                    Toast.makeText(getApplicationContext(), "보관함 위치를 꼭 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else if(filePath == null) {
                    Toast.makeText(getApplicationContext(), "이미지를 꼭 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (int i = 0; i < 10; i++) {
                        unique += String.valueOf((char) ((int) (random.nextInt(26)) + 97));
                    }
                    if (filePath != null) {

                        //storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        //Unique한 파일명을 만들자.
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
                        Date now = new Date();
                        filename = formatter.format(now) + ".png";
                        //storage 주소와 폴더 파일명을 지정해 준다.
//                    StorageReference storageRef = storage.getReferenceFromUrl("gs://teamtest1-6b76d.appspot.com").child("images/" + filename);
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://byebuy-95a72.appspot.com").child(filename);
                        //올라가거라...
                        storageRef.putFile(filePath)
                                //성공시
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                                        Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                //실패시
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                });
//            진행중
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
//                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
//                            //dialog에 진행률을 퍼센트로 출력해 준다
//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
//                        }
//                    });
                    } else {
                        Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
                    }

                    //String title, String detail, String price, String bid, String image
//                String image =  "images/" + filename;
                    String image = filename;
                    String estiStatus = "yet";
                    String placestatus = "x";

                    Product product = new Product(title, detail, price, image, count, unique, date, currentUid, status, estiStatus, category,place,placestatus);
                    databaseReference.push().setValue(product);

                    Toast.makeText(getApplicationContext(), "등록 완료", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                try {
                    filePath = data.getData();
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    img = BitmapFactory.decodeStream(in);
//                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    img_writeImage.setImageBitmap(img);

                    uri = data.getData();


                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}




















