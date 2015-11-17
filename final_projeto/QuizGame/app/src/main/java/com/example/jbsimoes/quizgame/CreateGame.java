package com.example.jbsimoes.quizgame;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class CreateGame extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(!nfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if ( intent.hasExtra(NfcAdapter.EXTRA_TAG) ) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage();
            writeNdefMessage(tag, ndefMessage);
        }
    }

    private NdefMessage createNdefMessage() {

        NdefRecord[] records = new NdefRecord[5];

        EditText question = (EditText) findViewById(R.id.question);
        EditText a1 = (EditText) findViewById(R.id.a1);
        EditText a2 = (EditText) findViewById(R.id.a2);
        EditText a3 = (EditText) findViewById(R.id.a3);
        EditText a4 = (EditText) findViewById(R.id.a4);
        records[0] = createTextRecord(question.getText().toString());
        records[1] = createTextRecord(a1.getText().toString());
        records[2] = createTextRecord(a2.getText().toString());
        records[3] = createTextRecord(a3.getText().toString());
        records[4] = createTextRecord(a4.getText().toString());

        return new NdefMessage(records);
    }

    private NdefRecord createTextRecord(String content) {
        byte[] language;
        language = Locale.getDefault().getLanguage().getBytes();

        final byte[] text = content.getBytes();
        final int languageSize = language.length;
        final int textLenght = text.length;
        byte[] payload = new byte[1 + languageSize + textLenght];
        payload[0] = (byte) languageSize;
        System.arraycopy(language, 0, payload, 1, languageSize);
        System.arraycopy(text, 0, payload, 1 + languageSize, textLenght);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage){
        try {
            if(tag == null) {
                Toast.makeText(this, "Tag cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null) {
                formatTag(tag, ndefMessage);
            }
            else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this, "Tag written", Toast.LENGTH_SHORT).show();

                EditText question = (EditText) findViewById(R.id.question);
                EditText a1 = (EditText) findViewById(R.id.a1);
                EditText a2 = (EditText) findViewById(R.id.a2);
                EditText a3 = (EditText) findViewById(R.id.a3);
                EditText a4 = (EditText) findViewById(R.id.a4);

                question.setText("");
                a1.setText("");
                a2.setText("");
                a3.setText("");
                a4.setText("");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage){
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable == null){
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag written", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this, CreateGame.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
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
        getMenuInflater().inflate(R.menu.menu_create_game, menu);
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
