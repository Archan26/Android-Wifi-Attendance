package com.example.attendance;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class LectureHistoryFragment extends Fragment {
    final String TAG = "LecHistoryList";
    String day=null,sub=null,Class=null,div = null,date=null,month=null,year=null;
    String[] historyLectList = null,arrayClass,arrayDiv;
    ListView historylistView = null;
    TextView historytextView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View lecHisView =  inflater.inflate(R.layout.fragment_history_lec, container, false);
        getDetailsOfWeek(lecHisView);
        getDetailsOfLec(lecHisView);
        historylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String data = (String) historylistView.getItemAtPosition(position);
                int dat = (int) historylistView.getItemIdAtPosition((int)id+1);
                Bundle lecHisBundle = new Bundle();
                lecHisBundle.putInt("Position",dat);
                lecHisBundle.putString("Class",arrayClass[(int)id]);
                lecHisBundle.putString("div",arrayDiv[(int)id]);
                lecHisBundle.putString("date",date);
                lecHisBundle.putString("month",month);
                lecHisBundle.putString("year",year);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                absentDetails absentfrag = new absentDetails();
                absentfrag.setArguments(lecHisBundle);

                fragmentTransaction.replace(R.id.fragment_container,absentfrag).commit();
            }
        });
        return lecHisView;
    }

    private void getDetailsOfWeek(View lecHisView){
        Bundle bundle = getArguments();
        date = bundle.getString("date");
        month = bundle.getString("month");
        year = bundle.getString("year");
        String dayOfWeek = bundle.getString("dayOfWeek");

        switch (dayOfWeek) {
            case "1":
                day = "Sunday";
                break;
            case "2":
                day = "Monday";
                break;
            case "3":
                day = "Tuesday";
                break;
            case "4":
                day = "Wednesday";
                break;
            case "5":
                day = "Thursday";
                break;
            case "6":
                day = "Friday";
                break;
            case "7":
                day = "Saturday";
                break;
        }
        Log.d(TAG,"Date: "+date+"."+month+"."+year);
        historytextView = (TextView) lecHisView.findViewById(R.id.dateText);
        historytextView.setText(date+"."+month+"."+year+"  "+day);
    }

    private void getDetailsOfLec(View lecHisView) {
        historylistView = (ListView) lecHisView.findViewById(R.id.historylistView);
        DatabaseReference reffOfLectures;
        reffOfLectures = FirebaseDatabase.getInstance().getReference().child("TimeTable");
        reffOfLectures.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i;
                String tag = dataSnapshot.child(day).child("slot").getValue().toString();
                boolean slot = Boolean.parseBoolean(tag);
                if(slot){
                    historyLectList = new String[(int) dataSnapshot.child(day).getChildrenCount()-1];
                    arrayDiv = new String[(int) dataSnapshot.child(day).getChildrenCount()-1];
                    arrayClass = new String[(int) dataSnapshot.child(day).getChildrenCount()-1];
                    int countLec = (int) (dataSnapshot.child(day).getChildrenCount()-1);
                    Log.d(TAG,"Total lect: "+ historyLectList.length);

                    for(i=0;i<countLec;i++){
                        String c = "0"+ (i+1);
                        sub = dataSnapshot.child(day).child(c).child("sub").getValue().toString();
                        Class = dataSnapshot.child(day).child(c).child("class").getValue().toString();
                        div = dataSnapshot.child(day).child(c).child("div").getValue().toString();
                        arrayDiv[i] = div;
                        arrayClass[i] = Class;
                        historyLectList[i] = sub +" : "+Class+" : "+div;
                    }
                    if(i==countLec){
                        Log.d(TAG,"Total lecture: "+ Arrays.toString(historyLectList));
                        ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, historyLectList);
                        historylistView.setAdapter(adapt);
                    }
                }else{
                    historyLectList = new String[1];
                    historyLectList[0]= "Today is the Holiday";
                    ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, historyLectList);
                    historylistView.setAdapter(adapt);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG,"Error:"+ databaseError);
            }
        });
    }
}
