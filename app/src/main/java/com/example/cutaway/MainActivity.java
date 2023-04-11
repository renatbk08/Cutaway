package com.example.cutaway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private RecyclerView recyclerView;

    private ArrayList<BusinessCard> businessCards;
    private TextView noCardsTextView;
    private FloatingActionButton addButton;
    private BusinessCardAdapter adapter;
    private SnapHelper snapHelper;
    private static final int ADD_BUSINESS_CARD_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        recyclerView = findViewById(R.id.recycler_view);
        noCardsTextView = findViewById(R.id.no_cards_text_view);
        addButton = findViewById(R.id.add_button);
        snapHelper = new LinearSnapHelper();
        // Initialize the list of business cards
        businessCards = new ArrayList<>();
        // Retrieve the list of business cards from SharedPreferences
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("businessCards", "");
        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<BusinessCard>>() {}.getType();
            businessCards = gson.fromJson(json, type);
        } else {
            businessCards = new ArrayList<>();
        }
        adapter = new BusinessCardAdapter(mContext, businessCards);
        if (businessCards.isEmpty()) {
            noCardsTextView.setVisibility(View.VISIBLE);
        } else {
            noCardsTextView.setVisibility(View.GONE);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                // Set the adapter for the RecyclerView
                recyclerView.setAdapter(new BusinessCardAdapter(mContext, businessCards));
                snapHelper.attachToRecyclerView(recyclerView);
            }
        }
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddBusinessCardActivity.class);
            intent.putParcelableArrayListExtra("businessCards", businessCards);
            startActivityForResult(intent, ADD_BUSINESS_CARD_REQUEST);
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_BUSINESS_CARD_REQUEST && resultCode == RESULT_OK && data != null) {
            BusinessCard newBusinessCard = data.getParcelableExtra("newBusinessCard");
            businessCards.add(newBusinessCard);
            adapter.notifyDataSetChanged();
            if (businessCards.isEmpty()) {
                noCardsTextView.setVisibility(View.VISIBLE);
            } else {
                noCardsTextView.setVisibility(View.GONE);
                if (recyclerView != null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    // Set the adapter for the RecyclerView
                    recyclerView.setAdapter(new BusinessCardAdapter(mContext, businessCards));
                    snapHelper.attachToRecyclerView(recyclerView);
                }
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // clear the preferences before adding new information
        String json = convertListToJson(businessCards);
        editor.putString("businessCards", json);
        editor.apply(); // apply the changes to SharedPreferences
    }
    private String convertListToJson(ArrayList<BusinessCard> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
