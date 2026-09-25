package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private List<PantryItem> pantryItems;
    private Context context;

    public PantryAdapter(Context context, List<PantryItem> pantryItems) {
        this.context = context;
        this.pantryItems = pantryItems;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_pantry,
                        parent,
                        false
                );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder,
            int position) {

        PantryItem item = pantryItems.get(position);

        holder.tvName.setText(
                getIngredientEmoji(item.getName()) + " " + item.getName()
        );

        holder.tvQuantity.setText(
                item.getQuantity() + " " + item.getUnit()
        );

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "SmartPantrySettings",
                        Context.MODE_PRIVATE
                );

        boolean showExpiryDates =
                preferences.getBoolean(
                        "show_expiry_dates",
                        true
                );

        if (showExpiryDates) {

            holder.tvExpiry.setVisibility(View.VISIBLE);

            if (item.getExpiryDate() == null
                    || item.getExpiryDate().isEmpty()) {

                holder.tvExpiry.setText("No expiry date");

            } else {

                holder.tvExpiry.setText(
                        "Expiry: " + item.getExpiryDate()
                );
            }

        } else {

            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    AddEditIngredientActivity.class
            );

            intent.putExtra(
                    "ingredient_id",
                    item.getId()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    private String getIngredientEmoji(String name) {

        name = name.trim().toLowerCase();

        if (name.contains("egg")) {
            return "🥚";
        }

        if (name.contains("banana")) {
            return "🍌";
        }

        if (name.contains("tomato")) {
            return "🍅";
        }

        if (name.contains("milk")) {
            return "🥛";
        }

        if (name.contains("bread")) {
            return "🍞";
        }

        if (name.contains("cheese")) {
            return "🧀";
        }

        if (name.contains("peanut butter")) {
            return "🥜";
        }

        if (name.contains("butter")) {
            return "🧈";
        }

        if (name.contains("salt")) {
            return "🧂";
        }

        if (name.contains("sugar")) {
            return "🍬";
        }

        return "🥫";
    }

    public static class PantryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQuantity;
        TextView tvExpiry;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvItemName
            );

            tvQuantity = itemView.findViewById(
                    R.id.tvItemQuantity
            );

            tvExpiry = itemView.findViewById(
                    R.id.tvItemExpiry
            );
        }
    }
}