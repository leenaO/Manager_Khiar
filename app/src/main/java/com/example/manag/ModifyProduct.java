package com.example.manag;

import static android.Manifest.permission.CAMERA;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;


public class ModifyProduct extends AppCompatActivity {
    static final int CAMERA_REQUEST_CODE= 200;
    static final int STORAGE_REQUEST_CODE=400;
    static final int IMAGE_PICK_GALLERY_CODE=1000;
    static final int IMAGE_PICK_CAMERA_CODE=1001;
    String cameraPermission[];
    String storagePermission[];
    Uri uri;
    DatabaseReference databaseReference;
    Button m;
    Button prodModifyButton2,productModify;
    ImageView prodModifyImg2;
    StorageReference storageReference;
    EditText productName,productPrice,productAmount;
    String key="";
    //Uri img2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String img = intent.getStringExtra("image");
        String amount = intent.getStringExtra("amount");
        String price = intent.getStringExtra("price");
        key = intent.getStringExtra("key");
        storageReference = FirebaseStorage.getInstance().getReference("Products_Manger/");


        cameraPermission= new String[]{CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission= new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};



        prodModifyButton2=findViewById(R.id.prodModifyButton2);
        prodModifyImg2=findViewById(R.id.prodModifyImg2);
        productName=findViewById(R.id.productName);
        productAmount=findViewById(R.id.productAmount);
        productPrice=findViewById(R.id.productPrice);
        productModify=findViewById(R.id.productModify);
//        prev=findViewById(R.id.backProductsPage);
//        prev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(ModifyProduct.this,DeleteProductPage.class));
//            }
//        });

        productModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!key.equals("")) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products").child(key);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap<String,Object> map=new HashMap<>();
//                            if(!img2.equals("")) {
//                                map.put("image", img2);
//                            }
                            map.put("name",productName.getText().toString());
                            map.put("price",productPrice.getText().toString());
                            map.put("amount",productAmount.getText().toString());
                            ref.updateChildren(map);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if(uri != null){
                        StorageReference storageReference1 = storageReference.child(System.currentTimeMillis()+"."+getfileextiontio(uri));
                        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //DatabaseReference dbr=FirebaseDatabase.getInstance().getReference("Products");
                                        //DatabaseReference dbr1=FirebaseDatabase.getInstance().getReference("Products").push();
                                        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Products");

                                        String ProductId=db2.push().getKey();

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("image",String.valueOf(uri));


                                        db2.child(key).updateChildren(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        Toast.makeText(getApplicationContext(), "Product successfully added", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Could not added"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }else { Toast.makeText(getApplicationContext(),"pleas add one image",Toast.LENGTH_LONG).show();
                    }
                }
                startActivity(new Intent(ModifyProduct.this,DeleteProductPage.class));

            }
        });


        Glide.with(getApplicationContext()).load(img).into(prodModifyImg2);
        productName.setText(name);
        productAmount.setText(amount);
        productPrice.setText(price);

        prodModifyButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageImportDialog();

            }
        });





//        imageView= findViewById(R.id.prodModifyImg2);

    }
    private String getfileextiontio(Uri u){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(u));
    }

    public void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i ==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickCamera();
                    }
                }
                if(i==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    public void pickGallery() {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    public void pickCamera() {
        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");//title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");//description
        uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    public void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);

    }

    public boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    public boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode ==RESULT_OK){
//            if(requestCode== IMAGE_PICK_GALLERY_CODE){
//                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(ModifyProduct.this);
//
//            }
//            if(requestCode==IMAGE_PICK_CAMERA_CODE){
//                CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).start(ModifyProduct.this);
//            }
//        }

//        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                //Uri resultUri= result.getUri();
                uri=data.getData();
                prodModifyImg2.setImageURI(uri);
                //img2=resultUri;

                //BitmapDrawable bitmapDrawable=(BitmapDrawable) prodModifyImg2.getDrawable();

            }

//            else if(resultCode== CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                Exception exception= result.getError();
//                Toast.makeText(this, ""+exception, Toast.LENGTH_SHORT).show();
//            }
//        }
    }



}

