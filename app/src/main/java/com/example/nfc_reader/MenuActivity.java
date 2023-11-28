package com.example.nfc_reader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity{

    private TextView nfcDataTextView;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Tag tag;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;
        nfcDataTextView = findViewById(R.id.articule);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(this, "Este dispositivo no soporta NFC, lo sentimos", Toast.LENGTH_LONG).show();
            finish();
        }
        onNewIntent(this.getIntent());
    }

    private void readFromIntent(Intent intent){
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)){
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Obtener los datos de la etiqueta y mostrarlos en el TextView
                String nfcData = intent.getExtras().get("EXTRA_ID")+": ";
                for (int i = 0;i<messages[0].getRecords().length;i++){
                    NdefRecord record = messages[0].getRecords()[i];
                    Toast.makeText(context, ""+record.toString(), Toast.LENGTH_LONG).show();
                    Log.i("NFCTagReader",record.toString());
                    nfcData = nfcData+"\n"+new String(record.getPayload());
                }
                nfcDataTextView.setText(nfcData);
                Log.i("NFCTagReader",nfcData);
                Toast.makeText(context, nfcData, Toast.LENGTH_LONG).show();
            }
        }
    }

    // Manejar la intención NFC
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())){
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Log.i("NFCIntent",tag.toString());
            Log.i("NFCID",bytesToHexString(tag.getId()));
            intent.putExtra("EXTRA_ID",bytesToHexString(tag.getId()));
            readFromIntent(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_MUTABLE);
        IntentFilter[] intentFilters = { new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED) };
        nfcAdapter.enableForegroundDispatch(   this,
                pendingIntent,
                intentFilters,
                new String[][]{
                        new String[]{"android.nfc.tech.NfcA"}
                });

    }

    // Deshabilitar el despacho en primer plano cuando la actividad está en pausa
    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

}
