package com.example.manag;


        import androidx.cardview.widget.CardView;
        import android.annotation.SuppressLint;
        import android.app.AlertDialog;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.Uri;
        import android.provider.MediaStore;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;
        import com.bumptech.glide.Glide;
        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.firebase.ui.database.FirebaseRecyclerOptions;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.orhanobut.dialogplus.DialogPlus;
        import com.orhanobut.dialogplus.ViewHolder;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;


public class productAdapter  extends FirebaseRecyclerAdapter<Product,productAdapter.myViewHolder>  {
    static final int IMAGE_PICK_GALLERY_CODE=1000;
    static final int IMAGE_PICK_CAMERA_CODE=1001;
    Uri uri;
    Context context;
    Uri modifiedImg;
    String image;
    String worstImage;
    void setSelectedImage(String worstImage){
        this.worstImage=worstImage;
    }
    public void displayImage(Uri modifiedUri) {
        modifiedImg=modifiedUri;
    }

    public productAdapter(@NonNull FirebaseRecyclerOptions<Product> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int postion, @NonNull Product product) {
        holder.productNm.setText(product.getName());
        Glide.with(context).load(product.getImage()).into(holder.productImg);

        holder.modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(view.getContext(), ModifyProduct.class);
                intent.putExtra("name", product.getName());
                if(product.getImage()!=null) {
                    intent.putExtra("image", product.getImage());
                }
                intent.putExtra("amount", product.getAmount());
                intent.putExtra("price",product.getPrice());
                intent.putExtra("key",product.getProductKey());
                view.getContext().startActivity(intent);


                //((DeleteProductPage) context).startActivityForResult(new Intent());
            }



        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(holder.productImg.getContext());
                builder.setTitle("Delete product");
                builder.setMessage("Are you sure that you want delete the product?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                           FirebaseDatabase.getInstance().getReference().child("Likes").child(product.getProductId()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Products").child(getRef(postion).getKey()).removeValue();
                        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("cart");
                       DatabaseReference accountRef= FirebaseDatabase.getInstance().getReference().child("Account");
                       ArrayList<String> accountId=new ArrayList<>();
                        accountRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    accountId.add(dataSnapshot.getKey());

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                                db.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            //String productId=dataSnapshot.getKey();
                                            for(String id: accountId) {
                                                if (dataSnapshot.getKey().equals(id)){
                                                    Iterable<DataSnapshot> a=dataSnapshot.getChildren();
                                                    for(DataSnapshot i: a){
                                                        if(i.getKey().equals(product.getProductId())){
                                                            db.child(id).child(product.getProductId()).removeValue();
                                                        }

                                                    }

                                                }
                                            }

                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cardview,parent,false);
        return new myViewHolder(view);
    }
    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView productImg; TextView productNm;
        ImageButton modify,delete;
        CardView cardView;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            productImg=itemView.findViewById(R.id.productImage);
            productNm=itemView.findViewById(R.id.productName);
            modify=itemView.findViewById(R.id.modifyProduct);
            delete=itemView.findViewById(R.id.deleteProduct);
            cardView=itemView.findViewById(R.id.productCV);
        }
    }
    void glide(ImageView pImg,String key){
        DatabaseReference databaseReference=
                FirebaseDatabase.getInstance().getReference().child("Products").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                image=snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Glide.with(context).load(image).into(pImg);  }
}





//===================================

//package com.example.manag;
//
//
//        import androidx.cardview.widget.CardView;
//        import android.annotation.SuppressLint;
//        import android.app.AlertDialog;
//        import android.content.ContentValues;
//        import android.content.Context;
//        import android.content.DialogInterface;
//        import android.content.Intent;
//        import android.net.Uri;
//        import android.provider.MediaStore;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.Button;
//        import android.widget.EditText;
//        import android.widget.ImageButton;
//        import android.widget.ImageView;
//        import android.widget.TextView;
//
//        import androidx.annotation.NonNull;
//        import androidx.recyclerview.widget.RecyclerView;
//        import com.bumptech.glide.Glide;
//        import com.firebase.ui.database.FirebaseRecyclerAdapter;
//        import com.firebase.ui.database.FirebaseRecyclerOptions;
//        import com.google.android.gms.tasks.OnFailureListener;
//        import com.google.android.gms.tasks.OnSuccessListener;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.ValueEventListener;
//        import com.orhanobut.dialogplus.DialogPlus;
//        import com.orhanobut.dialogplus.ViewHolder;
//        import java.util.HashMap;
//        import java.util.Map;
//
//
//public class productAdapter  extends FirebaseRecyclerAdapter<Product,productAdapter.myViewHolder>  {
//    static final int IMAGE_PICK_GALLERY_CODE=1000;
//    static final int IMAGE_PICK_CAMERA_CODE=1001;
//    Uri uri;
//    Context context;
//    Uri modifiedImg;
//    String image;
//    String worstImage;
//    void setSelectedImage(String worstImage){
//        this.worstImage=worstImage;
//    }
//    public void displayImage(Uri modifiedUri) {
//        modifiedImg=modifiedUri;
//    }
//
//    public productAdapter(@NonNull FirebaseRecyclerOptions<Product> options, Context context) {
//        super(options);
//        this.context = context;
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int postion, @NonNull Product product) {
//        holder.productNm.setText(product.getName());
//        Glide.with(context).load(product.getImage()).into(holder.productImg);
//        holder.modify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final DialogPlus dialogPlus =
//                        DialogPlus.newDialog(context)
//                                .setContentHolder(new ViewHolder(R.layout.dialog_content))
//                                .setExpanded(true, 1650).create();
//                View myView = dialogPlus.getHolderView();
//                EditText pName = myView.findViewById(R.id.productName);
//                EditText pPrice = myView.findViewById(R.id.productPrice);
//                EditText pAmount = myView.findViewById(R.id.productAmount);
//                ImageView pImg = myView.findViewById(R.id.prodModifyImg2);
//                Button modifyBut = myView.findViewById(R.id.productModify);
//                Button m = myView.findViewById(R.id.prodModifyButton2);
//                ImageView back= myView.findViewById(R.id.backProductsPage);
//                pName.setText(product.getName());
//                pPrice.setText(product.getPrice());
//                pAmount.setText(product.getAmount());
//                Glide.with(context).load(product.getImage()).into(pImg);
//                dialogPlus.show();
//                m.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String[] items = {"No", "Yes"};
//                        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
//                        dialog.setTitle("select Image");
//                        dialog.setMessage("Would you like to change the product image?");
////                        dialog.setItems(items, new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialogInterface, int i) {
////                                if (i ==0){
////
////                                }
////                                if(i==1) {
////
//////                                    String key=product.getProductKey();
//////                                    String uriValue=((DeleteProductPage)context).getSelectrdImage().toString();
//////                                    Glide.with((DeleteProductPage)context).load(modifiedImg).into(pImg);
////                                }
////                            }
////                        });
//                        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(Intent.ACTION_PICK);
//                                intent.setType("image/*");
//                                ((DeleteProductPage)context).setProductKey(product.getProductKey());
//                                ((DeleteProductPage)context).startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
//                            }
//                        });
//                        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                        dialog.create().show();
//                    }
//                });
////                if(modifiedImg!=null){
////                    Glide.with((DeleteProductPage)context).load(modifiedImg).into(pImg);
////                }
//                FirebaseDatabase.getInstance().getReference().child("Products").child(product.getProductKey()).child("image").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String image= snapshot.getValue(String.class);
//                        Glide.with((DeleteProductPage)context).load(image).into(pImg);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//                modifyBut.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("name", pName.getText().toString());
//                        map.put("price", pPrice.getText().toString());
//                        map.put("amount", pAmount.getText().toString());
//                        //                        map.put("section", pSection.getResources().getStringArray(R.array.Section));
//                        FirebaseDatabase.getInstance().getReference().child("Products").child(getRef(postion).getKey()).updateChildren(map)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        dialogPlus.dismiss();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        dialogPlus.dismiss();
//                                    }
//                                });

//             DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("cart");
//                        DatabaseReference accountRef= FirebaseDatabase.getInstance().getReference().child("Account");
//                        ArrayList<String> accountId=new ArrayList<>();
//                        accountRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                    accountId.add(dataSnapshot.getKey());
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//
//                        db.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                    //String productId=dataSnapshot.getKey();
//                                    for(String id: accountId) {
//                                        if (dataSnapshot.getKey().equals(id)){
//                                            Iterable<DataSnapshot> a=dataSnapshot.getChildren();
//                                            for(DataSnapshot i: a){
//                                                if(i.getKey().equals(product.getProductId())){
//                                                    Map<String, Object> map2 = new HashMap<>();
//                                                    map2.put("name", pName.getText().toString());
//                                                    map2.put("price", pPrice.getText().toString());
//                                                    map2.put("img", purl.getText().toString());
//                                                    map2.put("amount",pAmount.getText().toString());
//                                                    db.child(id).child(product.getProductId()).updateChildren(map2);
//                                                }
//
//                                            }
//
//                                        }
//                                    }
//
//                                }
//
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });

//                    }
//                });
//                back.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialogPlus.dismiss();
//                    }
//                });
//
//            }
//        });
//        holder.delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder= new AlertDialog.Builder(holder.productImg.getContext());
//                builder.setTitle("Delete product");
//                builder.setMessage("Are you sure that you want delete the product?");
//                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                           FirebaseDatabase.getInstance().getReference().child("Likes").child(product.getProductId()).removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("Products").child(getRef(postion).getKey()).removeValue();
//                        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("cart");
//                       DatabaseReference accountRef= FirebaseDatabase.getInstance().getReference().child("Account");
//                       ArrayList<String> accountId=new ArrayList<>();
//                        accountRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                    accountId.add(dataSnapshot.getKey());
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//
//                                db.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                            //String productId=dataSnapshot.getKey();
//                                            for(String id: accountId) {
//                                                if (dataSnapshot.getKey().equals(id)){
//                                                    Iterable<DataSnapshot> a=dataSnapshot.getChildren();
//                                                    for(DataSnapshot i: a){
//                                                        if(i.getKey().equals(product.getProductId())){
//                                                            db.child(id).child(product.getProductId()).removeValue();
//                                                        }
//
//                                                    }
//
//                                                }
//                                            }
//
//                                        }
//
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//                builder.show();
//            }
//        });
//    }
//
//    @NonNull
//    @Override
//    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cardview,parent,false);
//        return new myViewHolder(view);
//    }
//    class myViewHolder extends RecyclerView.ViewHolder{
//        ImageView productImg; TextView productNm;
//        ImageButton modify,delete;
//        CardView cardView;
//        public myViewHolder(@NonNull View itemView) {
//            super(itemView);
//            productImg=itemView.findViewById(R.id.productImage);
//            productNm=itemView.findViewById(R.id.productName);
//            modify=itemView.findViewById(R.id.modifyProduct);
//            delete=itemView.findViewById(R.id.deleteProduct);
//            cardView=itemView.findViewById(R.id.productCV);
//        }
//    }
//    void glide(ImageView pImg,String key){
//        DatabaseReference databaseReference=
//                FirebaseDatabase.getInstance().getReference().child("Products").child(key);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                image=snapshot.getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        Glide.with(context).load(image).into(pImg);  }
//}
//
//
//
