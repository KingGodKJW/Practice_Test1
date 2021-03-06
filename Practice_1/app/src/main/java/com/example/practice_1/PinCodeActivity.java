package com.example.practice_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFResult;
import com.beautycoder.pflockscreen.security.PFSecurityManager;
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback;
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PinCodeActivity extends AppCompatActivity {

    String masterKeyAlias;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    PFLockScreenFragment fragment;
    PFFLockScreenConfiguration.Builder builder;
    Context context;
    Boolean isPinExist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_pincode);
        context = this;
        showLockScreenFragment();


    }

    private void showLockScreenFragment(boolean isPinExist) {

        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    "secret",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor = sharedPreferences.edit();
        fragment = new PFLockScreenFragment();

        if(isPinExist && !sharedPreferences.getString("encoded_pin_code", "").equals("")) {
            fragment.setEncodedPinCode(sharedPreferences.getString("encoded_pin_code", ""));
            builder = new PFFLockScreenConfiguration.Builder(this).
                    setMode(PFFLockScreenConfiguration.MODE_AUTH)
                    .setTitle("PIN ????????? ??????????????????")
                    .setLeftButton("??????")
                    .setNextButton("??????")
                    .setCodeLength(6);
        }else {
            Log.d("encoded_pin_code","????????? ????????? ?????? ...");
            Toast.makeText(getApplicationContext(), "????????? ???????????? ????????????. ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
            builder = new PFFLockScreenConfiguration.Builder(this).
                    setMode(PFFLockScreenConfiguration.MODE_CREATE)
                    .setTitle("PIN ????????? ??????????????????")
                    .setNewCodeValidation(true)
                    .setNewCodeValidationTitle("?????? ??? ??? PIN ????????? ??????????????????")
                    .setNextButton("??????")
                    .setUseFingerprint(false)
                    .setCodeLength(6);
        }

        fragment.setConfiguration(builder.build());
        fragment.setOnLeftButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "???????????? ????????????????????????.", Toast.LENGTH_LONG);

                /*
                 * ???????????? ????????? ????????? ??????
                 * */
                PFSecurityManager.getInstance().getPinCodeHelper().delete(new PFPinCodeHelperCallback<Boolean>() {
                    @Override
                    public void onResult(PFResult<Boolean> result) {
                        Log.d("encoded_pin_code", "?????? ?????? ??????" + result.getResult());

                    }
                });
                // getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                finish();
            }
        });

        fragment.setLoginListener(new PFLockScreenFragment.OnPFLockScreenLoginListener() {
            @Override
            public void onCodeInputSuccessful() {
                Log.d("encoded_pin_code", "?????? ??????");
                Toast.makeText(context, "????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFingerprintSuccessful() {
                Log.d("encoded_pin_code", "?????? ?????? ??????");
            }

            @Override
            public void onPinLoginFailed() {
                Log.d("encoded_pin_code", "??? ?????? ??????");
                Toast.makeText(getApplicationContext(), "????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFingerprintLoginFailed() {
                Log.d("encoded_pin_code", "?????? ?????? ??????");
            }
        });

        fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
            @Override
            public void onCodeCreated(String encodedCode) {
                editor.putString("encoded_pin_code", encodedCode);
                editor.commit();
                Log.d("encoded_pin_code", "??? ?????? ?????? ?????? : " + encodedCode);
                Toast.makeText(getApplicationContext(), "???????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onNewCodeValidationFailed() {
                Toast.makeText(getApplicationContext(), "???????????? ????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.auth_pincode_container, fragment).commit();
    }

    private void showLockScreenFragment() {
        new PFPinCodeViewModel().isPinCodeEncryptionKeyExist().observe(this,
                new Observer<PFResult<Boolean>>() {
                    @Override
                    public void onChanged(PFResult<Boolean> result) {
                        Log.d("encoded_pin_code", "on Changed");

                        if (result == null) {
                            return;
                        }
                        if(result.getError() != null) {
                            Toast.makeText(PinCodeActivity.this, "??? ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        showLockScreenFragment(result.getResult());
                    }
                });
    }


}
