package com.example.jbsimoes.quizgame;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.LightingColorFilter;
import android.hardware.camera2.TotalCaptureResult;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlayGame extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    private TextView questionText;
    private Button bA1;
    private Button bA2;
    private Button bA3;
    private Button bA4;

    private String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // to check if the device support NFC and also to check if it's enabled
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_LONG).show();
            return;
        }

        questionText = (TextView) findViewById(R.id.question);
        bA1 = (Button) findViewById(R.id.buttonA1);
        bA2 = (Button) findViewById(R.id.buttonA2);
        bA3 = (Button) findViewById(R.id.buttonA3);
        bA4 = (Button) findViewById(R.id.buttonA4);

        Toast.makeText(this, "Tap device with NFC tag", Toast.LENGTH_LONG).show();

        onNewIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_game, menu);
        return true;
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

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, PlayGame.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // TODO
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        // TODO
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String temp1 = "UTF-8";
            String temp2 = "UTF-16";

            String textEncoding = ((payload[0] & 128) == 0) ? temp1 : temp2;
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    public void checkAnswer(View v) {
        Button clickedButton = (Button) v;
        String answerText = clickedButton.getText().toString();

        if (answerText.equals(correctAnswer)) {
            clickedButton.getBackground().setColorFilter(new LightingColorFilter(0xFF76FF03, 0x000000));
        }
        else {
            clickedButton.getBackground().setColorFilter(new LightingColorFilter(0xFFFF0000, 0x000000));
        }
    }

    public void resetView() {
        bA1.getBackground().setColorFilter(null);
        bA2.getBackground().setColorFilter(null);
        bA3.getBackground().setColorFilter(null);
        bA4.getBackground().setColorFilter(null);
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
