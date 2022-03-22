package com.example.manag;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeleteProductPage extends AppCompatActivity {
    RecyclerView recyclerView;
    productAdapter productAdap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product_page);

        recyclerView = (RecyclerView) findViewById(R.id.recyclreView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(
                FirebaseDatabase.getInstance().getReference().child("Data"),
                Product.class).build();
        productAdap = new productAdapter(options);
        recyclerView.setAdapter(productAdap);


    }



    protected void onStart() {
        super.onStart();
        productAdap.startListening();

    }
    protected void onStop() {
        productAdap.startListening();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.serch_product,menu);
        MenuItem item=menu.findItem(R.id.search_bar);
        SearchView searchView=(SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processSearch(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void processSearch(String s){
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>().setQuery(
                FirebaseDatabase.getInstance().getReference().child("Data").orderByChild("name").startAt(s).endAt(s+"\uf8ff"),
                Product.class).build();
        productAdap=new productAdapter(options);
        productAdap.startListening();
        recyclerView.setAdapter(productAdap);
    }
}