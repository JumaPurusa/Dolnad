package com.dopage.dolnad.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dopage.dolnad.Interfaces.OnItemClickListener;
import com.dopage.dolnad.Models.Coupon;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;

import java.util.ArrayList;
import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponViewHolder> {

    private List<Coupon> coupons = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public CouponsAdapter(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_coupon_layout, parent, false);
        return new CouponViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        holder.setProperties(coupon);
    }

    @Override
    public int getItemCount() {
        return coupons != null ? coupons.size() : 0;
    }


    public void add(Coupon coupon){
        coupons.add(coupon);
        notifyItemInserted(coupons.size() - 1);
    }

    public void addAll(List<Coupon> list){

        for(Coupon coupon : list){

            add(coupon);
        }
    }

    public void remove(Coupon coupon){

        int position = coupons.indexOf(coupon);

        if(position > -1){

            coupons.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear(){

        while(getItemCount() > 0){

            remove(getItem(0));
        }
    }

    public Coupon getItem(int position){

        return coupons.get(position);
    }
}
