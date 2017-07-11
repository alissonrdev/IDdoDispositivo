package com.alissonrafael.adbmobiddodispositivo;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Inicio extends AppCompatActivity implements View.OnClickListener{

    // BOTÃO COPIAR
    private CardView btnCopiar;

    // TEXTVIEW QUE MOSTRA O ID
    private TextView idDoDispositivo;

    // A IMAGEM QUE MOSTRA O MENU
    private ImageView imagemMenu, imagemCompartilhar;

    // MENU BOTTOM SHEET
    private BottomSheetDialog bsdMenu;

    private String admobID;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // BOTÃO COPIAR
        btnCopiar = (CardView) findViewById(R.id.btnCopiar);
        btnCopiar.setOnClickListener(this);

        idDoDispositivo = (TextView) findViewById(R.id.iddodispositivo);

        // IMAGEM MENU
        imagemMenu = (ImageView) findViewById(R.id.imagemMenu);
        imagemMenu.setOnClickListener(this);

        // IMAGEM COMPARTILHAR
        imagemCompartilhar = (ImageView) findViewById(R.id.imagemCompartilhar);
        imagemCompartilhar.setOnClickListener(this);

        // OBTER ID
        @SuppressLint("HardwareIds")
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        admobID = gerarMD5(id).toUpperCase();

        // MOSTRA O ID NO TEXTVIEW
        idDoDispositivo.setText(getString(R.string.msg_id) + "\n" + admobID);
    }

    // GERAR O MD5
    public String gerarMD5(String s) {
        try {
            // CRIAR HASH MD5
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // CRIA A STRING EM HEXADECIMAL
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCopiar:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Meu ID", admobID);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), getString(R.string.id_copiado),
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.imagemMenu:
                bsdMenu = new BottomSheetDialog(Inicio.this);
                View bsdView = LayoutInflater.from(Inicio.this).inflate(R.layout.botao_menu, null);


                bsdView.findViewById(R.id.acao_contato).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto:" + "SEU ENDEREÇO DE EMAIL AQUI"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                        try {
                            startActivity(Intent.createChooser(emailIntent, getString(R.string.email_via)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(Inicio.this, (getString(R.string.nenhum_cliente)), Toast.LENGTH_SHORT).show();
                        }
                        bsdMenu.dismiss();
                    }
                });

                bsdView.findViewById(R.id.acao_sair);
                bsdView.findViewById(R.id.acao_sair).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                bsdMenu.setContentView(bsdView);
                bsdMenu.show();
                break;
            case R.id.imagemCompartilhar:
                Intent compartilharID = new Intent();
                compartilharID.setAction(Intent.ACTION_SEND);
                compartilharID.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.compartilhar));
                compartilharID.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_id) + "\n" + admobID);
                compartilharID.setType("text/plain");
                startActivity(compartilharID);
                break;
        }
    }
}

