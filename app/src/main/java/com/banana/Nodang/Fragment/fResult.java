package com.banana.Nodang.Fragment;

import static com.banana.Nodang.Utils.LogDisplay.setLog;
import static com.banana.Nodang.Utils.LogDisplay.setToast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.banana.Nodang.Pojo.ProductIngredientByProductName.MainI2790Response;
import com.banana.Nodang.Pojo.ProductNameByBarCode.MainC005Response;
import com.banana.Nodang.Pojo.ProductRawMaterials.MainC002Response;
import com.banana.Nodang.R;
import com.banana.Nodang.Retrofit.RetrofitAPI;
import com.banana.Nodang.Retrofit.RetrofitClient;
import com.banana.Nodang.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fResult extends Fragment {
    private NavController navController = null;
    private RetrofitAPI retrofitAPI;
    private ProgressBar progressBar;
    private TextView productName, cont2, cont3, cont4, cont5, cont6, cont7, cont8, cont9, daily, prodKCal;
    private TextView alertText, rawMtrl;
    private SeekBar seekBar;
    private int nKCal;
    private ImageView img;

    private ArrayList<Float> contList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private Context context;
    private Activity activity;

    //    private String getTime() {
//        long now = System.currentTimeMillis();
//        Date date = new Date(now);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String getTime = dateFormat.format(date);
//        getTime.replace("-","");
//        setLog("simpleDate : " + getTime);
//
//        return getTime;
//    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity)
            activity = (Activity) context;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setLog("fResult handleOnBackPressed");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public fResult() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            navController = Navigation.findNavController(view);

            if (getArguments() != null) {
                setLog("Received Code : " + getArguments().getString("code"));
                getProductNameByBarCode(getArguments().getString("code"));
            }

            Button btnReScan = view.findViewById(R.id.btnReScan);
            btnReScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (navController != null) {
                        navController.popBackStack();
                    }
                }
            });

            productName = view.findViewById(R.id.productName);
            cont2 = view.findViewById(R.id.cont2);
            cont3 = view.findViewById(R.id.cont3);
            cont4 = view.findViewById(R.id.cont4);
            cont5 = view.findViewById(R.id.cont5);
            cont6 = view.findViewById(R.id.cont6);
            cont7 = view.findViewById(R.id.cont7);
            cont8 = view.findViewById(R.id.cont8);
            cont9 = view.findViewById(R.id.cont9);
            daily = view.findViewById(R.id.dailyKcal);
            prodKCal = view.findViewById(R.id.productKCal);
            img = view.findViewById(R.id.backView);
            //alertText = view.findViewById(R.id.alertText);
            rawMtrl = view.findViewById(R.id.rawmtrl);
            //alertText.setVisibility(View.INVISIBLE);

            Button btnStore = view.findViewById(R.id.btnStore);
            btnStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String getProductName = productName.getText().toString();
                    String getCont1 = prodKCal.getText().toString();
                    String getCont2 = cont2.getText().toString();
                    String getCont3 = cont3.getText().toString();
                    String getCont4 = cont4.getText().toString();
                    String getCont5 = cont5.getText().toString();
                    String getCont6 = cont6.getText().toString();
                    String getCont7 = cont7.getText().toString();
                    String getCont8 = cont8.getText().toString();
                    String getCont9 = cont9.getText().toString();
                    saveNutr(getProductName, getCont1, getCont2, getCont3, getCont4, getCont5, getCont6, getCont7, getCont8, getCont9);
                }
            });

            rawMtrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getProductRawMaterials(productName.getText().toString());
                }
            });

            seekBar = view.findViewById(R.id.seekBar);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    setLog(String.valueOf(progress));
                    if (progress == 0) {
                        setProgressBarData((int) (nKCal / 2));
                        if(!prodKCal.getText().toString().equals("자료 없음")){
                            float c1 = setCalcurate(1,contList.get(0));
                            prodKCal.setText("" + c1);
                        }else{
                            prodKCal.setText("자료 없음");
                        }
                        if(!cont2.getText().toString().equals("자료 없음")){
                            float c2 = setCalcurate(1,contList.get(1));
                            cont2.setText("" + c2);
                        }else{
                            cont2.setText("자료 없음");
                        }
                        if(!cont3.getText().toString().equals("자료 없음")) {
                            float c3 = setCalcurate(1,contList.get(2));
                            cont3.setText("" + c3);
                        }else{
                            cont3.setText("자료 없음");
                        }
                        if(!cont4.getText().toString().equals("자료 없음")){
                            cont4.setText("" + setCalcurate(1, contList.get(3)));
                        }else{
                            cont4.setText("자료 없음");
                        }
                        if(!cont5.getText().toString().equals("자료 없음")){
                            cont5.setText("" + setCalcurate(1, contList.get(4)));
                        }else{
                            cont5.setText("자료 없음");
                        }
                        if(!cont6.getText().toString().equals("자료 없음")){
                            cont6.setText("" + setCalcurate(1, contList.get(5)));
                        }else{
                            cont6.setText("자료 없음");
                        }
                        if(!cont7.getText().toString().equals("자료 없음")){
                            cont7.setText("" + setCalcurate(1, contList.get(6)));
                        }else{
                            cont7.setText("자료 없음");
                        }
                        if(!cont8.getText().toString().equals("자료 없음")){
                            cont8.setText("" + setCalcurate(1, contList.get(7)));
                        }else{
                            cont8.setText("자료 없음");
                        }
                        if(!cont9.getText().toString().equals("자료 없음")){
                            cont9.setText("" + setCalcurate(1, contList.get(8)));
                        }else{
                            cont9.setText("자료 없음");
                        }
                    }
                    else if (progress == 1) {
                        setProgressBarData(nKCal);
                        if(!prodKCal.getText().toString().equals("자료 없음")){
                            prodKCal.setText("" +setCalcurate(2, contList.get(0)));
                        }else{
                            prodKCal.setText("자료 없음");
                        }
                        if(!cont2.getText().toString().equals("자료 없음")){
                            cont2.setText("" + setCalcurate(2, contList.get(1)));
                        }else{
                            cont2.setText("자료 없음");
                        }
                        if(!cont3.getText().toString().equals("자료 없음")) {
                            cont3.setText("" + setCalcurate(2, contList.get(2)));
                        }else{
                            cont3.setText("자료 없음");
                        }
                        if(!cont4.getText().toString().equals("자료 없음")){
                            cont4.setText("" + setCalcurate(2, contList.get(3)));
                        }else{
                            cont4.setText("자료 없음");
                        }
                        if(!cont5.getText().toString().equals("자료 없음")){
                            cont5.setText("" + setCalcurate(2, contList.get(4)));
                        }else{
                            cont5.setText("자료 없음");
                        }
                        if(!cont6.getText().toString().equals("자료 없음")){
                            cont6.setText("" + setCalcurate(2, contList.get(5)));
                        }else{
                            cont6.setText("자료 없음");
                        }
                        if(!cont7.getText().toString().equals("자료 없음")){
                            cont7.setText("" + setCalcurate(2, contList.get(6)));
                        }else{
                            cont7.setText("자료 없음");
                        }
                        if(!cont8.getText().toString().equals("자료 없음")){
                            cont8.setText("" + setCalcurate(2, contList.get(7)));
                        }else{
                            cont8.setText("자료 없음");
                        }
                        if(!cont9.getText().toString().equals("자료 없음")){
                            cont9.setText("" + setCalcurate(2, contList.get(8)));
                        }else{
                            cont9.setText("자료 없음");
                        }

                    }
                    else if (progress == 2) {
                        setProgressBarData((int) (nKCal * 1.5));
                        if(!prodKCal.getText().toString().equals("자료 없음")){
                            prodKCal.setText("" +setCalcurate(3,contList.get(0)));
                        }else{
                            prodKCal.setText("자료 없음");
                        }
                        if(!cont2.getText().toString().equals("자료 없음")){
                            cont2.setText("" + setCalcurate(3, contList.get(1)));
                        }else{
                            cont2.setText("자료 없음");
                        }
                        if(!cont3.getText().toString().equals("자료 없음")) {
                            cont3.setText("" + setCalcurate(3, contList.get(2)));
                        }else{
                            cont3.setText("자료 없음");
                        }
                        if(!cont4.getText().toString().equals("자료 없음")){
                            cont4.setText("" + setCalcurate(3, contList.get(3)));
                        }else{
                            cont4.setText("자료 없음");
                        }
                        if(!cont5.getText().toString().equals("자료 없음")){
                            cont5.setText("" + setCalcurate(3, contList.get(4)));
                        }else{
                            cont5.setText("자료 없음");
                        }
                        if(!cont6.getText().toString().equals("자료 없음")){
                            cont6.setText("" + setCalcurate(3, contList.get(5)));
                        }else{
                            cont6.setText("자료 없음");
                        }
                        if(!cont7.getText().toString().equals("자료 없음")){
                            cont7.setText("" + setCalcurate(3, contList.get(6)));
                        }else{
                            cont7.setText("자료 없음");
                        }
                        if(!cont8.getText().toString().equals("자료 없음")){
                            cont8.setText("" + setCalcurate(3, contList.get(7)));
                        }else{
                            cont8.setText("자료 없음");
                        }
                        if(!cont9.getText().toString().equals("자료 없음")){
                            cont9.setText("" + setCalcurate(3, contList.get(8)));
                        }else{
                            cont9.setText("자료 없음");
                        }
                    }
                    else if (progress == 3) {
                        setProgressBarData((int) (nKCal * 2));
                        if(!prodKCal.getText().toString().equals("자료 없음")){
                            prodKCal.setText("" +setCalcurate(4,contList.get(0)));
                        }else{
                            prodKCal.setText("자료 없음");
                        }
                        if(!cont2.getText().toString().equals("자료 없음")){
                            cont2.setText("" + setCalcurate(4, contList.get(1)));
                        }else{
                            cont2.setText("자료 없음");
                        }
                        if(!cont3.getText().toString().equals("자료 없음")) {
                            cont3.setText("" + setCalcurate(4, contList.get(2)));
                        }else{
                            cont3.setText("자료 없음");
                        }
                        if(!cont4.getText().toString().equals("자료 없음")){
                            cont4.setText("" + setCalcurate(4, contList.get(3)));
                        }else{
                            cont4.setText("자료 없음");
                        }
                        if(!cont5.getText().toString().equals("자료 없음")){
                            cont5.setText("" + setCalcurate(4, contList.get(4)));
                        }else{
                            cont5.setText("자료 없음");
                        }
                        if(!cont6.getText().toString().equals("자료 없음")){
                            cont6.setText("" + setCalcurate(4, contList.get(5)));
                        }else{
                            cont6.setText("자료 없음");
                        }
                        if(!cont7.getText().toString().equals("자료 없음")){
                            cont7.setText("" + setCalcurate(4, contList.get(6)));
                        }else{
                            cont7.setText("자료 없음");
                        }
                        if(!cont8.getText().toString().equals("자료 없음")){
                            cont8.setText("" + setCalcurate(4, contList.get(7)));
                        }else{
                            cont8.setText("자료 없음");
                        }
                        if(!cont9.getText().toString().equals("자료 없음")){
                            cont9.setText("" + setCalcurate(4, contList.get(8)));
                        }else{
                            cont9.setText("자료 없음");
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    double n = Double.parseDouble(cont5.getText().toString());
                    if(n > 25){
                        Resources res = getResources();
                        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.badback_drawable, getActivity().getTheme());
//                        ImageView tv = (ImageView) view.findViewById(R.id.backView);
//                        tv.setBackground(shape);
                        //alertText.setVisibility(View.VISIBLE);
                        img.setBackground(shape);

                    }else{
                        Resources res = getResources();
                        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.background_drawable, getActivity().getTheme());
//                        ImageView tv = (ImageView) view.findViewById(R.id.backView);
//                        tv.setBackground(shape);
                        img.setBackground(shape);
                        //alertText.setVisibility(View.INVISIBLE);
//                        img.setBackgroundColor(R.drawable.background_drawable);
                    }
                }

            });

            progressBar = view.findViewById(R.id.progressBar);
            progressBar.setMin(1);
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(firebaseUser.getUid()).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    int dKcal = (int)user.getDailyKCal();
                    if (dKcal < 1){
                        progressBar.setMax(2000);
                    }
                    progressBar.setMax(dKcal);
                    daily.setText(Integer.toString(dKcal));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void setProgressBarData(int nProgress) {
        try {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(nProgress);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private float setCalcurate(int n,float value){
        switch(n) {
            case 1:
                value = (float) (value * 0.5);
                break;
            case 2:
                value = value * 1;
                break;
            case 3:
                value = (float) (value * 1.5);
                break;
            case 4:
                value = value * 2;
                break;
        }
        value = Math.round(value*100) / 100f;
        return value;
    }

    // 유저 정보 불러와서 일일 권장량 계산하는 함수 이렇게 안하고 그냥 저장할때 계산하기로
    // 파라미터로 dailyKcal daily x
//    private void getDailyIntake(){
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("users").child(firebaseUser.getUid()).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                // 표준체중 계산 (자신의키-100)*0.9
//                double stdWeight = (Double.parseDouble(user.getHeight()) - 100) * 0.9;
//                setLog("표준체중" + stdWeight);
//                dailyKCal d = new dailyKCal();
//                // 활동량
//                if(user.getMove().equals("low")){
//                    d.setdKCal(stdWeight * 25);
//                    daily.add(d);
//                }else if(user.getMove().equals("mid")){
//                    d.setdKCal(stdWeight * 30);
//                    daily.add(d);
//                }else if(user.getMove().equals("high")) {
//                    d.setdKCal(stdWeight * 40);
//                    daily.add(d);
////                    setLog("high"+ daily.getdKCal());
//                }
////                setLog("user 정보 출력좀" + user.getHeight());
////                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
////                    setLog("snapshot 동작 되는데.." + snapshot.getValue());
////                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    // 아래는 안됨
//        mDatabase.child("users").child(firebaseUser.getUid()).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                //setLog("데이터 확인"+snapshot.getValue());
//                User user = snapshot.getValue(User.class);
//                // 표준체중 계산 (자신의키-100)*0.9
//                double stdWeight = (Double.parseDouble(user.getHeight()) - 100) * 0.9;
//                setLog("표준체중" + stdWeight);
//                // 활동량
//                if(!user.getMove().equals("low")){
//                    dailyKCal = stdWeight * 25;
//                }else if(!user.getMove().equals("mid")){
//                    dailyKCal = stdWeight * 30;
//                }else if(!user.getMove().equals("high")) {
//                    dailyKCal = stdWeight * 40;
//                }
//                setLog("활동량" + dailyKCal);
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

    private void getProductNameByBarCode(String strBarCode) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        try {
            if (retrofitClient != null) {
                retrofitAPI = RetrofitClient.getRetrofitAPI();
                retrofitAPI.getProductNameByBarCode(strBarCode).enqueue(new Callback<MainC005Response>() {
                    @Override
                    public void onResponse(Call<MainC005Response> call, Response<MainC005Response> response) {
                        MainC005Response body = response.body();
                        if (body.getC005().getResult().getCode().equals("INFO-000")) {
                            setLog("제품명 : " + body.getC005().getRow().get(0).getPrdlstNm());
                            productName.setText(body.getC005().getRow().get(0).getPrdlstNm());
                            String name = body.getC005().getRow().get(0).getPrdlstNm().replace(" ","_");
                            getProductIngredientByProductName(name);
                        } else {
                            setToast(activity, "제품명이 검색되지 않았습니다.");
                            setLog("제품명이 검색되지 않았습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<MainC005Response> call, Throwable t) {
                        setLog("Reponse Error : " + t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getProductRawMaterials(String strRmtl) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        strRmtl = strRmtl.replace(" ","_");
        try {
            if (retrofitClient != null) {
                retrofitAPI = RetrofitClient.getRetrofitAPI();
                retrofitAPI.getProductRawMaterials(strRmtl).enqueue(new Callback<MainC002Response>() {
                    @Override
                    public void onResponse(Call<MainC002Response> call, Response<MainC002Response> response) {
                        MainC002Response body = response.body();
                        if (body.getC002().getResult().getCode().equals("INFO-000")) {
//                            setLog("원재료 : " + body.getC002().getRow().get(0).getRawmtrlNm());
                            String str = body.getC002().getRow().get(0).getRawmtrlNm();
                            String [] rmtl = str.split(","); // 기존 원재료 리스트

                            List<String> rmtlList = new ArrayList<>(Arrays.asList(rmtl));
                            String [] sugar = {"설탕","기타과당","유당","맥아엑기스","준초콜릿","가공버터"};
                            //List<String> sugarlist = Arrays.asList(sugar);
                            rmtlList.retainAll(Arrays.asList(sugar));
                            // retainAll은 완전 문자열이 겹쳐야 한다. 기존 원재료리스트에서 중복된 것만 추출된다.
                            rawMtrl.setText(rmtlList.toString());
                        } else {
                            setToast(activity, "원재료가 검색되지 않았습니다.");
                            setLog("원재료가 검색되지 않았습니다.");
                        }
                    }
                    @Override
                    public void onFailure(Call<MainC002Response> call, Throwable t) {
                        setLog("Reponse Error : " + t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 식품 저장 유저 정보 받아서
    // 현재날짜 -> food1,2,3
    private void saveNutr(String productName,String cont1, String cont2, String cont3,String cont4, String cont5, String cont6, String cont7, String cont8, String cont9){
        StoreNutr storeNutr = new StoreNutr(productName, cont1, cont2, cont3, cont4, cont5, cont6, cont7, cont8, cont9);
        /* 현재 날짜 가져와서 저장.. */
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar.getTimeInMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(date);
//        str = str.replace("-","");
//        str = str.substring(2);
        //데이터 저장
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(user.getUid()).child(str).push().setValue(storeNutr)
                .addOnSuccessListener(new OnSuccessListener<Void>() { //데이터베이스에 넘어간 이후 처리
                    @Override
                    public void onSuccess(Void aVoid) {
                        setToast(activity, "식품 영양 정보 저장을 완료했습니다.");

                        fResultDirections.ActionFResultToFStoreNutr action = fResultDirections.actionFResultToFStoreNutr();
                        action.setDate(str);
                        navController.navigate(action);
//                        navController.navigate(R.id.action_fResult_to_fStoreNutr);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setToast(activity, "식품 영양 정보 저장에 실패했습니다.");
                        fResultDirections.ActionFResultToFStoreNutr action = fResultDirections.actionFResultToFStoreNutr();
                        action.setDate(str);
                        navController.navigate(action);
//                        navController.navigate(R.id.action_fResult_to_fStoreNutr);
                    }
                });
    }

    private void getProductIngredientByProductName(String prdlstNm) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        try {
            if (retrofitClient != null) {
                retrofitAPI = RetrofitClient.getRetrofitAPI();
                retrofitAPI.getProductIngredientByProductName(prdlstNm).enqueue(new Callback<MainI2790Response>() {
                    @Override
                    public void onResponse(Call<MainI2790Response> call, retrofit2.Response<MainI2790Response> response) {
                        MainI2790Response body = response.body();
                        if (body.getI2790().getResult().getCode().equals("INFO-000")) {
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setLog("총 내용량 : " + body.getI2790().getRow().get(0).getServingSize());
                                    setLog("열량(kcal)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont1() +"\n");
                                    if (!body.getI2790().getRow().get(0).getNutrCont1().isEmpty()) {
                                        setProgressBarData(Integer.parseInt(body.getI2790().getRow().get(0).getNutrCont1()));
                                        nKCal = Integer.parseInt(body.getI2790().getRow().get(0).getNutrCont1());
                                        prodKCal.setText(""+nKCal);
                                        contList.add(Float.parseFloat(body.getI2790().getRow().get(0).getNutrCont1()));
                                    } else{
                                        prodKCal.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                    if (!body.getI2790().getRow().get(0).getNutrCont2().isEmpty()) {
                                        setLog("탄수화물(g)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont2());
                                        cont2.setText(body.getI2790().getRow().get(0).getNutrCont2());
                                        contList.add(Float.parseFloat(cont2.getText().toString()));
                                    } else {
                                        cont2.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                    if (!body.getI2790().getRow().get(0).getNutrCont3().isEmpty()) {
                                        setLog("단백질(g)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont3());
                                        cont3.setText(body.getI2790().getRow().get(0).getNutrCont3());
                                        contList.add(Float.parseFloat(cont3.getText().toString()));
                                    } else {
                                        cont3.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                    if (!body.getI2790().getRow().get(0).getNutrCont4().isEmpty()) {
                                        setLog("지방(g)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont4());
                                        cont4.setText(body.getI2790().getRow().get(0).getNutrCont4());
                                        contList.add(Float.parseFloat(cont4.getText().toString()));
                                    } else {
                                        cont4.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                    if (!body.getI2790().getRow().get(0).getNutrCont5().isEmpty()) {
                                        setLog("당류(g)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont5());
                                        cont5.setText(body.getI2790().getRow().get(0).getNutrCont5());
                                        // 당 1일 권장량 25g 넘을 시 배경 변경
                                        //double n = Double.parseDouble(cont5.getText().toString());
                                        double n = Double.parseDouble(body.getI2790().getRow().get(0).getNutrCont5());
                                        if(n > 25){
                                            Resources res = getResources();
                                            Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.badback_drawable, getActivity().getTheme());
                                            //alertText.setVisibility(View.VISIBLE);
                                            img.setBackground(shape);
                                        }else{
                                            Resources res = getResources();
                                            Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.background_drawable, getActivity().getTheme());
                                            //alertText.setVisibility(View.INVISIBLE);
                                            img.setBackground(shape);
                                        }
                                        contList.add(Float.parseFloat(cont5.getText().toString()));

                                    } else {
                                        cont5.setText("자료 없음");
                                        contList.add(0f);
                                    }if (!body.getI2790().getRow().get(0).getNutrCont6().isEmpty()) {
                                        setLog("나트륨(mg)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont6());
                                        cont6.setText(body.getI2790().getRow().get(0).getNutrCont6());
                                        contList.add(Float.parseFloat(cont6.getText().toString()));
                                    } else {
                                        cont6.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                    if (!body.getI2790().getRow().get(0).getNutrCont7().isEmpty()) {
                                        setLog("콜레스테롤(mg)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont7());
                                        cont7.setText(body.getI2790().getRow().get(0).getNutrCont7());
                                        contList.add(Float.parseFloat(cont7.getText().toString()));
                                    } else {
                                        cont7.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                    if (!body.getI2790().getRow().get(0).getNutrCont8().isEmpty()) {
                                        setLog("포화지방산(g)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont8());
                                        cont8.setText(body.getI2790().getRow().get(0).getNutrCont8());
                                        contList.add(Float.parseFloat(cont8.getText().toString()));
                                    } else {
                                        cont8.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                    if (!body.getI2790().getRow().get(0).getNutrCont9().isEmpty()) {
                                        setLog("트랜스지방(g)(1회제공량당) : " + body.getI2790().getRow().get(0).getNutrCont9());
                                        cont9.setText(body.getI2790().getRow().get(0).getNutrCont9());
                                        contList.add(Float.parseFloat(cont9.getText().toString()));
                                    } else {
                                        cont9.setText("자료 없음");
                                        contList.add(0f);
                                    }
                                }
                            });
                        } else {
                            setToast(activity, "제품 성분이 검색되지 않았습니다.");
                            setLog("제품 성분이 검색되지 않았습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<MainI2790Response> call, Throwable t) {
                        setLog("Reponse Error : " + t.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}