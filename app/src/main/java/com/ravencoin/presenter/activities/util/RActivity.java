package com.ravencoin.presenter.activities.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.ravencoin.RavenApp;
import com.ravencoin.presenter.activities.DisabledActivity;
import com.ravencoin.presenter.activities.intro.IntroActivity;
import com.ravencoin.presenter.activities.intro.RecoverActivity;
import com.ravencoin.presenter.activities.intro.WriteDownActivity;
import com.ravencoin.tools.animation.BRAnimator;
import com.ravencoin.tools.manager.BRApiManager;
import com.ravencoin.tools.manager.InternetManager;
import com.ravencoin.tools.security.AuthManager;
import com.ravencoin.tools.security.BRKeyStore;
import com.ravencoin.tools.threads.executor.RExecutor;
import com.ravencoin.tools.util.RConstants;
import com.ravencoin.wallet.wallets.util.CryptoUriParser;
import com.ravencoin.tools.security.PostAuth;
import com.ravencoin.wallet.WalletsMaster;
import com.platform.HTTPServer;
import com.platform.tools.BRBitId;

public class RActivity extends Activity {
    private static final String TAG = RActivity.class.getName();
    public static final Point screenParametersPoint = new Point();


    static {
        System.loadLibrary(RConstants.NATIVE_LIB_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        RavenApp.activityCounter.decrementAndGet();
        RavenApp.onStop(this);
    }

    @Override
    protected void onResume() {
        init(this);
        super.onResume();
        RavenApp.backgroundedTime = 0;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // 123 is the qrCode result
        switch (requestCode) {

            case RConstants.PAY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onPublishTxAuth(RActivity.this, true);
                        }
                    });
                }
                break;
            case RConstants.REQUEST_PHRASE_BITID:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onBitIDAuth(RActivity.this, true);
                        }
                    });

                }
                break;

            case RConstants.PAYMENT_PROTOCOL_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onPaymentProtocolRequest(RActivity.this, true);
                        }
                    });

                }
                break;

            case RConstants.CANARY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onCanaryCheck(RActivity.this, true);
                        }
                    });
                } else {
                    finish();
                }
                break;

            case RConstants.SHOW_PHRASE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onPhraseCheckAuth(RActivity.this, true);
                        }
                    });
                }
                break;
            case RConstants.PROVE_PHRASE_REQUEST:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onPhraseProveAuth(RActivity.this, true);
                        }
                    });
                }
                break;
            case RConstants.PUT_PHRASE_RECOVERY_WALLET_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onRecoverWalletAuth(RActivity.this, true);
                        }
                    });
                } else {
                    finish();
                }
                break;

            case RConstants.SCANNER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            String result = data.getStringExtra("result");
                            if (CryptoUriParser.isCryptoUrl(RActivity.this, result))
                                CryptoUriParser.processRequest(RActivity.this, result,
                                        WalletsMaster.getInstance(RActivity.this).getCurrentWallet(RActivity.this));
                            else if (BRBitId.isBitId(result))
                                BRBitId.signBitID(RActivity.this, result, null);
                            else
                                Log.e(TAG, "onActivityResult: not bitcoin address NOR bitID");
                        }
                    });

                }
                break;

            case RConstants.PUT_PHRASE_NEW_WALLET_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                        @Override
                        public void run() {
                            PostAuth.getInstance().onCreateWalletAuth(RActivity.this, true);
                        }
                    });

                } else {
                    Log.e(TAG, "WARNING: resultCode != RESULT_OK");
                    WalletsMaster m = WalletsMaster.getInstance(RActivity.this);
                    m.wipeWalletButKeystore(this);
                    finish();
                }
                break;

        }
    }

    public void init(Activity app) {
        //set status bar color
//        ActivityUTILS.setStatusBarColor(app, android.R.color.transparent);
        InternetManager.getInstance();
        if (!(app instanceof IntroActivity || app instanceof RecoverActivity || app instanceof WriteDownActivity))
            BRApiManager.getInstance().startTimer(app);
        //show wallet locked if it is
        if (!ActivityUTILS.isAppSafe(app))
            if (AuthManager.getInstance().isWalletDisabled(app))
                AuthManager.getInstance().setWalletDisabled(app);

        RavenApp.activityCounter.incrementAndGet();
        RavenApp.setBreadContext(app);

        if (!HTTPServer.isStarted())
            RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                @Override
                public void run() {
                    HTTPServer.startServer();
                }
            });

        lockIfNeeded(this);

    }

    private void lockIfNeeded(Activity app) {
        //lock wallet if 3 minutes passed
        if (RavenApp.backgroundedTime != 0
                && ((System.currentTimeMillis() - RavenApp.backgroundedTime) >= 180 * 1000)
                && !(app instanceof DisabledActivity)) {
            if (!BRKeyStore.getPinCode(app).isEmpty()) {
                Log.e(TAG, "lockIfNeeded: " + RavenApp.backgroundedTime);
                BRAnimator.startRavenActivity(app, true);
            }
        }

    }

}
