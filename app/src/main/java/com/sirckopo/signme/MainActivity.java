package com.sirckopo.signme;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class MainActivity extends ActionBarActivity {

    EditText etPrivateKey;
    EditText etPublicKey;
    EditText etMessage;
    EditText etSignature;

    TextView tvResult;

    Button butKeygen;
    Button butSign;
    Button butVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPrivateKey = (EditText) findViewById(R.id.etPrivateKey);
        etPublicKey = (EditText) findViewById(R.id.etPublicKey);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etSignature = (EditText) findViewById(R.id.etSignature);

        tvResult = (TextView) findViewById(R.id.tvResult);

        butKeygen = (Button) findViewById(R.id.butKeygen);
        butSign = (Button) findViewById(R.id.butSign);
        butVerify = (Button) findViewById(R.id.butVerify);
    }


    public void keygen(View v) {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair keyPair = kpg.generateKeyPair();

            etPublicKey.setText(Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT));
            etPrivateKey.setText(Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void sign(View v) {
        try {
            PKCS8EncodedKeySpec eks = new PKCS8EncodedKeySpec(
                    Base64.decode(etPrivateKey.getText().toString(), Base64.DEFAULT));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(eks);

            Signature sig = Signature.getInstance("MD5WithRSA");
            sig.initSign(privateKey);
            sig.update(etMessage.getText().toString().getBytes());

            etSignature.setText(Base64.encodeToString(sig.sign(), Base64.DEFAULT));

        } catch (NoSuchAlgorithmException | InvalidKeySpecException |
                SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public void verify(View v) {
        //TODO: make it work (always true)
        try {
            X509EncodedKeySpec eks = new X509EncodedKeySpec(
                    Base64.decode(etPublicKey.getText().toString(), Base64.DEFAULT));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(eks);

            Signature sig = Signature.getInstance("MD5WithRSA");
            sig.initVerify(publicKey);
            sig.update(etMessage.getText().toString().getBytes());

            boolean isLegit = sig.verify(Base64.decode(etSignature.getText().toString(),
                    Base64.DEFAULT));
            if (isLegit)
                tvResult.setText("True legit message!");
            else
                tvResult.setText("Wrong signature!");

        } catch (NoSuchAlgorithmException | InvalidKeySpecException |
                SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

}
