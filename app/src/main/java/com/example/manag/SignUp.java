package com.example.manag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    EditText pass,email;
    Button signIn, signUp;
    FirebaseAuth fAuth;
    ProgressDialog progressDialog;
    String  password , e_mail;
    String times = ""+System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signIn=findViewById(R.id.signInBut);
        signUp=findViewById(R.id.signUpBut);
        //username=findViewById(R.id.userName);
        pass=findViewById(R.id.pass);
        email=findViewById(R.id.email);
        //phoneNo=findViewById(R.id.phone);
        fAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                password=pass.getText().toString().trim();
                e_mail=email.getText().toString().trim();
                if(TextUtils.isEmpty(password)&&TextUtils.isEmpty(e_mail)){
                    pass.setError("Password is required");
                    email.setError("Email is required");
                    return;

                }
                if(TextUtils.isEmpty(password)){
                    pass.setError("Password is required");
                    return;

                }
                if(TextUtils.isEmpty(e_mail)){
                    email.setError("Email is required");
                    return;

                }
                if(password.length()<6){
                    pass.setError("Password Must be at least 6 Characters");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()){
                    email.setError("Please enter valid email");
                    return;

                }
                createaccount();

            }
        });
        signIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(SignUp.this,SignIn.class));
            }
        });
    }

    private void createaccount() {
        progressDialog.setMessage("Create Account...");
        progressDialog.show();
        fAuth.createUserWithEmailAndPassword(e_mail,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(SignUp.this, "User Created.", Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(SignUp.this).create();
                        alertDialog.setTitle("Warning");
                        alertDialog.setMessage("The Key Must Kept In Your Mind To Be Able To Login In The Future \n"+"\n"+times);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                                savefirbasedata();

                            }
                        });
                        alertDialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void savefirbasedata() {
        progressDialog.setMessage("Saving Account Info...");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",fAuth.getUid());
        hashMap.put("keyuser",times);
        hashMap.put("email",""+e_mail);
        hashMap.put("pass",""+password);
        DatabaseReference RF = FirebaseDatabase.getInstance().getReference("Manger");
        RF.child(fAuth.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        startActivity(new Intent(SignUp.this,KhiarAddPage.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        //   startActivity(new Intent(SignUp.this,Home.class));
                        Toast.makeText(getApplicationContext(),"Failed Saving.."+e.getMessage(),Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }


}

