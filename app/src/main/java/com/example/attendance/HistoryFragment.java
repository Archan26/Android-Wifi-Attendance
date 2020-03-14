package com.example.attendance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class HistoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View historyView = inflater.inflate(R.layout.fragment_history,container,false);
        final CalendarView calendarView = (CalendarView) historyView.findViewById(R.id.calendar);
        final String TAG = "History";
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.d(TAG,"Date: "+dayOfMonth+"."+month+"."+year);
                String dd = String.valueOf(dayOfMonth);
                String mm = String.valueOf(month+1);
                String yy = String.valueOf(year);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                Log.d(TAG,"Day of the week: "+dayOfWeek);

                Bundle bundle = new Bundle();
                bundle.putString("date",dd);
                bundle.putString("month",mm);
                bundle.putString("year",yy);
                bundle.putString("dayOfWeek",String.valueOf(dayOfWeek));

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                LectureHistoryFragment lectureHistoryFragment = new LectureHistoryFragment();
                lectureHistoryFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_container,lectureHistoryFragment).commit();
            }
        });
        return historyView;
    }
}
