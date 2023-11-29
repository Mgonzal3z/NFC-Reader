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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfc_reader.correo.Email;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractSet;
import java.util.Date;
import java.util.Formatter;

public class MenuActivity extends AppCompatActivity{

    private TextView nfcDataTextView;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private Tag tag;
    private Context context;
    private Email email;

    private String nfcData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
                nfcData = "Tag ID = "+intent.getExtras().get("EXTRA_ID")+": ";
                for (int i = 0;i<messages[0].getRecords().length;i++){
                    NdefRecord record = messages[0].getRecords()[i];
                    String msj="";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        msj = new String(record.getPayload(), StandardCharsets.UTF_8);
                    }
                    Log.i("MSJRead",msj);
                    Log.i("MSJRead",msj.substring(1,3));
                    if(msj.substring(1,3).equals("en")){msj = msj.substring(3);}
                    nfcData = nfcData+"\n"+msj;
                }
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String horaAct = "Hora: "+dateFormat.format(date);
                nfcData = nfcData + "\n"+horaAct;
                nfcDataTextView.setText(nfcData);
                Log.i("NFCTagReader",nfcData);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg) {
                        try {
                            email = new Email();
                            email.enviarMailLectura(nfcData, "real4895@gmail.com");
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                        return null;
                    }
                }.execute();
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
            StringBuilder hexString = new StringBuilder();
            try (Formatter formatter
                         = new Formatter(hexString)) {
                for (byte b : tag.getId()) {
                    formatter.format("%02x:", b);
                }
                hexString.deleteCharAt(hexString.length() - 1);
            }
            Log.i("NFCID",hexString.toString());
            intent.putExtra("EXTRA_ID",""+hexString);
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

}
