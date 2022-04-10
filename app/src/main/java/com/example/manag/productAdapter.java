package com.example.manag;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class productAdapter  extends FirebaseRecyclerAdapter<Product,productAdapter.myViewHolder>{


    public productAdapter(@NonNull FirebaseRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int postion, @NonNull Product product) {
        holder.productNm.setText(product.getName());
        Glide.with(holder.productImg.getContext()).load(product.getImage()).into(holder.productImg);
        holder.modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus =
                        DialogPlus.newDialog(holder.productImg.getContext())
                                .setContentHolder(new ViewHolder(R.layout.dialog_content))
                                .setExpanded(true, 1100).create();
                View myView = dialogPlus.getHolderView();
                EditText pName = myView.findViewById(R.id.productName);
                EditText pPrice = myView.findViewById(R.id.productPrice);
                EditText pAmount = myView.findViewById(R.id.productAmount);
                EditText purl = myView.findViewById(R.id.productUrl);
                Spinner pSection =myView.findViewById(R.id.spinnerSec);
                Button modifyBut = myView.findViewById(R.id.productModify);
                pName.setText(product.getName());
                pPrice.setText(product.getPrice());
                pAmount.setText(product.getAmount());
                purl.setText(product.getImage());


                dialogPlus.show();
                modifyBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", pName.getText().toString());
                        map.put("price", pPrice.getText().toString());
                        map.put("amount", pAmount.getText().toString());
                        map.put("image", purl.getText().toString());
//                        map.put("section", pSection.getResources().getStringArray(R.array.Section));
                        FirebaseDatabase.getInstance().getReference().child("Products").child(getRef(postion).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });

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

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            productImg=(ImageView) itemView.findViewById(R.id.productImage);
            productNm=(TextView) itemView.findViewById(R.id.productName);
            modify=(ImageButton) itemView.findViewById(R.id.modifyProduct);
            delete=(ImageButton) itemView.findViewById(R.id.deleteProduct);
        }
    }
}
