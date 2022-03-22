package com.example.manag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class addRecpieIngredients extends AppCompatActivity {
    private List<String> items;
    private ArrayAdapter<String> itemsAdpt;
    private ListView listView;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recpie_ingredients);
        listView =findViewById(R.id.listView);
        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(v);
            }
        });
        items = new ArrayList<>();
        itemsAdpt= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,items);
        listView.setAdapter(itemsAdpt);
        setUpListViewListener();

    }

    private void setUpListViewListener(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Context context = getApplicationContext();
                Toast.makeText(context, "One Ingredient Removed", Toast.LENGTH_LONG).show();
                items.remove(i);
                itemsAdpt.notifyDataSetChanged();
                return true;
            }
        }
        );}

    private void addItem(View v){
        EditText input = findViewById(R.id.et);
        String itemText= input.getText().toString();
        if(!(itemText.equals(""))){
            itemsAdpt.add(itemText);
            input.setText("");
        }else{
            Toast.makeText(getApplicationContext(), "Please enter text..", Toast.LENGTH_LONG).show();
        }


    }

}