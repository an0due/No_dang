package com.banana.Nodang.Fragment;

import static com.banana.Nodang.Utils.LogDisplay.setLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.banana.Nodang.LoginActivity;
import com.banana.Nodang.MainActivity;
import com.banana.Nodang.SignUpActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import org.threeten.bp.LocalDate;

import com.banana.Nodang.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class fMain extends Fragment {
    private NavController navController = null;
    private Context context;
    private Activity activity;
    private MaterialCalendarView calendarView;

    private FirebaseAuth firebaseAuth;
    private Button buttonSignOut;
    final CharSequence[] date = {null};

    GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference mDatabase;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity)
            activity = (Activity) context;
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setLog("fMain handleOnBackPressed");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public fMain() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        Button btnScan = view.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController != null)
                    navController.navigate(R.id.action_fMain_to_fScanCamera);
            }
        });

        calendarView = view.findViewById(R.id.calendarview);
        // 처음 선택된 날을 오늘 날짜로 설정
        calendarView.setSelectedDate(CalendarDay.today());
        // 월, 요일을 한글로 보이게 설정
        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        // 폰트 스타일 설정
        calendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader);
        // 좌우 화살표 가운데의 연/월이 보이는 방식 커스텀
        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                LocalDate inputText = day.getDate();
                String[] calendarHeaderElements = inputText.toString().split("-");
                StringBuilder calendarHeaderBuilder = new StringBuilder();
                calendarHeaderBuilder.append(calendarHeaderElements[0])
                        .append(" ")
                        .append(calendarHeaderElements[1]);
                return calendarHeaderBuilder.toString();
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            public void onSelectedDayChange(){

            }
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                setToast(activity, "클릭한 날짜" + date.getDate());
                String day = date.getDate().toString();

                fMainDirections.ActionFMainToFStoreNutr action = fMainDirections.actionFMainToFStoreNutr();
                action.setDate(day);
                //navController.navigate(R.id.action_fMain_to_fStoreNutr);
                //Navigation.findNavController(view).navigate(action);
                navController.navigate(action);
                //calendarView.addDecorator(EventDecorator(Collections.singleton(date))); // 캘린더에 점찍기
            }
        });

        buttonSignOut = view.findViewById(R.id.sign_out);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage();
            }
        });

//        calendarView.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                setToast(activity, "클릭 이벤트 테스트");
//                navController.navigate(R.id.action_fMain_to_fStoreNutr);
//            }
//        });

//        eventDay = CalendarDay.from(2022, 11, 05);
//        calendarView.setOnDateChangedListener(eventDay -> {
//            navController.navigate(R.id.action_fMain_to_fStoreNutr);
//        });

    }
    private void showMessage(){
        firebaseAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("안내");
        builder.setMessage("로그아웃하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                firebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = builder.create();
        builder.show();
    }

    private void revokeAccess() {
        // 회원탈퇴 버튼
        firebaseAuth.getCurrentUser().delete();
    }

}