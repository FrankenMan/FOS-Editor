package com.hydrabolt.foseditor;

import android.content.Intent;
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

    static int vault;

    EditText vaultName, vaultWater, vaultPower, vaultFood, vaultStimpaks, vaultRadaways, vaultLunchboxes, vaultCaps, vaultHandies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int vaultNum = getIntent().getIntExtra("vaultNumber", -2838);

        if(vaultNum == -2838)
            vaultNum = VaultListActivity.currentVault;

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

            Intent intent = new Intent(getApplicationContext(), VaultDwellersActivity.class);

            intent.putExtra("vaultNumber", 1);

            VaultOverviewActivity.this.startActivity(intent);
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

        if(vaultName.getText().toString().matches("")) vaultName.setText("123");
        if(vaultWater.getText().toString().matches("")) vaultWater.setText("2500");
        if(vaultPower.getText().toString().matches("")) vaultPower.setText("2500");
        if(vaultFood.getText().toString().matches("")) vaultFood.setText("2500");
        if(vaultStimpaks.getText().toString().matches("")) vaultStimpaks.setText("500");
        if(vaultRadaways.getText().toString().matches("")) vaultRadaways.setText("500");
        if(vaultCaps.getText().toString().matches("")) vaultCaps.setText("2500000");
        if(vaultLunchboxes.getText().toString().matches("")) vaultLunchboxes.setText("500");
        if(vaultHandies.getText().toString().matches("")) vaultHandies.setText("500");

        long lunchboxesAmount = Long.parseLong(vaultLunchboxes.getText().toString());
        long handiesAmount = Long.parseLong(vaultHandies.getText().toString());

        if(lunchboxesAmount + handiesAmount > 20000){
            Snackbar.make(findViewById(R.id.snackbarPosition), "Crash expected!", Snackbar.LENGTH_SHORT).show();
        }

        vaultData.put("VaultName", vaultName.getText().toString());
        vaultResources.put("Water", Double.parseDouble(vaultWater.getText().toString()));
        vaultResources.put("Energy", Double.parseDouble(vaultPower.getText().toString()));
        vaultResources.put("Food", Double.parseDouble(vaultFood.getText().toString()));
        vaultResources.put("StimPack", Double.parseDouble(vaultStimpaks.getText().toString()));
        vaultResources.put("RadAway", Double.parseDouble(vaultRadaways.getText().toString()));
        vaultResources.put("Nuka", Double.parseDouble(vaultCaps.getText().toString()));

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

            long handyAmount = 0, lunchboxCount = 0;

            for(Object x : specials){
                long y = resolveNumber(x);
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

    long resolveNumber(Object o){
        if(o instanceof Integer){
            return Long.valueOf((int) ((Integer) o).longValue());
        }else if(o instanceof Long){
            return ((Long) o).longValue();
        }
        return 123;
    }

}
