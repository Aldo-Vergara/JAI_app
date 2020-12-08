package com.movil.jaiapp.ui.client.wish_list;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.List;


public class WishProductsAdapter extends RecyclerView.Adapter<WishProductsAdapter.WishProductsViewHolder>{
    private List<Product> productList;
    private FragmentActivity activity;
    private DatabaseReference databaseReference;
    private UserClient userClient;
    private ProgressDialog progressDialog;

    public static class WishProductsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgViewProduct;
        public TextView textViewID;
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewCost;
        public ImageButton imgBtnDelete;

        public WishProductsViewHolder(View v) {
            super(v);
            imgViewProduct = v.findViewById(R.id.client_cardView_imgView_wishProduct);
            textViewID = v.findViewById(R.id.client_cardView_imgView_wishIDProduct);
            textViewName = v.findViewById(R.id.client_cardView_imgView_wishNameProduct);
            textViewDescription = v.findViewById(R.id.client_cardView_imgView_wishDescProduct);
            textViewCost = v.findViewById(R.id.client_cardView_imgView_wishCostProduct);
            imgBtnDelete = v.findViewById(R.id.client_cardView_imgBtn_wishDelete);
        }
    }

    public WishProductsAdapter(List<Product> productList, FragmentActivity activity,
                               DatabaseReference databaseReference, UserClient userClient, ProgressDialog progressDialog) {
        this.productList = productList;
        this.activity = activity;
        this.databaseReference = databaseReference;
        this.userClient = userClient;
        this.progressDialog = progressDialog;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public WishProductsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.client_card_view_wish_products, viewGroup, false);
        return new WishProductsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WishProductsViewHolder viewHolder, final int i) {
        loadImgView(viewHolder, i);
        viewHolder.textViewID.setText(productList.get(i).getId());
        viewHolder.textViewName.setText(productList.get(i).getName());
        viewHolder.textViewDescription.setText("Descripción: " + productList.get(i).getDescription());
        viewHolder.textViewCost.setText("$" + productList.get(i).getCost());
        eventImgButtonDelete(viewHolder, i);
    }

    public void loadImgView(WishProductsViewHolder viewHolder, int i){
        Glide.with(activity).load(productList.get(i).getImage()).into(viewHolder.imgViewProduct);
    }

    private void eventImgButtonDelete(WishProductsViewHolder viewHolder, final int i) {
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Actualizando...");
        progressDialog.show();

        viewHolder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("UserClient").child(userClient.getId()).child("wishProductsList").child(String.valueOf(i+1)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }else{
                            mShowAlert("Error", "No se pudo eliminar el producto");
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void mShowAlert(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }
}
