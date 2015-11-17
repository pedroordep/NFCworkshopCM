package com.its.workshopnfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class WriteTextActivity extends AppCompatActivity {

	//declarar controller e o adapter
    NFC_Controller nfc_controller;
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_text);

        //inicializar as variaveis
        //ir buscar o adapter do device
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        nfc_controller = new NFC_Controller();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //faz com que a activity intercepte um intent NFC para ter prioridade sobre outras activities
        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    //a funcao é chamada quando aparece uma nova activity, neste caso para quando se passa numa tag
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //contem uma tag se for descoberta por uma das actions (ACTION_NDEF_DISCOVERED neste caso)
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            //Toast.makeText(this, "nfcIntent", Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            EditText message = (EditText) findViewById(R.id.editText);
            //criar um objecto ndefMessage que vai conter a mensagem
            NdefMessage ndefMessage = nfc_controller.createNdefMessage(message.getText().toString());
            //enviar a mensagem
            nfc_controller.writeNdefMessage(tag, ndefMessage, this);
        }
    }

    private void enableForegroundDispatchSystem() {
    	//flag serve para um novo broadcast dar replace a um que ja exista
        Intent intent = new Intent(this, WriteTextActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        //e um token que serve para enviar para outras aplicacoes, sendo executadas com as permissoes da outra app
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_write_text, menu);
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
