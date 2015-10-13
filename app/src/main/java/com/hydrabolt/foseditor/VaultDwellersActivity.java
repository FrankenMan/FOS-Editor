package com.hydrabolt.foseditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VaultDwellersActivity extends AppCompatActivity {

    private List<Dweller> dwellers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_dwellers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        VaultDecrypter vault = VaultListActivity.getVault(VaultListActivity.currentVault);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        dwellers = new ArrayList<Dweller>();

        for(Object rawDweller : (JSONArray) ((JSONObject) vault.data.get("dwellers")).get("dwellers")){

            JSONObject dweller = (JSONObject) rawDweller;

            dwellers.add(new Dweller(
                    (String) dweller.get("name") + " " + (String) dweller.get("lastName"), // their name
                    (long) ((JSONObject) dweller.get("experience")).get("currentLevel"), // current level
                    (boolean) ((long) dweller.get("gender") == 2) // gender
            ));

        }

        getSupportActionBar().setTitle(dwellers.size() + " Dwellers");

        RVAdapter adapter = new RVAdapter(dwellers, this);
        rv.setAdapter(adapter);


    }

}

class Dweller {

    String name;
    long level;
    boolean isMale;

    public Dweller(String name, long level, boolean isMale) {
        this.name = name;
        this.level = level;
        this.isMale = isMale;
    }

}

