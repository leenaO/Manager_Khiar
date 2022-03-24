package com.example.manag;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    boolean keto;
    boolean sugarFree;
    boolean vegan;
    int BACK =1;
    ImageView ingredientsBtn;
    private ImageView front_imageV;
    private RelativeLayout frontP, backP;
    Uri uri;
    static final int CAMERA_REQUEST_CODE= 200;
    static final int STORAGE_REQUEST_CODE=400;
    static final int IMAGE_PICK_GALLERY_CODE=1000;
    static final int IMAGE_PICK_CAMERA_CODE=1001;
    StorageReference storageReference;
    String cameraPermission[] = {Manifest.permission.CAMERA};
    String storagePermission[];

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
        ingredientsBtn = (ImageView) findViewById(R.id.bPicture);
        front_imageV = (ImageView)findViewById(R.id.fPicture);
//        productIngredients=getIntent().getStringExtra("IngredientsKey");
//        productIngredientsTextView.setText(productIngredients);
        storageReference = FirebaseStorage.getInstance().getReference("Products_Manger/");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty()){
                    name.setError("Is empty");
                    return;
                }
                if(name.getText().toString().contains("[0-9]+")){
                    Toast.makeText(getApplicationContext(),"Name Incorrect input! should be letters",Toast.LENGTH_LONG).show();
                    return;
                }
                if(price.getText().toString().isEmpty()){
                    price.setError("The filed is empty");
                    return;
                }
                if(price.getText().toString().contains("[a-zA-Z]+")){
                    price.setError("Incorrect input! should be number");
                    return;
                }

                if(price.getText().toString().length()<=0 ){
                    Toast.makeText(getApplicationContext(),"Price Incorrect input! should be great 0",Toast.LENGTH_LONG).show();
                    return;
                }  if(amount.getText().toString().length()<=0 ){
                    Toast.makeText(getApplicationContext(),"Amount Incorrect input! should be great 0",Toast.LENGTH_LONG).show();
                    return;
                }
                if( amount.getText().toString().contains("-")){
                    Toast.makeText(getApplicationContext(),"Amount Incorrect input! should be great 0",Toast.LENGTH_LONG).show();
                    return;
                }


                if( amount.getText().toString().isEmpty()){
                    amount.setError("The filed is empty");
                    return;
                }
                if(price.getText().toString().isEmpty() || amount.getText().toString().isEmpty() ){
                    Toast.makeText(getApplicationContext(),"The filed is empty",Toast.LENGTH_LONG).show();
                    return;
                }

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
    /**file extinction jpg , png,... **/
    private String getfileextiontio(Uri u){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(u));
    }



    private void productInsert() {
        if(uri != null){
            StorageReference storageReference1 = storageReference.child(System.currentTimeMillis()+"."+getfileextiontio(uri));
            storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Map<String, Object> map = new HashMap<>();
                            map.put("name", name.getText().toString());
                            map.put("price", price.getText().toString());
                            map.put("amount", amount.getText().toString());
                            map.put("section", section.getSelectedItem().toString());
                            map.put("ingredients", productIngredientsTextView.getText().toString());
                            dietSection();
                            map.put("keto",keto);
                            map.put("sugarFree",sugarFree);
                            map.put("vegan",vegan);
                            map.put("image",String.valueOf(uri));


                            FirebaseDatabase.getInstance().getReference().child("Products").push().setValue(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            name.setText("");
                                            price.setText("");
                                            amount.setText("");
                                            productIngredientsTextView.setText("");
                                            front_imageV.setImageURI(null);
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

    public void ProductIngredients(View v) {
        Intent ingredientsImg = new Intent(this, ingredientsProductImg.class);
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


    // add photo
    private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
        androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i ==0){
                    //camera
                    if(!checkCameraPermission()) {
                        requestCameraPermission();
                        pickCamera();
                    }
                }else if(i==1){
                    //gallery
                    if(!checkStoragePermission()){

                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        Intent intent =new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(KhiarAddPage.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "new-photo-name.jpg");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }



    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }



    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case CAMERA_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if (cameraAccepted && writeStorageAccepted) {
//                        pickCamera();
//                    } else {
//                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
//                    }
//                }
//                break;
//            case STORAGE_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if (writeStorageAccepted) {
//                        pickGallery();
//                    } else {
//                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
//                    }
//                }
//                break;
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==BACK){
            if(resultCode==RESULT_OK){
                productIngredients=data.getStringExtra("IngredientsKey");
                productIngredientsTextView.setText(productIngredients);
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                uri = data.getData();
                front_imageV.setImageURI(uri);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                try {
                    ContentResolver cr = getContentResolver();
                    try {
                        // Creating a Bitmap with the image Captured
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                        // Setting the bitmap as the image of the
                        front_imageV.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IllegalArgumentException e) {
                    if (e.getMessage() != null)
                        Log.e("Exception", e.getMessage());
                    else
                        Log.e("Exception", "Exception");
                    e.printStackTrace();

                }
            }
        }
    }

    // when click card relative
    public void FP(View view) {
        showImageImportDialog();
    }
}
