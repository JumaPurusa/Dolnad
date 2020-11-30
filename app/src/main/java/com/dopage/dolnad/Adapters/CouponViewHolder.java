package com.dopage.dolnad.Adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dopage.dolnad.Interfaces.OnItemClickListener;
import com.dopage.dolnad.Models.Coupon;
import com.dopage.dolnad.R;
import com.dopage.dolnad.Utils.StringManipulation;

public class CouponViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView textLinkageCode, textLinker, textCreatedAt;
    private View isLinkedView, isNotLinkedView;
    private OnItemClickListener onItemClickListener;

    public CouponViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);

        textLinkageCode = itemView.findViewById(R.id.textLinkageCode);
        textLinker = itemView.findViewById(R.id.textLinker);
        textCreatedAt = itemView.findViewById(R.id.textCreatedAt);
        isLinkedView = itemView.findViewById(R.id.isLinkedView);
        isNotLinkedView = itemView.findViewById(R.id.isNotLinkedView);

        this.onItemClickListener = listener;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(onItemClickListener != null) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public void setProperties(Coupon coupon){

        if(coupon != null){
            textLinkageCode.setText(coupon.getLinkage_code());
            textLinker.setText("Linker: " + coupon.getLinker());
            textCreatedAt.setText(coupon.getCreatedAt());
            if(coupon.getIsLinked() == 1) {
                isLinkedView.setVisibility(View.VISIBLE);
                isNotLinkedView.setVisibility(View.GONE);
            }else{
                isLinkedView.setVisibility(View.GONE);
                isNotLinkedView.setVisibility(View.VISIBLE);
            }
        }

    }
}
