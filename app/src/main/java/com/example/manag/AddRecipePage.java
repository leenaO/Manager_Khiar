package com.example.manag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class AddRecipePage extends AppCompatActivity {
    Button addIng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe_page);
        Spinner mySpin = (Spinner) findViewById(R.id.spinnerD);
        ArrayAdapter<String> myAdpt = new ArrayAdapter<String>(AddRecipePage.this,android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Diet));
        myAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpin.setAdapter(myAdpt);


        addIng = (Button) findViewById(R.id.buttonIng);


    }
    public void resIng(View v){
        Intent add = new Intent(this,addRecpieIngredients.class);
        startActivity(add);


    }
}
