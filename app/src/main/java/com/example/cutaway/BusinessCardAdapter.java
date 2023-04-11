package com.example.cutaway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.BusinessCardViewHolder> {
    private List<BusinessCard> mCards;
    private Context mContext;

    public BusinessCardAdapter(Context context, List<BusinessCard> cards) {
        mCards = cards;
        mContext = context;
    }

    @NonNull
    @Override
    public BusinessCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        return new BusinessCardViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BusinessCardViewHolder holder, int position) {
        BusinessCard currentCard = mCards.get(position);
        if (currentCard.getImage() != null) {
            holder.mImageView.setImageBitmap(currentCard.getImage());
            holder.mImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mImageView.setImageBitmap(null);
            holder.mImageView.setVisibility(View.GONE);
        }
        try {
            holder.firstNameTextView.setText("Name: " + currentCard.getFirstName());
            holder.lastNameTextView.setText("Last name: " + currentCard.getLastName());
            holder.companyTextView.setText("Company: " + currentCard.getCompany());
            holder.phoneTextView.setText(currentCard.getPhone());
            holder.emailTextView.setText(currentCard.getEmail());
            holder.socialNetworksTextView.setText(currentCard.getSocialNetworks().toString());
        } catch(NullPointerException exc) {
            // create a Toast message with a short duration
            Toast.makeText(mContext, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Set the LayoutParams to have match_parent width
        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Add click listener to delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the removeCard() method with the adapter position of the card
                removeCard(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public void removeCard(int position) {
        mCards.remove(position);
        notifyItemRemoved(position);
    }

    public class BusinessCardViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView firstNameTextView;
        public TextView lastNameTextView;
        public TextView companyTextView;
        public TextView phoneTextView;
        public TextView emailTextView;
        public TextView socialNetworksTextView;
        public ImageButton deleteButton;

        public BusinessCardViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.mImageView);
            firstNameTextView = view.findViewById(R.id.first_name_text_view);
            lastNameTextView = view.findViewById(R.id.last_name_text_view);
            companyTextView = view.findViewById(R.id.company_text_view);
            phoneTextView = view.findViewById(R.id.phone_text_view);
            emailTextView = view.findViewById(R.id.email_text_view);
            socialNetworksTextView = view.findViewById(R.id.social_networks_text_view);
            deleteButton = view.findViewById(R.id.delete_button);
        }
    }
}
