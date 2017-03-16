/*
 * Copyright (C) 2016 Orange
 *
 * This software is confidential and proprietary information of Orange.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the agreement you entered into.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * If you are Orange employee you shall use this software in accordance with
 * the Orange Source Charter (http://opensource.itn.ftgroup/index.php/Orange_Source).
 */

package com.orange.orangesiminfo;

import android.content.Context;
import android.util.Log;

import com.orange.authentication.lowLevelApi.impl.LowLevelAuthenticationUsingVolley;
import com.orange.authentication.lowLevelApi.api.LowLevelAuthenticationIdentity;
import com.orange.authentication.lowLevelApi.api.LowLevelAuthenticationListener;
import com.orange.authentication.lowLevelApi.impl.LowLevelAuthenticationConfigurationImpl;


/**
 * A class to authenticate the mobile via WASSUP.
 */
public class WassupAuthenticator {

    private final static String TAG = WassupAuthenticator.class.getSimpleName();

    private static final String AUTHENTICATION_SERVICE_ID = "SDKAPI";
    private static final String AUTHENTICATION_REQUEST_ID = "PROD_FR";
    private static final String AUTHENTICATION_MOBILE_CC_OPERATOR = "OFR";

    private static final String MSS_PARAM = "mss";  // Mobiles Subscription Segment
    private static final String MSISDN_PARAM = "msisdn";  // MSISDN (mobile phone identifier)
    private static final String USC_PARAM = "usc";  // Commercial Segment
    private static final String DCR_PARAM = "dcr";  // Profile creation date
    private static final String SPE_PARAM = "spe";  // Closing date of validity of products and option code
    private static final String WAD_PARAM = "wad";  // Wassup authentication date: date at the first authentication in current session cookie
    private static final String[] AUTHENTICATION_PARAMETERS = {MSS_PARAM, USC_PARAM, DCR_PARAM, SPE_PARAM, WAD_PARAM};

    final LowLevelAuthenticationUsingVolley mApi;
    private static String mMsisdn="xx xx xx xx xx";
    private static String mExpDate="jj/mm/aaaa";


    /**
     * An interface to report Wassup authentication result
     */
    public interface WassupAuthenticatorListener {
        void onAuthenticationSuccess(String contact);

        void onAuthenticationFailure(String yourRequestId, String errorMessage);
    }

    public WassupAuthenticator(Context ctx, final WassupAuthenticatorListener listener) {
        mApi = getAuthenticator(ctx);
        Log.d(TAG, "Wassup Authenticator : mApi = " + mApi);

        LowLevelAuthenticationListener myListener = new LowLevelAuthenticationListener() {
            @Override
            public void onAuthenticationSuccess(String yourRequestId) {
                LowLevelAuthenticationIdentity identity = mApi.getCurrentIdentity();
                mMsisdn = identity.getMsisdn();
                Log.d(TAG, "Wassup Parameter: MSISDN (msisdn) = " + mMsisdn);

                String mss = identity.getRawWassupValue(MSS_PARAM);
                Log.d(TAG, "Wassup Parameter: mobile subscription segment (mss) = " + mss);

                String usc = identity.getRawWassupValue(USC_PARAM);
                Log.d(TAG, "Wassup Parameter: user commercial segment (usc) = " + usc);

                String dcr = identity.getRawWassupValue(DCR_PARAM);
                Log.d(TAG, "Wassup Parameter: Profile creation date (dcr) = " + dcr);

                String spe = identity.getRawWassupValue(SPE_PARAM);
                Log.d(TAG, "Wassup Parameter: Closing date of validity of products and option code (spe) = " + spe);
                mExpDate = spe;

                String wad = identity.getRawWassupValue(WAD_PARAM);
                Log.d(TAG, "Wassup Parameter: Wassup authentication date: date at the first authentication in current session cookie (wad) = " + wad);

                listener.onAuthenticationSuccess(mMsisdn);
            }

            @Override
            public void onAuthenticationFailure(String yourRequestId, String errorMessage) {
                listener.onAuthenticationFailure(yourRequestId, errorMessage);
            }
        };
        mApi.addListener(myListener);
        Log.d(TAG, "Wassup Authenticator : mApi listener = " + myListener);
    }

    private LowLevelAuthenticationUsingVolley getAuthenticator(Context ctx) {
        LowLevelAuthenticationConfigurationImpl configuration = new LowLevelAuthenticationConfigurationImpl();
        configuration.setServiceId(AUTHENTICATION_SERVICE_ID);
        configuration.setAuthenticationPlatform(AUTHENTICATION_REQUEST_ID);
        configuration.setMobileCountryOperator(AUTHENTICATION_MOBILE_CC_OPERATOR);
        for (String param : AUTHENTICATION_PARAMETERS) {
            configuration.addExtraAuthenticationParameter(param);
        }
        Log.d(TAG, "Wassup Authenticator : getAuthenticator Configuration = " + configuration);
        return new LowLevelAuthenticationUsingVolley(ctx, configuration);
    }

    public void authenticate() {
        Log.d(TAG, "Start WASSUP authentication");
        mApi.authenticateImplicitlyWithMobile(AUTHENTICATION_REQUEST_ID, false);
    }

    public void cancelAuthentication() {
        Log.d(TAG, "Cancel WASSUP authentication");
        mApi.cancelOngoingAuthenticationRequest(AUTHENTICATION_REQUEST_ID);
    }

    public static String getMsisdn(){
        Log.d(TAG, "Wassup getMsisdn : " + mMsisdn);
        return mMsisdn;
    }
    public static String getExpDate(){
        Log.d(TAG, "Wassup getExpDate : " + mExpDate);
        return mExpDate;
    }
}
