package com.dopage.dolnad.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dopage.dolnad.Interfaces.OnItemClickListener;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;

public class ClientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView textFullName;
    private TextView textMobile;
    private TextView textGender;
    private OnItemClickListener onItemClickListener;

    public ClientViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);

        textFullName = itemView.findViewById(R.id.textFullName);
        textMobile = itemView.findViewById(R.id.textPhone);
        textGender = itemView.findViewById(R.id.textGender);
        this.onItemClickListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(onItemClickListener != null)
            onItemClickListener.onItemClick(getAdapterPosition());
    }

    public void setClientProperties(User user){
        String fullName = user.getFirstName() + " " + user.getLastName();
        textFullName.setText(fullName);
        textMobile.setText(user.getPhone());

        if(user.getGender() == 6){
            textGender.setText("Male");
        }else if(user.getGender() == 7){
            textGender.setText("Female");
        }
    }

}
