package com.movil.jaiapp.ui.client.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.Product;
import com.movil.jaiapp.models.UserClient;
import com.movil.jaiapp.models.UserMember;

import java.util.ArrayList;
import java.util.List;

public class HomeProductsAdapter extends RecyclerView.Adapter<HomeProductsAdapter.HomeProductsViewHolder>{
    private List<Product> productList;
    private FragmentActivity activity;
    private DatabaseReference databaseReference;
    private UserClient userClient;
    private UserMember userMember;

    public static class HomeProductsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgViewProduct;
        public TextView textViewName;
        public TextView textViewCost;
        public TextView textViewID;
        public ImageButton imgBtnFavorite;

        public HomeProductsViewHolder(View v) {
            super(v);
            imgViewProduct = v.findViewById(R.id.client_cardView_imgView_imgProduct);
            textViewName = v.findViewById(R.id.client_cardView_txtView_nameProduct);
            textViewCost = v.findViewById(R.id.client_cardView_txtView_costProduct);
            textViewID = v.findViewById(R.id.client_cardView_txtView_IDProduct);
            imgBtnFavorite = v.findViewById(R.id.client_cardView_imgBtn_favorite);
        }
    }

    public HomeProductsAdapter(List<Product> productList, FragmentActivity activity,
                               DatabaseReference databaseReference, UserClient userClient, UserMember userMember) {
        this.productList = productList;
        this.activity = activity;
        this.databaseReference = databaseReference;
        this.userClient = userClient;
        this.userMember = userMember;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public HomeProductsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.client_card_view_products, viewGroup, false);
        return new HomeProductsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final HomeProductsViewHolder viewHolder, final int i) {
        loadImgView(viewHolder, i);
        viewHolder.textViewName.setText(productList.get(i).getName());
        viewHolder.textViewCost.setText("$" + productList.get(i).getCost());
        viewHolder.textViewID.setText("ID: " + productList.get(i).getId());
        eventImgButtonFavorite(viewHolder, i);
    }

    public void loadImgView(HomeProductsViewHolder viewHolder, int i){
        Glide.with(activity).load(productList.get(i).getImage()).into(viewHolder.imgViewProduct);
    }

    private void eventImgButtonFavorite(final HomeProductsViewHolder viewHolder, final int i) {
        if(productList.get(i).getFavorite() == 0){
            viewHolder.imgBtnFavorite.setImageResource(R.drawable.icon_not_favorite);
        }else{
            viewHolder.imgBtnFavorite.setImageResource(R.drawable.icon_favorite);
        }

        viewHolder.imgBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(productList.get(i).getFavorite() == 0){
                    productList.get(i).setFavorite(1);
                }else{
                    productList.get(i).setFavorite(0);
                }
                if(productList.get(i).getFavorite() == 1){
                    userClient.getWishProductsList().add(productList.get(i));
                    databaseReference.child("UserClient").child(userClient.getId()).setValue(userClient).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(activity, "Se agrego a la lista de deseos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    userClient.getWishProductsList().remove(productList.get(i));
                    databaseReference.child("UserClient").child(userClient.getId()).setValue(userClient).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(activity, "Se quito de la lista de deseos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                userMember.setProductsList(productList);
                databaseReference.child("UserMember").child(userMember.getId()).setValue(userMember).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            activity.finish();
                        }
                    }
                });
            }
        });
    }
}


