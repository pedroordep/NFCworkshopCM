package com.its.workshopnfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class WriteApplicationActivity extends AppCompatActivity {
    NFC_Controller nfc_controller;
    NfcAdapter nfcAdapter = null;
    Context context;
    ArrayList<String> itemloc, itemname;
    ArrayList<Drawable> imgid;
    String itemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_application);

        nfc_controller = new NFC_Controller();
        context = getApplicationContext();

        itemloc = new ArrayList<String>();
        imgid = new ArrayList<Drawable>();
        itemname = new ArrayList<String>();

        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if(getPackageManager().getLaunchIntentForPackage(packageInfo.packageName) != null){
                itemloc.add(packageInfo.packageName);
                imgid.add(getPackageManager().getApplicationIcon(packageInfo));
                itemname.add(getPackageManager().getApplicationLabel(packageInfo).toString());
            }
        }
        CustomListAdapter adapter = new CustomListAdapter(this, itemname, imgid, itemloc);
        ListView list=(ListView)findViewById(R.id.listApps);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nfcAdapter = NfcAdapter.getDefaultAdapter(context);
                if(!nfcAdapter.isEnabled()){
                    // NFC is disabled, show the settings UI
                    // to enable NFC
                    Toast.makeText(getApplicationContext(), "Please enable NFC.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
                itemSelected = itemloc.get(position);
                enableForegroundDispatchSystem();
                Toast.makeText(context, "Pass near a tag to write the app", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(nfcAdapter!=null) enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(nfcAdapter!=null) disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(itemSelected!=null) {
            super.onNewIntent(intent);

            if(intent.hasExtra(nfcAdapter.EXTRA_TAG)){
                //Toast.makeText(this, "nfcIntent", Toast.LENGTH_SHORT).show();

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefMessage ndefMessage = nfc_controller.createNdefApplication(itemSelected);

                nfc_controller.writeNdefMessage(tag, ndefMessage, this);
            }

            itemSelected = null;
            nfcAdapter = null;
        }
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this, WriteApplicationActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_application, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
