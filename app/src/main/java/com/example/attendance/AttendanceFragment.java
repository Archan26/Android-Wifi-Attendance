package com.example.attendance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;

import static android.widget.Toast.LENGTH_SHORT;

public class AttendanceFragment extends Fragment {
    final String TAG = "AttendLectList";
    String day=null,sub=null,Class=null,div=null,sendDiv=null,sendClass=null,post=null;
    int date,month,year,slot;
    String[] attendlectList,arrayClass,arrayDiv;
    ListView attendlistView = null;
    TextView attendtextView = null;
    DatabaseReference reffOfStuList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View attendView = inflater.inflate(R.layout.fragment_attendance,container,false);
        getDetails_OfWeek(attendView);
        getDetails_OfLec(attendView);
        attendlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String slot = (String) attendlistView.getItemAtPosition(position);
                slot = (int) attendlistView.getItemIdAtPosition((int)id+1);
                post = "0"+slot;

                Log.d(TAG,"SLOT:"+slot);
                Bundle bundle = new Bundle();
//                bundle.putInt("slot",slot);
                bundle.putString("position",post);
                bundle.putString("Class",arrayClass[slot-1]);
                bundle.putString("div",arrayDiv[slot-1]);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                NewAttendance newAttendance = new NewAttendance();
                newAttendance.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_container,newAttendance).commit();

            }
        });
        return attendView;
    }

    private void getDetails_OfWeek(View attendView) {
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        int  dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        attendtextView = (TextView) attendView.findViewById(R.id.todayDate);
        attendtextView.setText(date+"."+(month+1)+"."+year+"  "+day);
    }
    private void getDetails_OfLec(View attendView) {
        attendlistView = (ListView) attendView.findViewById(R.id.attendlistView);
        DatabaseReference reffOfLectures;
        reffOfLectures = FirebaseDatabase.getInstance().getReference().child("TimeTable");
        reffOfLectures.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i;
                String tag = dataSnapshot.child(day).child("slot").getValue().toString();
                boolean flagslot = Boolean.parseBoolean(tag);
                if(flagslot){
                    attendlectList = new String[(int) dataSnapshot.child(day).getChildrenCount()-1];
                    arrayClass = new String[(int) dataSnapshot.child(day).getChildrenCount()-1];
                    arrayDiv = new String[(int) dataSnapshot.child(day).getChildrenCount()-1];
                    int countLec = (int) (dataSnapshot.child(day).getChildrenCount()-1);
                    Log.d(TAG,"Total lect: "+ attendlectList.length);

                    for(i=0;i<countLec;i++){
                        String c = "0"+ (i+1);
                        sub = dataSnapshot.child(day).child(c).child("sub").getValue().toString();
                        Class = dataSnapshot.child(day).child(c).child("class").getValue().toString();
                        div = dataSnapshot.child(day).child(c).child("div").getValue().toString();
                        arrayClass[i] = Class;
                        arrayDiv[i] = div;
                        attendlectList[i] = sub +" : "+Class+" : "+div;
                    }
                    if(i==countLec){
                        Log.d(TAG,"Total lecture: "+ Arrays.toString(attendlectList));
                        ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, attendlectList);
                        attendlistView.setAdapter(adapt);
                    }
                }else{
                    attendlectList = new String[1];
                    attendlectList[0]= "Today is the Holiday";
                    ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, attendlectList);
                    attendlistView.setAdapter(adapt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Error:"+ databaseError);
            }
        });
    }
}
