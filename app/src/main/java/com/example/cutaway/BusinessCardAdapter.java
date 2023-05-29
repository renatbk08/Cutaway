package com.example.cutaway;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class BusinessCardAdapter extends RecyclerView.Adapter<BusinessCardAdapter.BusinessCardViewHolder> {
    private ArrayList<BusinessCard> mCards;
    private final LayoutInflater mInflater;
    private final Activity mActivity;
    private BusinessCardAdapterCallback callback;

    public BusinessCardAdapter(Activity activity) {
        mActivity = activity;
        mCards = readBusinessCardsFromJson();
        mInflater = LayoutInflater.from(mActivity);
    }

    @NonNull
    @Override
    public BusinessCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.cardview, parent, false);
        return new BusinessCardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessCardViewHolder holder, int position) {
        BusinessCard card = mCards.get(position);
        holder.bindData(card);
        holder.deleteButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(holder.itemView.getRootView().getContext());
            dialog.setContentView(R.layout.delete_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Button okButton = dialog.findViewById(R.id.ok_button);
            Button cancelButton = dialog.findViewById(R.id.cancel_button);
            okButton.setOnClickListener(v1 -> {
                dialog.dismiss();
                if (position != RecyclerView.NO_POSITION) {
                    removeBusinessCard(position);
                    saveBusinessCards();
                    if (callback != null) {
                        callback.onCardRemoved(position);
                    }
                }
            });
            cancelButton.setOnClickListener(v2 -> dialog.dismiss());
            dialog.show();
        });
        holder.refactButton.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, RefactorBusinessCardActivity.class);
            intent.putExtra("position", position);
            mActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    private ArrayList<BusinessCard> readBusinessCardsFromJson() {
        Gson gson = new GsonBuilder().create();
        ArrayList<BusinessCard> businessCards = new ArrayList<>();
        try (FileReader reader = new FileReader("business_cards.json")) {
            Type listType = new TypeToken<ArrayList<BusinessCard>>() {}.getType();
            businessCards = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return businessCards;
    }
    private void saveBusinessCards() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("business_cards.json", false)) {
            gson.toJson(mCards, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeBusinessCard(int position) {
        if (mCards.size() <= 1) {
            mCards = new ArrayList<>();
        } else {
            mCards.remove(position);
            notifyItemRemoved(position);
        }
    }

    private String convertListToJson(ArrayList<BusinessCard> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    public interface BusinessCardAdapterCallback {
        void onCardRemoved(int position);
    }

    public void setCallback(BusinessCardAdapterCallback callback) {
        this.callback = callback;
    }
    static class BusinessCardViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView, lastNameTextView, companyTextView, phoneTextView, emailTextView;
        RelativeLayout frontSide;
        LinearLayout backSide;
        ImageButton deleteButton, refactButton;
        ImageView mImageView;
        ViewFlipper viewFlipper;
        BusinessCardViewHolder(View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.first_name_text_view);
            lastNameTextView = itemView.findViewById(R.id.last_name_text_view);
            companyTextView = itemView.findViewById(R.id.company_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
            emailTextView = itemView.findViewById(R.id.email_text_view);
            frontSide = itemView.findViewById(R.id.front_side);
            backSide = itemView.findViewById(R.id.back_side);
            deleteButton = itemView.findViewById(R.id.delete_button);
            refactButton = itemView.findViewById(R.id.refactor_button);
            mImageView = itemView.findViewById(R.id.mImageView);
            viewFlipper = itemView.findViewById(R.id.viewflipper);
            viewFlipper.setInAnimation(itemView.getContext(), android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(itemView.getContext(), android.R.anim.slide_out_right);
            itemView.setOnClickListener(v -> viewFlipper.showNext());
        }

        void bindData(BusinessCard card) {
            firstNameTextView.setText(card.getFirstName());
            lastNameTextView.setText(card.getLastName());
            companyTextView.setText(card.getCompany());
            phoneTextView.setText(card.getPhone());
            emailTextView.setText(card.getEmail());
            if(card.getEncoded() != null) {
                byte[] decodedBytes = Base64.decode(card.getEncoded(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                mImageView.setImageBitmap(decodedBitmap);
            } else {
                mImageView.setImageResource(R.drawable.ic_none);
            }
        }
    }
    public void addBusinessCard(BusinessCard card) {
        mCards.add(card);
        notifyItemInserted(mCards.size() - 1);
        saveBusinessCards();
    }
}
