package com.example.attendance;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class absentDetails extends Fragment {
    final String TAG = "absentList";
    int position = 0,j,count;
    String Class = null,div=null,date=null,month=null,year=null,slot=null,id;
    String[] absentLect;
    ListView absentlistView = null;
    TextView absenttextView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View absentView = inflater.inflate(R.layout.fragment_absent_details, container, false);
        Bundle bundle = getArguments();
        position =  bundle.getInt("Position");
        slot = "0"+position;
        Class = bundle.getString("Class");
        div = bundle.getString("div");
        date = bundle.getString("date");
        month = bundle.getString("month");
        year = bundle.getString("year");


        absenttextView = (TextView) absentView.findViewById(R.id.abList);
        absenttextView.setText(Class.concat(" "+div));

        if(div.equals("CE1")){
            j=1;
            count=5;
//            Log.d(TAG,"IN CE1");
        } else if(div.equals("CE2")){
            j=6;
            count=10;
//            Log.d(TAG,"IN CE2");
        }
        getabsentDetails(absentView);
        return absentView;
    }

    private void getabsentDetails(View absentView) {
        absentlistView = (ListView) absentView.findViewById(R.id.absentListView);
        DatabaseReference reffOfAbsent;
        reffOfAbsent = FirebaseDatabase.getInstance().getReference().child(year).child(month).child(date).child(slot).child(Class)
                .child(div);
        reffOfAbsent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i;
                Log.d(TAG,"Position:"+slot+":"+Class+":"+div+":"+date+":"+month+":"+year);
                int count = (int) dataSnapshot.getChildrenCount();
                Log.d(TAG,"Count:"+count);
                absentLect = new String[count];
                for(i=0;i<count;i++){
                    id = Class+j;
                    String value = dataSnapshot.child(id).child("flag").getValue().toString();
                    boolean flag = Boolean.parseBoolean(value);
                    if(flag){
                        absentLect[i] = id+": Present";
                    } else{
                        absentLect[i] = id+": Absent";
                    }
                    j++;
                }
                if(i==count){
                    Log.d(TAG,"Absent:"+ Arrays.toString(absentLect));
                    ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, absentLect);
                    absentlistView.setAdapter(adapt);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Error:"+ databaseError);
            }
        });
    }
}