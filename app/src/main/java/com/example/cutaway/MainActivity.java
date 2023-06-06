package com.example.cutaway;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity implements BusinessCardAdapter.BusinessCardAdapterCallback {
    private RecyclerView recyclerView;
    private ArrayList<BusinessCard> businessCards = new ArrayList<>();
    private TextView noCardsTextView;
    private BusinessCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noCardsTextView = findViewById(R.id.no_cards_text_view);
        FloatingActionButton addButton = findViewById(R.id.add_button);
        businessCards = readBusinessCardsFromJson();
        adapter = new BusinessCardAdapter(this);
        adapter.setCallback(this);

        if (businessCards.isEmpty()) {
            noCardsTextView.setVisibility(View.VISIBLE);
        } else {
            noCardsTextView.setVisibility(View.GONE);
            setupRecyclerView();
        }

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddBusinessCardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    public void writeBusinessCardsToJson(ArrayList<BusinessCard> businessCards) {
        Gson gson = new Gson();
        String json = gson.toJson(businessCards);
        try {
            FileOutputStream fileOutput = openFileOutput("business_card.txt", MODE_PRIVATE);
            fileOutput.write(json.getBytes());
            fileOutput.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<BusinessCard> readBusinessCardsFromJson() {
        ArrayList<BusinessCard> businessCards = new ArrayList<>();
        try {
            FileInputStream fileInput = openFileInput("business_card.txt");
            int size = fileInput.available();
            byte[] buffer = new byte[size];
            fileInput.read(buffer);
            fileInput.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type businessCardListType = new TypeToken<ArrayList<BusinessCard>>() {}.getType();
            businessCards = gson.fromJson(json, businessCardListType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return businessCards;
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCardRemoved(int position) {
        if (position >= 0) {
            businessCards.remove(position);
        } else {
            businessCards.clear();
        }
        /*saveBusinessCards*/writeBusinessCardsToJson(businessCards);

        if (businessCards.isEmpty()) {
            noCardsTextView.setVisibility(View.VISIBLE);
        } else {
            noCardsTextView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }
}



