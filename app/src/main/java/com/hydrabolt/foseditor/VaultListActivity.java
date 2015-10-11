package com.hydrabolt.foseditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.crypto.Cipher;

public class VaultListActivity extends AppCompatActivity {

    ProgressBar loadingBar;
    TextView noVaultsFoundText;
    LinearLayout buttonView;

    Button vault1Button, vault2Button, vault3Button;

    public static VaultDecrypter vault1, vault2, vault3;

    String vaultPrefix = "/storage/emulated/0/Android/data/com.bethsoft.falloutshelter/files/Vault";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);

        vault1Button = (Button) findViewById(R.id.vault1Button);
        vault2Button = (Button) findViewById(R.id.vault2Button);
        vault3Button = (Button) findViewById(R.id.vault3Button);

        vault1Button.setVisibility(View.GONE);
        vault2Button.setVisibility(View.GONE);
        vault3Button.setVisibility(View.GONE);

        buttonView = (LinearLayout) findViewById(R.id.vaultButtonList);

        noVaultsFoundText = (TextView) findViewById(R.id.vaultListStatus);

        buttonView.setVisibility(View.GONE);
        noVaultsFoundText.setVisibility(View.GONE);

        startUp();

    }

    public void startUp() {

        vault1 = new VaultDecrypter(vaultPrefix + "1.sav");
        vault2 = new VaultDecrypter(vaultPrefix + "2.sav");
        vault3 = new VaultDecrypter(vaultPrefix + "3.sav");

        int foundAmount = 0;

        if (vault1.state == DecryptState.SUCCESS) {
            vault1Button.setVisibility(View.VISIBLE);
            vault1Button.setText("Vault " + vault1.getVaultName() );
            foundAmount++;
        }
        if (vault2.state == DecryptState.SUCCESS) {
            vault2Button.setVisibility(View.VISIBLE);
            vault2Button.setText("Vault " + vault2.getVaultName());
            foundAmount++;
        }
        if (vault3.state == DecryptState.SUCCESS) {
            vault3Button.setVisibility(View.VISIBLE);
            vault3Button.setText("Vault " + vault3.getVaultName());
            foundAmount++;
        }

        loadingBar.setVisibility(View.GONE);

        if (foundAmount == 0) {
            noVaultsFoundText.setVisibility(View.VISIBLE);
        } else {
            buttonView.setVisibility(View.VISIBLE);
        }

    }

    public void openVault1(View v){
        openVault(1);
    }

    public void openVault2(View v){
        openVault(2);
    }

    public void openVault3(View v){
        openVault(3);
    }

    public void openVault(int n){
        Intent intent = new Intent(VaultListActivity.this, VaultOverviewActivity.class);

        intent.putExtra("vaultNumber", n);

        VaultListActivity.this.startActivity(intent);
    }

    public static VaultDecrypter getVault(int num){
        switch(num){
            case 1:
                return vault1;
            case 2:
                return vault2;
            default:
                return vault3;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vault_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openfile(View v) {


        String vaultPrefix = "/storage/emulated/0/Android/data/com.bethsoft.falloutshelter/files/Vault";

        VaultDecrypter decrypter = new VaultDecrypter(vaultPrefix + "1.sav");
        VaultDecrypter decrypter2 = new VaultDecrypter(vaultPrefix + "2.sav");
        VaultDecrypter decrypter3 = new VaultDecrypter(vaultPrefix + "3.sav");

        JSONParser parser = new JSONParser();

        ((JSONObject) decrypter.data.get("vault")).get("dwellers");

        Log.d("foseditor-decrypt", "result when loading vault 1 " + resultify(decrypter));
        Log.d("foseditor-decrypt", "result when loading vault 2 " + resultify(decrypter2));
        Log.d("foseditor-decrypt", "result when loading vault 3 " + resultify(decrypter3));

    }

    public String resultify(VaultDecrypter vault) {

        switch (vault.state) {
            case WAITING:
                return "WAITING";
            case ERROR:
                return "ERROR";
            case SUCCESS:
                return "SUCCESS";
            case FILE_NOT_FOUND:
                return "FILE_NOT_FOUND";
        }

        return "UNKNOWN";

    }
}
