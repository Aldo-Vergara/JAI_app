package com.movil.jaiapp.ui.client.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.movil.jaiapp.R;
import com.movil.jaiapp.models.Product;
import com.movil.jaiapp.ui.member.list_available.ListProductsAdapter;

import java.util.List;


public class HomeProductsAdapter extends RecyclerView.Adapter<HomeProductsAdapter.HomeProductsViewHolder>{
    private List<Product> productList;
    private FragmentActivity activity;

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

    public HomeProductsAdapter(List<Product> productList, FragmentActivity activity) {
        this.productList = productList;
        this.activity = activity;
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
        viewHolder.textViewID.setText("$" + productList.get(i).getId());
        eventImgButtonFavorite(viewHolder, i);
    }

    public void loadImgView(HomeProductsViewHolder viewHolder, int i){
        Glide.with(activity).load(productList.get(i).getImage()).into(viewHolder.imgViewProduct);
    }

    private void eventImgButtonFavorite(final HomeProductsViewHolder viewHolder, final int i) {
        viewHolder.imgBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.imgBtnFavorite.setImageResource(R.drawable.icon_favorite);
            }
        });
    }
}


