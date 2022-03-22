package com.example.manag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton add;
    Button add2;
    ImageButton deletePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = (ImageButton) findViewById(R.id.imageButton2);
        //add2 = (Button) findViewById(R.id.button5);
        deletePage= (ImageButton) findViewById(R.id.imageButton3);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Lujain");


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