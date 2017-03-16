package com.orange.orangesiminfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class OrangeSIMInfo extends AppCompatActivity {

    private final static String TAG = WassupAuthenticator.class.getSimpleName();
    private String mMsisdn="01 23 45 67 89";
    private String mExpDate="01/01/2017";
    private TextView mMSisdnField;
    private TextView mExpDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Wassup : onCreate debut");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orange_siminfo);
        mMSisdnField = (TextView) findViewById(R.id.SimInfoTelValue);
        mExpDateField = (TextView) findViewById(R.id.SimInfoExpDateValue);

        wassupAuthenticate();
        Log.d(TAG, "Wassup : onCreate fin");
    }

    private void wassupAuthenticate() {
        WassupAuthenticator.WassupAuthenticatorListener listener = new WassupAuthenticator.WassupAuthenticatorListener() {
            @Override
            public void onAuthenticationSuccess(String msisdn) {
                Log.d(TAG, "Wassup : onAuthenticationSuccess msisdn=" + msisdn);
                updateViewsAfterChange();
            }

            @Override
            public void onAuthenticationFailure(String yourRequestId, String errorMessage) {
                Log.e(TAG, "Wassup : onAuthenticationFailure error=" + errorMessage);
            }
        };
        new WassupAuthenticator(getApplicationContext(), listener).authenticate();
    }

    public void updateViewsAfterChange() {
        mMsisdn=WassupAuthenticator.getMsisdn();
        mMSisdnField.setText(mMsisdn);
        mExpDate=WassupAuthenticator.getExpDate();
        mExpDateField.setText(mExpDate);
    }
}
