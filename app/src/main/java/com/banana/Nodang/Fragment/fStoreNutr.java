package com.banana.Nodang.Fragment;

import static com.banana.Nodang.Utils.LogDisplay.setLog;
import static com.banana.Nodang.Utils.LogDisplay.setToast;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banana.Nodang.R;

import com.banana.Nodang.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class fStoreNutr extends Fragment implements View.OnClickListener,FoodViewListener{
    private NavController navController = null;
    private ProgressBar progressBar;

    private Context context;
    private Activity activity;
    private DatabaseReference mDatabase;

    private TextView currentDate, daily_kcal;
    private ArrayList<StoreNutr> foodList = null;
    private StoreAdapter storeAdapter = null;
    private String day = null;

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

    public fStoreNutr() {
        foodList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        findDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        daily_kcal = view.findViewById(R.id.dailyText);

        progressBar = view.findViewById(R.id.progress_store);
        progressBar.setMin(1);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(firebaseUser.getUid()).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                int dKcal = (int)user.getDailyKCal();
                findDate(dKcal);
                if (dKcal < 1){
                    progressBar.setMax(2000);
                }
                progressBar.setMax(dKcal);
                daily_kcal.setText(Integer.toString(dKcal));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.foodlistView);

        storeAdapter = new StoreAdapter(foodList, getActivity(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(storeAdapter);

        // 리사이클뷰 구분선 및 리스트 여백
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), layoutManager);
//        recyclerView.addItemDecoration(dividerItemDecoration);
        OffsetItemDecoration itemDecoration = new OffsetItemDecoration(50);
        recyclerView.addItemDecoration(itemDecoration);

        String day = fStoreNutrArgs.fromBundle(getArguments()).getDate();
        currentDate = (TextView)view.findViewById(R.id.text_logo);
        currentDate.setText(""+day);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController != null)
                    navController.navigate(R.id.action_fStoreNutr_to_fMain);
            }
        });

        Button insert = view.findViewById(R.id.btnInsert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController != null) {
                    String day = fStoreNutrArgs.fromBundle(getArguments()).getDate();
                    fStoreNutrDirections.ActionFStoreNutrToFNutrInsert action = fStoreNutrDirections.actionFStoreNutrToFNutrInsert();
                    action.setDate(day);
                    navController.navigate(action);
                    //navController.navigate(R.id.action_fStoreNutr_to_fNutrInsert);
                }
            }
        });

//        productName = view.findViewById(R.id.productName);
//        mReference.child("users").child("테스트1").child("221105").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                adapter.clear();
//                for (DataSnapshot messageData : dataSnapshot.getChildren()){
//                    String value = dataSnapshot.getValue().toString();
//                    Array.add(value);
//                    adapter.add(value);
//                    productName.setText(value);
//                }
//                adapter.notifyDataSetChanged();
//                listView.setSelection(adapter.getCount()-1);
//                StoreNutr nutr = dataSnapshot.getValue(StoreNutr.class);
//                cont2 = nutr.getCont2();
//                cont3 = nutr.getCont3();
//                cont4 = nutr.getCont4();
//                cont5 = nutr.getCont5();
//                cont6 = nutr.getCont6();
//                cont7 = nutr.getCont7();
//                cont8 = nutr.getCont8();
//                cont9 = nutr.getCont9();
//
//                //텍스트뷰에 받아온 문자열 대입하기
//                goaltime_tv.setText(goaltime);
//                gintro_tv.setText(gintro);
//                gdate_tv.setText(gdate);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
//            }
//        });

    }

    private void setProgressBarData(int nProgress, int max) {
        try {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressBar.getProgress() >= max){
                        progressBar.setProgress(max);
                    }
                    progressBar.incrementProgressBy(nProgress);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void findDate(int dailyKcal){
        //StoreNutr storeNutr = new StoreNutr();
        /* 현재 날짜 가져오는데..? */
//        Calendar calendar = Calendar.getInstance();
//        Date date = new Date(calendar.getTimeInMillis());
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String str = format.format(date);
        String day = fStoreNutrArgs.fromBundle(getArguments()).getDate();
//        day = day.replace("-","");
//        if(day.length() > 6) {
//            day = day.substring(2);
//        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("users").child(user.getUid()).child("info").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    StoreNutr item = snapshot.getValue(StoreNutr.class);
//                    foodList.add(item);
//                    storeAdapter.notifyDataSetChanged();
//                    sum = Integer.parseInt(item.getCont1());
//                    setProgressBarData(sum); // food kcal 합치기
//                }
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        mDatabase.child("users").child(firebaseUser.getUid()).child(day).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                StoreNutr item = snapshot.getValue(StoreNutr.class);
                foodList.add(item);
//              setLog("----------- foodList ---------" + item.getCont1());
                double sum = Double.parseDouble(item.getCont1());
//                setLog("------------sum ------------" + sum);
                setProgressBarData((int)sum, dailyKcal);
                storeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.text_logo:
//                findDate();
//                break;
//        }
    }

    @Override
    public void onItemClick(int position, View view) {

    }

//    private void initDatabase(){
//        mDatabase = FirebaseDatabase.getInstance();
//    }

}