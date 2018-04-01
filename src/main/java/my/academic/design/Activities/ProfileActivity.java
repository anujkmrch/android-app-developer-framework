package my.academic.design.Activities;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import my.academic.design.App.AuthenticationHandler;
import my.academic.design.R;

public class ProfileActivity extends AppCompatActivity {
    private String KEY_NAME ="somekeyname";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if(!fingerprintManager.isHardwareDetected())
        {
            Log.e("Hardware","Fingerprint device does not exists");
            Toast.makeText(this, "Hardware does not exists", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED){
            Log.e("Permission","finger print permission rejected");
            Toast.makeText(this, "permission rejeected", Toast.LENGTH_SHORT).show();
        }

//        if(!keyguardManager.isKeyguardSecure()){
//            Log.e("Keyguard","Keyguard is not secure");
//            Toast.makeText(this, "Keyguard is not secure", Toast.LENGTH_SHORT).show();
//        }

        KeyStore keyStore;
        try{
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e){
            Log.e("KeyStore",e.getMessage());
            return;
        }

        KeyGenerator keyGenerator;
        try{
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
        } catch(Exception e)
        {
            Log.e("KeyGenerator",e.getMessage());
            return;
        }

        try{
            keyStore.load(null);
            //Initialize the KeyGenerator//
            keyGenerator.init(new
                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } catch (Exception e)
        {
            Log.e("Key Generator",e.getMessage());
        }

        Cipher cipher;

        try {
            Toast.makeText(this, "Hello how are you", Toast.LENGTH_SHORT).show();
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            Toast.makeText(this, cipher.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
//            Toast.makeText(this, key.toString(), Toast.LENGTH_SHORT).show();
            cipher.init(Cipher.ENCRYPT_MODE, key);

        }
        catch (Exception e) {
              Log.e("Secret Key",e.getMessage());
            return;
        }

        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject,cancellationSignal,0, new AuthenticationHandler(this),null);
    }
}
