package com.example.manag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class KhiarAddPage extends AppCompatActivity {
    EditText name,price,amount;
    Button add;
    Spinner section;
    FirebaseDatabase database;
    DatabaseReference reference;
    Product product;
    TextView productIngredientsTextView;
    String productIngredients;
    ImageButton ingredientsBtn;
    boolean keto;
    boolean sugarFree;
    boolean vegan;
    int BACK =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khiar_add_page);
        Spinner mySpin = (Spinner) findViewById(R.id.spinner);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        ArrayAdapter<String> myAdpt = new ArrayAdapter<String>(KhiarAddPage.this,android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Section));
        myAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpin.setAdapter(myAdpt);

        product= new Product();
        name=(EditText) findViewById(R.id.addpname);
        price=(EditText) findViewById(R.id.addpprice);
        amount=(EditText) findViewById(R.id.addpamount);
        section=(Spinner) findViewById(R.id.spinner);
        productIngredientsTextView=(TextView)findViewById(R.id.ProductIngredientsTV);
        add=(Button)findViewById(R.id.addp);
        ingredientsBtn=(ImageButton)findViewById(R.id.bPicture);
//        productIngredients=getIntent().getStringExtra("IngredientsKey");
//        productIngredientsTextView.setText(productIngredients);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productInsert();
            }
        });
        ingredientsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ingredientsImg = new Intent(KhiarAddPage.this,ingredientsProductImg.class);
                startActivityForResult(ingredientsImg,BACK);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==BACK){
            if(resultCode==RESULT_OK){
                productIngredients=data.getStringExtra("IngredientsKey");
                productIngredientsTextView.setText(productIngredients);
            }
        }
    }

    private void productInsert() {
        Map<String,Object> map= new HashMap<>();
        map.put("name",name.getText().toString());
        map.put("price",price.getText().toString());
        map.put("amount",amount.getText().toString());
        map.put("section",section.getSelectedItem().toString());
        map.put("ingredients",productIngredientsTextView.getText().toString());
        dietSection();
        map.put("keto",keto);
        map.put("sugarFree",sugarFree);
        map.put("vegan",vegan);
        FirebaseDatabase.getInstance().getReference().child("Products").push().setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        name.setText("");
                        price.setText("");
                        amount.setText("");
                        productIngredientsTextView.setText("");
                        Toast.makeText(getApplicationContext(), "Product successfully added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Could not added", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public void ProductIngredients(View v){
        Intent ingredientsImg = new Intent(this,ingredientsProductImg.class);
        startActivity(ingredientsImg);
    }
    public void dietSection(){
        keto=true;
        sugarFree=true;
        vegan=true;
        /**String[] ka={"apple","orange","dates","mango","pomegranate","banana","dried fruit","grape","kiwi","peach","fig","cantaloupe","pineapple","pear","raisin",
                "potatoes","sweet potatoes","baked potatoes","corn","peas","carrot","yam", "avocado oil",
                "rice", "wheat", "oats", "barley","quinoa","kale",
                "bread","corn flakes","pasta", "pizza","popcorn",
                "Beans"};
        String[] la={"soybean oil","olive oil","coconut oil"};
        String[] va={"milk"};*/
        String line="";
        String line2="";
        String line3="";
        ArrayList<String> ketoAvoid=new ArrayList<>();
        ArrayList<String> sugarAvoid=new ArrayList<>();
        ArrayList<String> veganvoid=new ArrayList<>();
        try{
            File file=new File("C:\\Users\\leele\\AndroidStudioProjects\\Manag\\app\\ketoAvoid.txt");
            Scanner scan=new Scanner(file);
            File file2=new File("C:\\Users\\leele\\AndroidStudioProjects\\Manag\\app\\SugarFreeAvoid.txt");
            Scanner scan2=new Scanner(file2);
            File file3=new File("C:\\Users\\leele\\AndroidStudioProjects\\Manag\\app\\veganAvoid.txt");
            Scanner scan3=new Scanner(file3);
            while(scan.hasNext()){
                line=line.concat(scan.next()+" ");
                ketoAvoid = new ArrayList<>(Arrays.asList(line.split(",")));
            }
            while(scan2.hasNext()){
                line2=line2.concat(scan2.next()+" ");
                sugarAvoid = new ArrayList<>(Arrays.asList(line2.split(",")));
            }
            while(scan3.hasNext()){
                line3=line3.concat(scan3.next()+" ");
                veganvoid = new ArrayList<>(Arrays.asList(line3.split(",")));
            }


        }catch (FileNotFoundException e){

        }catch(IOException e){}

        /**ArrayList<String> ketoAvoid=new ArrayList<>(Arrays.asList(ka));
        ArrayList<String> lowAvoid=new ArrayList<>(Arrays.asList(sa));
        ArrayList<String> veganvoid=new ArrayList<>(Arrays.asList(va));*/
        String[] sugarfreeDrinksAvoid={"Blueberry", "Caramel", "Chai" , "Chamomile", "Chocolate", "Cinnamon", "Cranberry", "Echinacea"};


        String i=productIngredientsTextView.getText().toString();
        String s=section.getSelectedItem().toString();
        String n=name.getText().toString();

        ArrayList<String> ing = new ArrayList<>(Arrays.asList(i.split(",")));
        if(s.toLowerCase().equals("fresh")){
            for(String avoid: ketoAvoid){
                if(n.toLowerCase().trim().contains(avoid.toLowerCase().trim())){
                    keto=false;
                }
            }
        }else {
            for (String ingredient : ing) {
                for (String avoid : ketoAvoid) {
                    if (ingredient.toLowerCase().trim().contains(avoid.toLowerCase().trim())) {
                        keto = false;
                    }
                }


            }
        }
        if(s.toLowerCase().equals("drinks")){
            for(String avoid: sugarfreeDrinksAvoid){
                if(n.toLowerCase().trim().contains(avoid.toLowerCase().trim())){
                    sugarFree=false;
                }
            }

        }else{
            for (String ingredient : ing) {
                for (String avoid : sugarAvoid) {
                    if (ingredient.toLowerCase().trim().contains(avoid.toLowerCase().trim())) {
                        sugarFree = false;
                    }
                }


            }

        }
        if(s.toLowerCase().equals("frozen")){
            for(String avoid: veganvoid){
                if(n.toLowerCase().trim().contains(avoid.toLowerCase().trim())){
                    vegan=false;
                }
            }

        }else{
            for (String ingredient : ing) {
                for (String avoid : veganvoid) {
                    if (ingredient.toLowerCase().trim().contains(avoid.toLowerCase().trim())) {
                        vegan = false;
                    }
                }


            }

        }

    }
}
