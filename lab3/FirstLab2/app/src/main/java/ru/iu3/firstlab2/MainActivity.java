package ru.iu3.firstlab2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;


import ru.iu3.firstlab2.databinding.ActivityMainBinding;

interface TransactionEvents {
    String enterPin(int ptc, String amount);
    void transactionResult(boolean result);
}
public class MainActivity extends AppCompatActivity implements TransactionEvents{

    static {
        System.loadLibrary("firstlab2");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private String pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activityResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            //String pin = data.getStringExtra("pin");
                            //Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                });
        int randomRes = initRng();

        // Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
//
//        byte[] byteArr = randomBytes(20);
//        byte[] KEY = randomBytes(16);
//        Log.d("firstlab2_ndk", Arrays.toString(byteArr));
//        byteArr = encrypt(KEY, byteArr);
//        Log.d("firstlab2_ndk", Arrays.toString(byteArr));
//        byteArr = decrypt(KEY, byteArr);
//        Log.d("firstlab2_ndk", Arrays.toString(byteArr));
//        byte[] encryptedTextBytes = encrypt(KEY, tv.getText().toString().getBytes(StandardCharsets.UTF_8));
//        String decryptedText = new String(decrypt(KEY, encryptedTextBytes), StandardCharsets.UTF_8);
//        Log.d("firstlab2_ndk", Arrays.toString(tv.getText().toString().getBytes(StandardCharsets.UTF_8)));
//        Log.d("firstlab2_ndk", Arrays.toString(decrypt(KEY, encryptedTextBytes)));
//

    }

    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch(DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }
    public void onButtonClick(View v)
    {
        byte[] trd = stringToHex("9F0206000000000100");
        transaction(trd);
    }

    @Override
    public String enterPin(int ptc, String amount) {
        pin = "";
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
        });
    }
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);

    public static native byte[] encrypt(byte[] KEY, byte[] byteArr);

    public static native byte[] decrypt(byte[] KEY, byte[] byteArr);

    public native boolean transaction(byte[] trd);
}