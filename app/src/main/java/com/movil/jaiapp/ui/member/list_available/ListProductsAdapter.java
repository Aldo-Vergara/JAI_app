package com.movil.jaiapp.ui.member.list_available;

import android.app.AlertDialog;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.Product;
import com.movil.jaiapp.models.UserMember;

import java.io.File;
import java.util.List;


public class ListProductsAdapter extends RecyclerView.Adapter<ListProductsAdapter.ListProductsViewHolder>{
    private List<Product> productList;
    private FragmentActivity activity;
    private DatabaseReference databaseReference;
    private UserMember userMember;
    private boolean isListAvailable;

    public static class ListProductsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgViewProduct;
        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewCost;
        public Switch statusSwitch;
        public ImageButton imgBtnDelete;

        public ListProductsViewHolder(View v) {
            super(v);
            imgViewProduct = v.findViewById(R.id.member_card_view_imgView_product);
            textViewName = v.findViewById(R.id.member_card_view_txtView_nameProduct);
            textViewDescription = v.findViewById(R.id.member_card_view_txtView_descProduct);
            textViewCost = v.findViewById(R.id.member_card_view_txtView_costProduct);
            statusSwitch = v.findViewById(R.id.member_card_view_switch_status);
            imgBtnDelete = v.findViewById(R.id.member_card_view_imgBtn_delete);
        }
    }

    public ListProductsAdapter(List<Product> productList, FragmentActivity activity, boolean isListAvailable,
                               DatabaseReference databaseReference, UserMember userMember) {
        this.productList = productList;
        this.activity = activity;
        this.isListAvailable = isListAvailable;
        this.databaseReference = databaseReference;
        this.userMember = userMember;
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
        viewHolder.textViewName.setText(productList.get(i).getName());
        viewHolder.textViewDescription.setText("Descripción" + productList.get(i).getDescription());
        viewHolder.textViewCost.setText("$" + productList.get(i).getCost());
        loadStatusProduct(viewHolder, i);
        eventImgButtonDelete(viewHolder, i);
    }

    public void loadImgView(ListProductsViewHolder viewHolder, int i){
        try{
            File photoFile = new File(productList.get(i).getImage());
            Uri uriFoto = FileProvider.getUriForFile(null, "com.movil.jaiapp.android.fileprovider", photoFile);
            viewHolder.imgViewProduct.setImageURI(uriFoto);
        }catch (Exception ex){
            Toast.makeText(null, "No se encontro la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadStatusProduct(final ListProductsViewHolder viewHolder, final int i) {
        if(productList.get(i).getStatus() == 1){
            viewHolder.statusSwitch.setChecked(true);
        }else{
            viewHolder.statusSwitch.setChecked(false);
        }
        viewHolder.statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    userMember.getProductsList().get(i).setStatus(1);
                }else{
                    userMember.getProductsList().get(i).setStatus(0);
                }

                databaseReference.child("UserMember").child(userMember.getId()).setValue(userMember).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }else{
                            mShowAlert("Error", "No se pudo cambiar el estatus");
                        }
                    }
                });
            }
        });
    }

    private void eventImgButtonDelete(ListProductsViewHolder viewHolder, int i) {
        if(isListAvailable) {
            viewHolder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.child("UserMember").child(userMember.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                mShowAlert("Error", "No se pudo eliminar el producto");
                            }
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
