package com.its.workshopnfc;


import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

public class NFC_Controller {

    public NdefMessage createNdefMessage(String content){
        // createTextRecord API 21

        // TODO
        return null;
    }

    public NdefMessage createNdefURI(String content){
        // TODO
        return null;
    }

    public NdefMessage createNdefApplication(String content){
        // TODO
        return null;
    }

    private NdefRecord createTextRecord(String content){

        // TODO

        return null;
    }

    public void writeNdefMessage(Tag tag, NdefMessage ndefMessage, Context context){
        try{
            if(tag == null){
                Toast.makeText(context, "Tag cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                formatTag(tag, ndefMessage, context);
            }
            else{

                // TODO

                Toast.makeText(context, "Tag written", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage, Context context) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(context, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO

            Toast.makeText(context, "Tag written", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
