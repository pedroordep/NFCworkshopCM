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
        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] { ndefRecord });

        return ndefMessage;
    }

    public NdefMessage createNdefURI(String content){
        NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI , content.getBytes(Charset.forName("US-ASCII")), new byte[0], new byte[0]);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] { uriRecord });

        return ndefMessage;
    }

    public NdefMessage createNdefApplication(String content){
        NdefRecord appRecord = NdefRecord.createApplicationRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] { appRecord });

        return ndefMessage;
    }

    private NdefRecord createTextRecord(String content){
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
                ndef.connect();

                if(!ndef.isWritable()){
                    Toast.makeText(context, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

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

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(context, "Tag written", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
