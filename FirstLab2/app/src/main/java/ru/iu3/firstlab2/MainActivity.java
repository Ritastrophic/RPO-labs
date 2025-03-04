package ru.iu3.firstlab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import ru.iu3.firstlab2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'firstlab2' library on application startup.
    static {
        System.loadLibrary("firstlab2");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        int randomRes = initRng();
        byte[] byteArr = randomBytes(20);
        byte[] KEY = randomBytes(16);
        Log.d("firstlab2_ndk", Arrays.toString(byteArr));
        byteArr = encrypt(KEY, byteArr);
        Log.d("firstlab2_ndk", Arrays.toString(byteArr));
        byteArr = decrypt(KEY, byteArr);
        Log.d("firstlab2_ndk", Arrays.toString(byteArr));
        byte[] encryptedTextBytes = encrypt(KEY, tv.getText().toString().getBytes(StandardCharsets.UTF_8));
        String decryptedText = new String(decrypt(KEY, encryptedTextBytes), StandardCharsets.UTF_8);
        Log.d("firstlab2_ndk", Arrays.toString(tv.getText().toString().getBytes(StandardCharsets.UTF_8)));
        Log.d("firstlab2_ndk", Arrays.toString(decrypt(KEY, encryptedTextBytes)));
    }

    /**
     * A native method that is implemented by the 'firstlab2' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);

    public static native byte[] encrypt(byte[] KEY, byte[] byteArr);

    public static native byte[] decrypt(byte[] KEY, byte[] byteArr);
}