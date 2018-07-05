package com.ravencoin.presenter.activities.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ravencoin.R;
import com.ravencoin.presenter.activities.util.ActivityUTILS;
import com.ravencoin.presenter.activities.util.RActivity;
import com.ravencoin.presenter.customviews.RDialogView;
import com.ravencoin.presenter.interfaces.BRAuthCompletion;
import com.ravencoin.tools.animation.BRAnimator;
import com.ravencoin.tools.animation.RDialog;
import com.ravencoin.tools.manager.BRSharedPrefs;
import com.ravencoin.tools.security.AuthManager;
import com.ravencoin.tools.security.BRKeyStore;
import com.ravencoin.tools.util.RConstants;
import com.ravencoin.tools.util.CurrencyUtils;
import com.ravencoin.tools.util.Utils;
import com.ravencoin.wallet.WalletsMaster;

import java.math.BigDecimal;


public class FingerprintActivity extends RActivity {
    private static final String TAG = FingerprintActivity.class.getName();

    public RelativeLayout layout;
    public static boolean appVisible = false;
    private static FingerprintActivity app;
    private TextView limitExchange;
    private TextView limitInfo;

    private ToggleButton toggleButton;

    public static FingerprintActivity getApp() {
        return app;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        limitExchange = (TextView) findViewById(R.id.limit_exchange);
        limitInfo = (TextView) findViewById(R.id.limit_info);

        ImageButton faq = (ImageButton) findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                BRAnimator.showSupportFragment(app, RConstants.enableFingerprint);
            }
        });

        toggleButton.setChecked(BRSharedPrefs.getUseFingerprint(this));

        limitExchange.setText(getLimitText());

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Activity app = FingerprintActivity.this;
                if (isChecked && !Utils.isFingerprintEnrolled(app)) {
                    Log.e(TAG, "onCheckedChanged: fingerprint not setup");
                    RDialog.showCustomDialog(app, getString(R.string.TouchIdSettings_disabledWarning_title_android), getString(R.string.TouchIdSettings_disabledWarning_body_android), getString(R.string.Button_ok), null, new RDialogView.BROnClickListener() {
                        @Override
                        public void onClick(RDialogView rDialogView) {
                            rDialogView.dismissWithAnimation();
                        }
                    }, null, null, 0);
                    buttonView.setChecked(false);
                } else {
                    BRSharedPrefs.putUseFingerprint(app, isChecked);
                }

            }
        });
        SpannableString ss = new SpannableString(getString(R.string.TouchIdSettings_customizeText_android));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                AuthManager.getInstance().authPrompt(FingerprintActivity.this, null, getString(R.string.VerifyPin_continueBody), true, false, new BRAuthCompletion() {
                    @Override
                    public void onComplete() {
                        Intent intent = new Intent(FingerprintActivity.this, SpendLimitActivity.class);
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        //start index of the last space (beginning of the last word)
        int indexOfSpace = limitInfo.getText().toString().lastIndexOf(" ");
        // make the whole address clickable if failed to select the last word
        ss.setSpan(clickableSpan, indexOfSpace == -1 ? 0 : indexOfSpace, limitInfo.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        limitInfo.setText(ss);
        limitInfo.setMovementMethod(LinkMovementMethod.getInstance());
        limitInfo.setHighlightColor(Color.TRANSPARENT);

    }

    private String getLimitText() {
        String iso = BRSharedPrefs.getPreferredFiatIso(this);
        //amount in satoshis
        BigDecimal satoshis = new BigDecimal(BRKeyStore.getSpendLimit(this));
        WalletsMaster master = WalletsMaster.getInstance(this);
        //amount in RVN, mRVN or uRVN
        BigDecimal amount = master.getCurrentWallet(this).getFiatForSmallestCrypto(this, satoshis, null);
        //amount in user preferred ISO (e.g. USD)
        BigDecimal curAmount = master.getCurrentWallet(this).getFiatForSmallestCrypto(this, satoshis, null);
        //formatted string for the label
        return String.format(getString(R.string.TouchIdSettings_spendingLimit), CurrencyUtils.getFormattedAmount(this, "RVN", amount), CurrencyUtils.getFormattedAmount(this, iso, curAmount));
    }

    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;

        ActivityUTILS.changeStatusBarColor(this, R.color.logo_gradient_end);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        BRAnimator.startBreadActivity(this, false);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void onPause() {
        super.onPause();
        appVisible = false;
    }

}
