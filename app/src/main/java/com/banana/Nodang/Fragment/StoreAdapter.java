package com.banana.Nodang.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.banana.Nodang.R;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    private Context context = null;
    private ArrayList<StoreNutr> foodList = null;
    private FoodViewListener foodViewListener = null;

    public StoreAdapter(ArrayList<StoreNutr> items, Context context, FoodViewListener listener){
        this.context = context;
        this.foodList = items;
        this.foodViewListener = listener;
    }
    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_item_list, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder holder, int i) {
        holder.foodNumView.setText("food"+(i+1));
        holder.productNameView.setText(foodList.get(i).getProductName());
        holder.cont1View.setText(foodList.get(i).getCont1());
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView foodNumView = null;
        public TextView productNameView = null;
        public TextView cont1View = null;

        public ViewHolder(View v){
            super(v);
            foodNumView = (TextView)v.findViewById(R.id.foodNum);
            productNameView = (TextView)v.findViewById(R.id.loadProductName);
            cont1View = (TextView)v.findViewById(R.id.loadcont1);
        }
        @Override
        public void onClick(View view) {
            foodViewListener.onItemClick(getAdapterPosition(), view);
        }
    }
}
