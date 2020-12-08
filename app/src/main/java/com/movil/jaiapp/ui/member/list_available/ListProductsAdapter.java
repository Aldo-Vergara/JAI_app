package com.movil.jaiapp.ui.member.list_available;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.Product;
import com.movil.jaiapp.models.UserMember;

import java.util.ArrayList;
import java.util.List;


public class ListProductsAdapter extends RecyclerView.Adapter<ListProductsAdapter.ListProductsViewHolder>{
    private List<Product> productList;
    private FragmentActivity activity;
    private DatabaseReference databaseReference;
    private UserMember userMember;
    private boolean isListAvailable;
    private ProgressDialog progressDialog;

    public static class ListProductsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgViewProduct;
        public TextView textViewID;
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewCost;
        public Switch statusSwitch;
        public ImageButton imgBtnDelete;

        public ListProductsViewHolder(View v) {
            super(v);
            imgViewProduct = v.findViewById(R.id.member_card_view_imgView_product);
            textViewID = v.findViewById(R.id.member_card_view_txtView_IDProduct);
            textViewName = v.findViewById(R.id.member_card_view_txtView_nameProduct);
            textViewDescription = v.findViewById(R.id.member_card_view_txtView_descProduct);
            textViewCost = v.findViewById(R.id.member_card_view_txtView_costProduct);
            statusSwitch = v.findViewById(R.id.member_card_view_switch_status);
            imgBtnDelete = v.findViewById(R.id.member_card_view_imgBtn_delete);
        }
    }

    public ListProductsAdapter(List<Product> productList, FragmentActivity activity, boolean isListAvailable,
                               DatabaseReference databaseReference, UserMember userMember, ProgressDialog progressDialog) {
        this.productList = productList;
        this.activity = activity;
        this.isListAvailable = isListAvailable;
        this.databaseReference = databaseReference;
        this.userMember = userMember;
        this.progressDialog = progressDialog;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public ListProductsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_card_view, viewGroup, false);
        return new ListProductsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ListProductsViewHolder viewHolder, final int i) {
        loadImgView(viewHolder, i);
        viewHolder.textViewID.setText(productList.get(i).getId());
        viewHolder.textViewName.setText(productList.get(i).getName());
        viewHolder.textViewDescription.setText("Descripción: " + productList.get(i).getDescription());
        viewHolder.textViewCost.setText("$" + productList.get(i).getCost());
        loadStatusProduct(viewHolder, i);
        eventImgButtonDelete(viewHolder, i);
    }

    public void loadImgView(ListProductsViewHolder viewHolder, int i){
        Glide.with(activity).load(productList.get(i).getImage()).into(viewHolder.imgViewProduct);
    }

    private void loadStatusProduct(final ListProductsViewHolder viewHolder, final int i) {
        if(productList.get(i).getStatus() == 1){
            viewHolder.statusSwitch.setChecked(true);
        }else{
            viewHolder.statusSwitch.setChecked(false);
        }
        final ArrayList<Product> products = (ArrayList<Product>) userMember.getProductsList();

        viewHolder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean band = false;
                for(int k = 0; k < products.size(); k++){
                    if(userMember.getProductsList().get(k).getId().equals(productList.get(i).getId()) &&
                            products.get(k).getCreated() != null){
                        if(b){
                            userMember.getProductsList().get(k).setStatus(1);
                        }else{
                            userMember.getProductsList().get(k).setStatus(0);
                        }
                        band = true;
                        break;
                    }else{
                        band = false;
                    }
                }

                if(band){
                    databaseReference.child("UserMember").child(userMember.getId()).setValue(userMember).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(activity, "Cambio el estado del producto", Toast.LENGTH_SHORT).show();
                            }else{
                                mShowAlert("Error", "No se pudo cambiar el estatus");
                            }
                        }
                    });
                }
            }
        });
    }

    private void eventImgButtonDelete(ListProductsViewHolder viewHolder, final int i) {
        if(isListAvailable) {
            viewHolder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setIcon(R.mipmap.ic_launcher);
                    progressDialog.setMessage("Borrando...");
                    progressDialog.show();

                    databaseReference.child("UserMember").child(userMember.getId()).child("productsList").child(String.valueOf(i)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(activity, "Se borro su producto", Toast.LENGTH_SHORT).show();
                            }else{
                                mShowAlert("Error", "No se pudo eliminar el producto");
                            }
                            progressDialog.dismiss();
                        }
                    });


                }
            });
        }else{
            viewHolder.imgBtnDelete.setVisibility(View.INVISIBLE);
        }
    }

    private void mShowAlert(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton("Aceptar", null);
        dialog.show();
    }
}
