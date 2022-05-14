package com.example.manag;

        import static android.Manifest.permission.CAMERA;

        import android.Manifest;
        import android.app.Activity;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.database.Cursor;
        import android.graphics.drawable.BitmapDrawable;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.ImageView;
        import android.widget.SearchView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;
        import com.firebase.ui.database.FirebaseRecyclerOptions;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.theartofdev.edmodo.cropper.CropImage;
        import com.theartofdev.edmodo.cropper.CropImageView;

        import java.util.HashMap;
        import java.util.Map;

public class DeleteProductPage extends AppCompatActivity{
    static final int CAMERA_REQUEST_CODE= 200;
    static final int STORAGE_REQUEST_CODE=400;
    private String selectedImagePath;
    RecyclerView recyclerView;
    productAdapter productAdap;
    Activity activity;
    Context context;
    ImageView imageView;
    Uri uri;
    String cameraPermission[];
    String storagePermission[];
    String productKey="";
    Uri selectedImageUri;
    String productId="";
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_product_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cameraPermission= new String[]{CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission= new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imageView=findViewById(R.id.prodModifyImg2);
        activity=DeleteProductPage.this;
        context=getApplicationContext();
        recyclerView = findViewById(R.id.recyclreView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(
                FirebaseDatabase.getInstance().getReference().child("Products"),
                Product.class).build();
        productAdap = new productAdapter(options,this);
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
    public void setProductKey(String productKey){
        this.productKey=productKey;
    }
    public void setProductId(String productId){
        this.productId=productId;
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
                        FirebaseDatabase.getInstance().getReference().child("Products").orderByChild("name").startAt(s).endAt(s+"\uf8ff"),
                        Product.class).build();
        productAdap=new productAdapter(options,this);
        productAdap.startListening();
        recyclerView.setAdapter(productAdap);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedImageUri = data.getData();
        setSelectrdImage(selectedImageUri);
        if(resultCode ==RESULT_OK){
            Map<String, Object> map = new HashMap<>();
            map.put("image", String.valueOf(selectedImageUri));
            FirebaseDatabase.getInstance().getReference().child("Products").child(productKey).updateChildren(map);
            Map<String, Object> map2 = new HashMap<>();
            map2.put("img", String.valueOf(selectedImageUri));
            FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("cart");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (snapshot.child(dataSnapshot.getKey()).exists()) {
                            if (snapshot.child(dataSnapshot.getKey()).child(productId).exists()) {
                                ref.child(dataSnapshot.getKey()).child(productId).updateChildren(map2);

                            }
                        }
                }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            productAdap.displayImage(selectedImageUri);


        }
    }
    Uri getSelectrdImage(){
        return selectedImageUri;
    }
    void setSelectrdImage(Uri selectedImageUri ){
        this.selectedImageUri=selectedImageUri;
    }
}


