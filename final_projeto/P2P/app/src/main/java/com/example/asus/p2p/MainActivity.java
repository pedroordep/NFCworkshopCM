package com.example.asus.p2p;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();

            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
        // Android 4.1(JELLY_BEAN) or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Toast.makeText(this, "Android Beam is not supported.", Toast.LENGTH_SHORT).show();

            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "NFC is supported on your device.",
                    Toast.LENGTH_SHORT).show();

            TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            if (mPhoneNumber.isEmpty())
                mPhoneNumber = "999999999";
            EditText editText = (EditText) findViewById(R.id.editText);
            editText.setText(mPhoneNumber);

            sendNumber();
        }
    }

    private void sendNumber() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        else if(!nfcAdapter.isNdefPushEnabled()) {
            Toast.makeText(this, "Please enable Android Beam.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            nfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback()
            {
                @Override
                public NdefMessage createNdefMessage(NfcEvent event) {
                    EditText editText = (EditText) findViewById(R.id.editText);

                    NdefRecord uriRecord = NdefRecord.createUri("tel:"+editText.getText());
                    return new NdefMessage(new NdefRecord[] { uriRecord });
                }

            }, this, this);
            Toast.makeText(this, "Message written!", Toast.LENGTH_SHORT).show();
        }
    }
}
