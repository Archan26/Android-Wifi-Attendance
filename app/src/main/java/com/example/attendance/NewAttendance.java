package com.example.attendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.os.Looper.getMainLooper;
import static android.widget.Toast.LENGTH_SHORT;

public class NewAttendance extends Fragment {
    final String TAG = "studentLectList";
    String Class,div,position,id=null,m=null;
    private int slot,date,month,year,count,fix_i;
    private String[] stList;
    private ListView stlistview;
    private DatabaseReference reffOfStuList,reff_forCmp;
    Button btnStart,btnDiscover;
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    TextView connectionStatus;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray,absent;
    WifiP2pDevice[] deviceArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View NewAttend =  inflater.inflate(R.layout.fragment_new_attendance, container, false);
        btnStart = (Button) NewAttend.findViewById(R.id.wifi);
        btnDiscover = (Button) NewAttend.findViewById(R.id.start);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity().getApplicationContext(),getActivity().getMainLooper(),null);
        mReceiver = new WifiBroadcastReceiver(mManager,mChannel,this);
        connectionStatus = (TextView) NewAttend.findViewById(R.id.connectionStatus);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    btnStart.setText("Wifi is OFF");
                    Toast.makeText(getContext().getApplicationContext(),"Wifi is OFF",Toast.LENGTH_SHORT).show();
                } else {
                    wifiManager.setWifiEnabled(true);
                    btnStart.setText("Wifi is ON");
                    Toast.makeText(getContext().getApplicationContext(),"Wifi is ON",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Running");
                    }
                    @Override
                    public void onFailure(int reason) {
                        connectionStatus.setText("Failed");
                    }
                });
            }
        });
        Bundle bundle = getArguments();
        position = bundle.getString("position");
        Class  = bundle.getString("Class");
        div = bundle.getString("div");
        Calendar calendar = Calendar.getInstance();
        date = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        m = String.valueOf(++month);
        reffOfStuList = FirebaseDatabase.getInstance().getReference().child(String.valueOf(year))
                .child(String.valueOf(m)).child(String.valueOf(date)).child(position)
                .child(Class).child(div);
        getData(NewAttend);

        return NewAttend;
    }
    private void getData(View newAttend) {
        int j;
        Log.d(TAG,"div:"+div);
        if(div.equals("CE1")){
            fix_i=1;
            count=5;
//            Log.d(TAG,"IN CE1");
        } else if(div.equals("CE2")){
            fix_i=6;
            count=10;
//            Log.d(TAG,"IN CE2");
        }
        for(j = fix_i; j<=count; j++){
            id = Class+j;
//            Log.d(TAG,"ID:"+id);
            reffOfStuList.child(id).child("flag").setValue(false);
        }

        getFlag(newAttend);
    }

   private void getFlag(View newAttend){
       stlistview = (ListView) newAttend.findViewById(R.id.studentListView);
        reffOfStuList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int j,c=0,i=fix_i;
                stList = new String[(int)dataSnapshot.getChildrenCount()];
                for(j=i;j<=count;j++){
                    id = Class+j;
                    Log.d(TAG,"ID:"+id);
                    String flagValue = dataSnapshot.child(id).child("flag").getValue().toString();
                    Boolean flag = Boolean.parseBoolean(flagValue);
                    if(!flag){
                        stList[c] = id +": Absent";
                    } else {
                        stList[c] = id + ": Present";
                    }
                    c++;
                }
                Log.d(TAG,"C:"+c);
                if(c==dataSnapshot.getChildrenCount()){
                    ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stList);
                    stlistview.setAdapter(adapt);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Error:"+ databaseError);
            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(final WifiP2pDeviceList peersList) {
            Log.d(TAG, "onPeersAvailable called");
            if (!peersList.getDeviceList().equals(peers)) {
                Log.d(TAG, "in If");
                peers.clear();
                peers.addAll(peersList.getDeviceList());
                deviceNameArray = new String[peersList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peersList.getDeviceList().size()];
                int index = 0;

                for (WifiP2pDevice device : peersList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
            }
            if (peers.size() == 0) {
                Toast.makeText(getActivity().getApplicationContext(), "No Device Found", LENGTH_SHORT).show();
                return;
            }
            compare();
        }
    };

    private void compare() {
        reff_forCmp = FirebaseDatabase.getInstance().getReference().child("Deviceaddress");
        reff_forCmp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int j;
                for (j = 0; j <peers.size(); j++) {
                    int i=fix_i;
                    Log.d(TAG, "From Nearby:" + peers.get(j).deviceAddress);
                    String cmp = peers.get(j).deviceAddress;
                    for (int k = 0; k < dataSnapshot.child(Class).child(div).getChildrenCount(); k++) {
                        id = Class+i;
                        Log.d(TAG,"Compare id:"+id);
                        Log.d(TAG, "Count:" + dataSnapshot.child(Class).child(div).getChildrenCount());
                        String address = dataSnapshot.child(Class).child(div).child(id).child("deviceAdd").getValue().toString();
                        Log.d(TAG, "From database:" + k + " " + address);
                        if (address.equals(cmp)) {
                            reffOfStuList.child(id).child("flag").setValue(true);
                            Log.d(TAG, "True");
                        } else {
                            Log.d(TAG, "False");
                        }
                        i++;
                    }
                }
                if(j==peers.size()){
                    absentDetails();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error:" + databaseError);
            }
        });
    }

    private void absentDetails(){
        reffOfStuList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,"In Absent Details");
                int j,i=fix_i;
                stList = new String[(int) dataSnapshot.getChildrenCount()];
                for(j=0;j<dataSnapshot.getChildrenCount();j++){
                    id = Class+i;
                    Log.d(TAG,"ID::"+id);
                    String tag = dataSnapshot.child(id).child("flag").getValue().toString();
                    boolean flag = Boolean.parseBoolean(tag);
                    if(!flag){
                        Log.d(TAG,"Enter in IF");
                        stList[j] = id+": Absent" ;
                    }else{
                        stList[j] = id + ": Present";
                    }
                    i++;
                }
                if(j==dataSnapshot.getChildrenCount()){
                    Log.d(TAG,"absent: "+ Arrays.toString(stList));
                    ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, stList);
                    stlistview.setAdapter(adapt);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"Error:"+ databaseError);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }
}
