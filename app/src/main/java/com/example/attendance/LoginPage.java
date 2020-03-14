package com.example.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {

    Button checkUser;
    TextView wrongCre;
    EditText username,password;
    DatabaseReference loginReff;
    private static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        initial();
    }

    private void initial() {
        checkUser = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.passWord);
        wrongCre = (TextView) findViewById(R.id.wrongID);
        loginReff = FirebaseDatabase.getInstance().getReference().child("Login");
        checkUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Username = username.getText().toString();
                final String Password = password.getText().toString();

                Log.d(TAG,"Username:"+Username);
                Log.d(TAG,"Password:"+Password);

                loginReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String user = dataSnapshot.child("User1").child("username").getValue().toString();
                        String pass = dataSnapshot.child("User1").child("password").getValue().toString();

                        if(user.equals(Username) && pass.equals(Password)){
                            Log.d(TAG,"Success");
                            startActivity(new Intent(LoginPage.this,MainActivity.class));
                        } else{
                            wrongCre.setText("Invalid Username and Password");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Error:"+databaseError,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
