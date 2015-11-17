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
        //createTextRecord - funcao implementada na API 21 mas tem de se fazer à pata nesta.
        //mostrar aquela imagem 
        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] { ndefRecord });

        return ndefMessage;
    }

    public NdefMessage createNdefURI(String content){
        //criar record com as flags para ser URL, e ja nao se precisa de se fazer um payload por causa da flag
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
        //o tipo é wellknown, e para wellkown existe rtd-text. no payload tem de ter 1 byte(status), linguagem(1 a x) e o conteudo(1+linguagem+ y)
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

                //caso exista, connectar
                ndef.connect();
                //ver se a tag é writable
                if(!ndef.isWritable()){
                    Toast.makeText(context, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }
                //se for, escrever a mensagem. esta funcao tem o mesmo nome mas esta é mesmo da classe
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

            //conectar, formatar com o text novo e fechar
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(context, "Tag written", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
