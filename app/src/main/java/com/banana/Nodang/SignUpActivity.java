package com.banana.Nodang;

import static com.banana.Nodang.Utils.LogDisplay.setLog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextHeight;
    private Button buttonJoin;

    private RadioButton genderWoman, genderMan;
    private RadioButton moveLow, moveMid, moveHigh;
    private DatabaseReference mDatabase;
    private EditText editWeight, editGoal;
    private static final String TAG = "EmailPassword";

    private String getUserGender, getUserMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(); //DatabaseReference의 인스턴스

        editTextEmail = (EditText) findViewById(R.id.editText_email);
        editTextPassword = (EditText) findViewById(R.id.editText_passWord);
        editTextName = (EditText) findViewById(R.id.editText_name);
        genderWoman = (RadioButton) findViewById(R.id.gender_woman);
        genderMan = (RadioButton) findViewById(R.id.gender_man);
        editTextHeight = (EditText) findViewById(R.id.editText_Height);
        editWeight = (EditText) findViewById(R.id.editText_weight);
        editGoal = (EditText) findViewById(R.id.editText_goal);
        moveLow = (RadioButton) findViewById(R.id.move_low);
        moveMid = (RadioButton) findViewById(R.id.move_middle);
        moveHigh = (RadioButton) findViewById(R.id.move_high);

        buttonJoin = (Button) findViewById(R.id.btn_join);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                    // 이메일과 비밀번호가 공백이 아닌 경우
                    String getUserEmail = editTextEmail.getText().toString();
                    String getUserPassword = editTextPassword.getText().toString();
                    String getUserName = editTextName.getText().toString();
                    String getUserHeight = editTextHeight.getText().toString();

                    if(genderWoman.isChecked()) {
                        getUserGender = "woman";
                    }else if(genderMan.isChecked()){
                        getUserGender = "man";
                    }

                    if(moveLow.isChecked()){
                        getUserMove = "low";
                    }else if(moveMid.isChecked()){
                        getUserMove = "mid";
                    }else if(moveHigh.isChecked()){
                        getUserMove = "high";
                    }
//                    RadioGroup move = (RadioGroup) findViewById(R.id.moveGroup);
//                    move.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
//                    {
//
//                        public void onCheckedChanged(RadioGroup group, int checkedId) {
//                            if(checkedId == R.id.move_low) {
//                                getUserMove = moveLow.getText().toString();
//                            }else if(checkedId == R.id.move_middle) {
//                                getUserMove = moveMid.getText().toString();
//                            }else if(checkedId == R.id.move_high) {
//                                getUserMove = moveHigh.getText().toString();
//                            }
//                        }
//                    });
//                    String getUserGender;
//                    if(!genderWomen.getText().toString().isEmpty()){
//                        getUserGender = genderWomen.getText().toString();
//                    }else{
//                        getUserGender = genderMen.getText().toString();
//                    }
                    String getUserWeight = editWeight.getText().toString();
                    String getUserGoal = editGoal.getText().toString();

                    //createAccount(getUserEmail, getUserPassword);

                    register(getUserEmail, getUserPassword, getUserName, getUserGender, getUserHeight, getUserWeight, getUserGoal, getUserMove);
                } else {
                    // 이메일과 비밀번호가 공백인 경우
                    Toast.makeText(SignUpActivity.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if(currentUser != null){
//            reload();
//        }
//    }
//    private void createAccount(String email, String password){
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG,"createUserWithEmail:success");
//                            FirebaseUser user = firebaseAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//    }

    private void createUser(String email, String name, String gender,String height, String weight, String goal,String move){
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        double stdWeight = (Double.parseDouble(height) - 100) * 0.9;
        setLog("표준체중" + stdWeight);
        double dailyKcal = 0;
        if(move.equals("low")){
            dailyKcal = stdWeight * 25;
        }else if(move.equals("mid")){
            dailyKcal = stdWeight * 30;
        }else if(move.equals("high")) {
            dailyKcal = stdWeight * 40;
            setLog("일일 칼로리"+dailyKcal);
        }

        User user = new User(email, name, gender, height, weight, goal, move, dailyKcal);
        //데이터 저장
        mDatabase.child("users").child(fuser.getUid()).child("info").setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() { //데이터베이스에 넘어간 이후 처리
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"사용자 저장을 완료했습니다", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"사용자 저장에 실패했습니다" , Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void register(String email, String password ,String name, String gender,String height, String weight, String goal,String move) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공시
                            Toast.makeText(SignUpActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            createUser(email, name, gender,height,weight, goal, move);
                            updateUI(user);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "계정 생성 실패", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }



}