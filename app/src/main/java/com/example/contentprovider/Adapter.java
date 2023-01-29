package com.example.contentprovider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contentprovider.databinding.ItemBinding;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ContactsViewHolder> {
    ArrayList<Contacts> contacts;
    Context context;
    MyListener listener ;

    public Adapter(Context context, ArrayList<Contacts> contacts,MyListener listener) {
        this.context = context;
        this.contacts = contacts;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBinding binding = ItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new ContactsViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        int pos = position;

        final Contacts c = contacts.get(pos);
        holder.name.setText(c.getName());
        holder.number.setText(c.getNumber());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.click(holder.getAdapterPosition());
            }
        });
    }
    @Override
    public int getItemCount() {
        return contacts.size();
    }
    class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        public ContactsViewHolder(ItemBinding binding) {
            super(binding.getRoot());
            name = binding.Name;
            number = binding.Number;
        }
    }
}

