package com.hydrabolt.foseditor;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class VaultOverviewActivity extends AppCompatActivity {

    VaultDecrypter currentVault;

    EditText vaultName, vaultWater, vaultPower, vaultFood, vaultStimpaks, vaultRadaways, vaultLunchboxes, vaultCaps, vaultHandies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int vaultNum = getIntent().getIntExtra("vaultNumber", 666);

        currentVault = VaultListActivity.getVault(vaultNum);

        getSupportActionBar().setTitle("Vault " + currentVault.getVaultName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vaultName = (EditText) findViewById(R.id.vaultNameConfig);
        vaultWater = (EditText) findViewById(R.id.waterConfig);
        vaultPower = (EditText) findViewById(R.id.powerConfig);
        vaultFood = (EditText) findViewById(R.id.foodConfig);
        vaultStimpaks = (EditText) findViewById(R.id.stimpakConfig);
        vaultRadaways = (EditText) findViewById(R.id.radawayConfig);
        vaultLunchboxes = (EditText) findViewById(R.id.lunchboxConfig);
        vaultCaps = (EditText) findViewById(R.id.capsConfig);
        vaultHandies = (EditText) findViewById(R.id.handiesConfig);


        setup();

        final View coordinatorLayoutView = findViewById(R.id.snackbarPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            save();
        }
        if (item.getItemId() == R.id.action_dwellers) {
            Snackbar.make(findViewById(R.id.snackbarPosition), "Not Yet Available!", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vault_overview, menu);
        return true;
    }

    public void save() {

        JSONObject vaultData = (JSONObject) currentVault.data.get("vault");
        JSONObject vaultStorage = (JSONObject) vaultData.get("storage");
        JSONObject vaultResources = (JSONObject) vaultStorage.get("resources");

        vaultData.put("VaultName", vaultName.getText().toString());
        vaultResources.put("Water", Double.parseDouble(vaultWater.getText().toString()));
        vaultResources.put("Energy", Double.parseDouble(vaultPower.getText().toString()));
        vaultResources.put("Food", Double.parseDouble(vaultFood.getText().toString()));
        vaultResources.put("StimPack", Double.parseDouble(vaultStimpaks.getText().toString()));
        vaultResources.put("RadAway", Double.parseDouble(vaultRadaways.getText().toString()));
        vaultResources.put("Nuka", Double.parseDouble(vaultCaps.getText().toString()));

        long lunchboxesAmount = Long.parseLong(vaultLunchboxes.getText().toString());
        long handiesAmount = Long.parseLong(vaultHandies.getText().toString());

        vaultData.put("LunchBoxesCount", lunchboxesAmount + handiesAmount);

        JSONArray LunchBoxesByType = new JSONArray();
        for(int i = 0; i < lunchboxesAmount; i++){
            LunchBoxesByType.add(0);
        }
        for(int i = 0; i < handiesAmount; i++){
            LunchBoxesByType.add(1);
        }

        vaultData.put("LunchBoxesByType", LunchBoxesByType);

        if(currentVault.saveChanges() == EncryptState.SUCCESS){
            Snackbar.make(findViewById(R.id.snackbarPosition), "Success!", Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(findViewById(R.id.snackbarPosition), "Error Occurred!", Snackbar.LENGTH_SHORT).show();
        }

    }

    public void setup() {
        try {

            vaultName.setText(currentVault.getVaultName());

            JSONObject vault = (JSONObject) currentVault.data.get("vault");
            JSONObject resources = (JSONObject) ((JSONObject) vault.get("storage")).get("resources");

            vaultWater.setText(Double.toString((double) resources.get("Water")));
            vaultPower.setText(Double.toString((double) resources.get("Energy")));
            vaultFood.setText(Double.toString((double) resources.get("Food")));
            vaultStimpaks.setText(Double.toString((double) resources.get("StimPack")));
            vaultRadaways.setText(Double.toString((double) resources.get("RadAway")));
            vaultCaps.setText(Double.toString((double) resources.get("Nuka")));

            JSONArray specials = (JSONArray) vault.get("LunchBoxesByType");

            int handyAmount = 0, lunchboxCount = 0;

            for(Object x : specials){
                long y = (long) x;
                if(y == 1){
                    handyAmount++;
                }else{
                    lunchboxCount++;
                }
            }

            vaultHandies.setText(Long.toString(handyAmount));
            vaultLunchboxes.setText(Long.toString(lunchboxCount));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
