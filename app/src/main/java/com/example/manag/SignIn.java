package com.example.manag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class SignIn extends AppCompatActivity {
    Button signIn, signUp , forgetPassButton;
    EditText pass;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String password;
    TextView textView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signIn = findViewById(R.id.signInButton);
        signUp = findViewById(R.id.signUptButton);
        pass = findViewById(R.id.pass);
        textView3 = (TextView) findViewById(R.id.textView3);
        forgetPassButton = (Button)findViewById(R.id.forgetPassButton);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("pleas wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignIn.this, SignUp.class));
            }
        });
        forgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn.this, forgetpass.class));
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginuser();

                //startActivity(new Intent(SignIn.this, HomePage.class));

            }


        });
    }
    private void loginuser() {
        password = pass.getText().toString();

        if(TextUtils.isEmpty(password)){
            pass.setError("Please enter your password");
            return;
        }
        DatabaseReference RF = FirebaseDatabase.getInstance().getReference("Manger");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        RF.orderByChild("keyuser").equalTo(password).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressDialog.setMessage("Sing in Account ...");
                    startActivity(new Intent(SignIn.this, KhiarAddPage.class));
                    progressDialog.dismiss();
                }else{
                    pass.setError("the key not found or deleted");
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pass.setError(error.getMessage());

            }
        });


    }

}