package com.dopage.dolnad.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dopage.dolnad.Interfaces.OnItemClickListener;
import com.dopage.dolnad.Models.User;
import com.dopage.dolnad.R;

import java.util.ArrayList;
import java.util.List;

public class ClientsAdapter extends RecyclerView.Adapter<ClientViewHolder> {

    private List<User> clients = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public ClientsAdapter(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public void setClients(List<User> clientList){
        this.clients = clientList;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_client_layout, parent, false);
        return new ClientViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        User user = clients.get(position);
        holder.setClientProperties(user);

    }

    @Override
    public int getItemCount() {
        return clients != null ? clients.size(): 0;
    }

    public void add(User user){
        clients.add(user);
        notifyItemInserted(clients.size() - 1);
    }

    public void addAll(List<User> list){

        for(User user : list){

            add(user);
        }
    }

    public void remove(User user){

        int position = clients.indexOf(user);

        if(position > -1){

            clients.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear(){

        while(getItemCount() > 0){

            remove(getItem(0));
        }
    }

    public User getItem(int position){

        return clients.get(position);
    }
}
