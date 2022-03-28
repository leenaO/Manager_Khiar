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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        map.put("keto",dietSection());
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
    public boolean dietSection(){
        boolean keto=true;
        boolean low=true;
        boolean vegan=true;
        String[] ka={"apple","orange","dates","mango","pomegranate","banana","dried fruit","grape","kiwi"
        ,"peach","fig","cantaloupe","pineapple","pear","raisin",
                "potatoes","sweet potatoes","baked potatoes","corn","peas","carrot","yam","avocado oil"};
        String[] la={"soybean oil","olive oil","coconut oil"};
        String[] va={"milk"};

        ArrayList<String> ketoAvoid=new ArrayList<>(Arrays.asList(ka));
        ArrayList<String> lowAvoid=new ArrayList<>(Arrays.asList(la));
        ArrayList<String> veganvoid=new ArrayList<>(Arrays.asList(va));


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
                    if (ingredient.toLowerCase().trim().equals(avoid.toLowerCase().trim())) {
                        keto = false;
                    }
                }


            }
        }
        return keto;
    }
}
