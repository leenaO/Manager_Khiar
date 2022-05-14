package com.example.manag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class forgetpass extends AppCompatActivity {
    private EditText Email;
    private TextView key;
    private Button Reset, Back;


    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);
        Email = (EditText) findViewById(R.id.email);

        Reset = (Button) findViewById(R.id.btn_reset_password);
        key = (TextView) findViewById(R.id.key);
        Back = (Button) findViewById(R.id.btn_back);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait");
        progressDialog.setCanceledOnTouchOutside(false);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Loading");
                progressDialog.show();
                String email = Email.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    progressDialog.dismiss();
                    Email.setError("Enter your registered email");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Fields email pattern", Toast.LENGTH_LONG).show();
                    return;


                }

                DatabaseReference RF = FirebaseDatabase.getInstance().getReference("Manger");
                DatabaseReference RF1 = FirebaseDatabase.getInstance().getReference("Manger");
                RF1.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            RF.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot d : snapshot.getChildren()) {
                                        String getkey = d.child("keyuser").getValue(String.class);
                                        key.setText(getkey);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Email.setError(error.getMessage());
                                    progressDialog.dismiss();

                                }
                            });
                        }else {
                            key.setText("");
                            Email.setError("The Email Not Found Or Deleted ");
                            progressDialog.dismiss();
                            return;}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }
}