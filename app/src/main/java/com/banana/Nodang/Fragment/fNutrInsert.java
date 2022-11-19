package com.banana.Nodang.Fragment;

import static com.banana.Nodang.Utils.LogDisplay.setLog;
import static com.banana.Nodang.Utils.LogDisplay.setToast;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.banana.Nodang.R;
import com.banana.Nodang.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanOptions;

public class fNutrInsert extends Fragment {
    private NavController navController = null;

    private Context context;
    private Activity activity;

    private DatabaseReference mDatabase;
    private EditText productName, cont1, cont2, cont3, cont4, cont5, cont6, cont7;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutr_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController != null)
                    navController.popBackStack();
            }
        });

        productName = view.findViewById(R.id.editText_foodName);
        cont1 = view.findViewById(R.id.edit_newcont1);
        cont2 = view.findViewById(R.id.edit_newcont2);
        cont3 = view.findViewById(R.id.edit_newcont3);
        cont4 = view.findViewById(R.id.edit_newcont4);
        cont5 = view.findViewById(R.id.edit_newcont5);
        cont6 = view.findViewById(R.id.edit_newcont6);
        cont7 = view.findViewById(R.id.edit_newcont7);

        Button btnInsert = view.findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getProductName = productName.getText().toString();
                String getCont1 = cont1.getText().toString();
                String getCont2 = cont2.getText().toString();
                String getCont3 = cont3.getText().toString();
                String getCont4 = cont4.getText().toString();
                String getCont5 = cont5.getText().toString();
                String getCont6 = cont6.getText().toString();
                String getCont7 = cont7.getText().toString();
                String getCont8 = "자료 없음";
                String getCont9 = "자료 없음";
                insertNutr(getProductName, getCont1, getCont2, getCont3, getCont4, getCont5, getCont6, getCont7, getCont8, getCont9);
            }
        });
    }

    private void insertNutr(String productName, String cont1, String cont2, String cont3, String cont4, String cont5, String cont6, String cont7, String cont8, String cont9){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StoreNutr storeNutr = new StoreNutr(productName, cont1, cont2, cont3, cont4, cont5, cont6, cont7, cont8, cont9);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String day = fStoreNutrArgs.fromBundle(getArguments()).getDate();
//        day = day.replace("-","");
//        if(day.length() > 6) {
//            day = day.substring(2);
//        }
        //데이터 저장
        String finalDay = day;
        mDatabase.child("users").child(user.getUid()).child(day).push().setValue(storeNutr)
                .addOnSuccessListener(new OnSuccessListener<Void>() { //데이터베이스에 넘어간 이후 처리
                    @Override
                    public void onSuccess(Void aVoid) {
                        setToast(activity, "식품 추가 저장을 완료했습니다.");
                        fNutrInsertDirections.ActionFNutrInsertToFStoreNutr action = fNutrInsertDirections.actionFNutrInsertToFStoreNutr();
                        action.setDate(finalDay);

                        navController.navigate(action);
//                        navController.navigate(R.id.action_fNutrInsert_to_fStoreNutr);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setToast(activity, "식품 추가 저장에 실패했습니다.");
                        fNutrInsertDirections.ActionFNutrInsertToFStoreNutr action = fNutrInsertDirections.actionFNutrInsertToFStoreNutr();
                        action.setDate(finalDay);
                        navController.navigate(action);
//                        navController.navigate(R.id.action_fNutrInsert_to_fStoreNutr);
                    }
                });
    }

}