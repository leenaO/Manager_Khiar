package com.example.manag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    CardView add;
    Button add2;
    CardView deletePage;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = (CardView) findViewById(R.id.imageButton2);
        //add2 = (Button) findViewById(R.id.button5);
        deletePage= (CardView) findViewById(R.id.imageButton3);
        TextView user = findViewById(R.id.usertext);
        TextView Product = findViewById(R.id.totalP);
        TextView Recipe = findViewById(R.id.rectext);
        TextView order = findViewById(R.id.ordtext);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Lujain");
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@androidx.annotation.NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:

                        return true;
                    case R.id.product:
                        startActivity(new Intent(getApplicationContext(),KhiarAddPage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.orgnize:
                        startActivity(new Intent(getApplicationContext(),DeleteProductPage.class));
                        overridePendingTransition(0,0);
                        return true;



                }
                return false;
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    int i ;
                    String  p = null;
                    int a =0;
                    String a1= "";
                    for (  i = 0 ; i<product.getProductId().length(); i++){
                        p +=product.getProductId();
                        a ++;
                        a1=""+a;


                    }
                    Product.setText(a1);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("recipe");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    int i ;
                    String  p = null;
                    int a =0;
                    String a1= "";
                    for (  i = 0 ; i<databaseReference1.getKey().length(); i++){
                        p +=databaseReference1.getKey();
                        a ++;
                        a1=""+a;


                    }
                    Recipe.setText(a1);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Account");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){


                    int i ;
                    String  p = null;
                    int a =0;
                    String a1= "";
                    for (  i = 0 ; i<databaseReference2.getKey().length(); i++){
                        p +=databaseReference2.getKey();
                        a ++;
                        a1=""+a;


                    }
                    user.setText(a1);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Cart");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){


                    int i ;
                    String  p = null;
                    int a =0;
                    String a1= "";
                    for (  i = 0 ; i<databaseReference3.getKey().length(); i++){
                        p +=databaseReference3.getKey();
                        a ++;
                        a1=""+a;


                    }
                    order.setText(a1);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addProductAction(View v){
        Intent add = new Intent(this,KhiarAddPage.class);
        startActivity(add);
    }
    public void mdPrd(View v){
        Intent add = new Intent(this,AddRecipePage.class);
        startActivity(add);
    }
    public void DeleteProductAction(View v){
        Intent delete = new Intent(this,DeleteProductPage.class);
        startActivity(delete);
    }
}