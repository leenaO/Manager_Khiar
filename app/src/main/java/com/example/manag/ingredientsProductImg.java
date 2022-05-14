package com.example.manag;

import static android.Manifest.permission.CAMERA;

//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;


public class ingredientsProductImg extends AppCompatActivity {
    EditText editTextIngredients;
    ImageView imgViewIngredients;
    Button saveTheProductIngredients;
    static final int CAMERA_REQUEST_CODE= 200;
    static final int STORAGE_REQUEST_CODE=400;
    static final int IMAGE_PICK_GALLERY_CODE=1000;
    static final int IMAGE_PICK_CAMERA_CODE=1001;
    String cameraPermission[];
    String storagePermission[];
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_product_img);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click + button to insert image");

        editTextIngredients=findViewById(R.id.resultEt);
        imgViewIngredients=findViewById(R.id.imageIv);
        saveTheProductIngredients=findViewById(R.id.saveTheProductIngredients);

        //camera permission
        cameraPermission= new String[]{
            CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission= new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        saveTheProductIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productIngredients = editTextIngredients.getText().toString();
                Intent ingredientsIntent=new Intent();
                ingredientsIntent.putExtra("IngredientsKey",productIngredients);
                setResult(RESULT_OK,ingredientsIntent);
                finish();
            }
        });
    }

      //action bar menu
      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
          getMenuInflater().inflate(R.menu.add_image_action_bar, menu);
          return true;
      }

      //handle actionbar item click
      @Override
      public boolean onOptionsItemSelected(@NonNull MenuItem item) {
          int id =item.getItemId();
          if(id == R.id.addProductIngredientImg){
              showImageImportDialog();
          }
          if(id==R.id.settings){
              Toast.makeText(this,"Settings",Toast.LENGTH_LONG).show();
          }
          return super.onOptionsItemSelected(item);
      }

      private void showImageImportDialog() {
        String[] items = {"Camera", "Gallery"};
          AlertDialog.Builder dialog = new AlertDialog.Builder(this);

          dialog.setTitle("select Image");
          dialog.setItems(items, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                  if (i ==0){
                      //camera
                      if(!checkCameraPermission()){
                          //camera permission not allowed, request it
                          requestCameraPermission();
                      }else{
                          //camera permission allowed
                          pickCamera();
                      }
                  }
                  if(i==1){
                      //gallery
                      if(!checkStoragePermission()){
                          //Storage permission not allowed, request it
                          requestStoragePermission();
                      }else{
                          //camera permission allowed
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

      private void pickCamera() {
        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");//title of the picture
          values.put(MediaStore.Images.Media.DESCRIPTION,"Image to text");//description
          uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
          Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
          startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
      }

      private void requestStoragePermission() {
          ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
      }

      private boolean checkStoragePermission() {
          boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
          return result;
      }

      private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
      }

      private boolean checkCameraPermission() {
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
          if(resultCode ==RESULT_OK){
              if(requestCode== IMAGE_PICK_GALLERY_CODE){
                  CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(ingredientsProductImg.this);

              }
              if(requestCode==IMAGE_PICK_CAMERA_CODE){
                  CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).start(ingredientsProductImg.this);
              }
          }

          if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
              CropImage.ActivityResult result = CropImage.getActivityResult(data);
              if(resultCode==RESULT_OK){
                  Uri resultUri= result.getUri();
                  imgViewIngredients.setImageURI(resultUri);
                  BitmapDrawable bitmapDrawable=(BitmapDrawable) imgViewIngredients.getDrawable();
                  Bitmap bitmap= bitmapDrawable.getBitmap();
                  TextRecognizer recognizer= new TextRecognizer.Builder(this).build();
                  if(!recognizer.isOperational()){
                      Toast.makeText(this, "Text recognizer not working", Toast.LENGTH_SHORT).show();
                  }else{
                      Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                      SparseArray<TextBlock> items = recognizer.detect(frame);
                      StringBuilder sb = new StringBuilder();
                      for(int i =0; i<items.size(); i++){
                          TextBlock myItem= items.valueAt(i);
                          sb.append(myItem.getValue());
                          sb.append("\n");
                      }
                      editTextIngredients.setText(sb.toString());
                  }
              }

              else if(resultCode== CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                  Exception exception= result.getError();
                  Toast.makeText(this, ""+exception, Toast.LENGTH_SHORT).show();
              }
          }
      }




      //        captureImage=findViewById(R.id.CaptureImage);
//        copyIngredients=findViewById(R.id.ExtractIngredients);
//        textIngredients=findViewById(R.id.ExtractedIngredients);
//        imgIngredients=findViewById(R.id.ingredientsImg);


        //////////////////////////////////////////////بداية/////////////////////////////////////////////////////////////////

//        if(ContextCompat.checkSelfPermission(ingredientsProductImg.this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(ingredientsProductImg.this,new String[]{
//                    Manifest.permission.CAMERA
//            },REQUEST_IMAGE_CAPTURE);
//        }
//        captureImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ingredientsProductImg.this);
//
//            }
//        });
//        copyIngredients.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String scannedText= textIngredients.getText().toString();
//                copyToClipBoard(scannedText);
//
//            }
//        });
//   }
        //////////////////////////////////////////////نهايه/////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////بداية////////////////////////////////////////////////////////////////
//@Override
//protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//    super.onActivityResult(requestCode, resultCode, data);
//    if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//        CropImage.ActivityResult result=CropImage.getActivityResult(data);
//        if(resultCode==RESULT_OK){
//             Uri uri=result.getUri();
//            try {
//                bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
//                getTextFromImage(bitmap);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//private void getTextFromImage(Bitmap bitmap){
//        TextRecognizer textRecognizer=new TextRecognizer.Builder(this).build();
//        if(!textRecognizer.isOperational()){
//            Toast.makeText(ingredientsProductImg.this,"Error",Toast.LENGTH_LONG).show();
//        }else{
//            //to extract text from image
//            Frame frame= new Frame.Builder().setBitmap(bitmap).build();
//            SparseArray<TextBlock> textBlockSparseArray=textRecognizer.detect(frame);
//            StringBuilder stringBuilder=new StringBuilder();
//            for (int i = 0; i<textBlockSparseArray.size();i++){
//                TextBlock textBlock= textBlockSparseArray.valueAt(i);
//                stringBuilder.append(textBlock.getValue());
//                stringBuilder.append("\n");
//            }
//            textIngredients.setText(stringBuilder.toString());
//            captureImage.setText("Retake");
//            copyIngredients.setVisibility(View.VISIBLE);
//            imgIngredients.setImageBitmap(bitmap);
//        }
//
//}
//    private void copyToClipBoard(String  text){
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("copied data",text);
//        clipboard.setPrimaryClip(clip);
//        Toast.makeText(ingredientsProductImg.this,"Copied to clipboard",Toast.LENGTH_LONG).show();
//    }

        //////////////////////////////////////////////نهايه/////////////////////////////////////////////////////////////////




}
// private void requestCameraPermission() {
////        PermissionCode=200;
////        ContentValues values = new ContentValues();
////        values.put(MediaStore.Images.Media.TITLE, "new-photo-name.jpg");
////        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
////        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
////        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
////        someActivityResultLauncher1.launch(intent);
////    }
////    private void requestStoragePermission() {
////        PermissionCode =100;
////        Intent intent = new Intent(Intent.ACTION_PICK,
////                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
////        intent.setType("image/*");
////        someActivityResultLauncher1.launch(intent);
////
////    }

