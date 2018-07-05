package com.ravencoin.presenter.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ravencoin.R;
import com.ravencoin.core.BRCoreAddress;
import com.ravencoin.core.BRCoreTransaction;
import com.ravencoin.presenter.activities.util.ActivityUTILS;
import com.ravencoin.presenter.activities.util.RActivity;
import com.ravencoin.presenter.customviews.RDialogView;
import com.ravencoin.presenter.entities.CryptoRequest;
import com.ravencoin.tools.animation.RDialog;
import com.ravencoin.tools.animation.SpringAnimator;
import com.ravencoin.tools.manager.BRClipboardManager;
import com.ravencoin.tools.manager.RReportsManager;
import com.ravencoin.tools.manager.SendManager;
import com.ravencoin.tools.util.Utils;
import com.ravencoin.wallet.WalletsMaster;
import com.ravencoin.wallet.abstracts.BaseWalletManager;
import com.ravencoin.wallet.wallets.util.CryptoUriParser;

import java.math.BigDecimal;

public class DonationActivity extends RActivity {
    private static final String TAG = DonationActivity.class.getName();

    private static DonationActivity app;
    private ImageButton mBackButton;
    private Button mSendButton;
    private TextView mWarningTxt;
    private EditText mAmountEdit;

    public static DonationActivity getApp() {
        return app;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        mAmountEdit = findViewById(R.id.amount_edit);
        mWarningTxt = findViewById(R.id.warning_text);
        mSendButton = findViewById(R.id.send_button);
        mSendButton = findViewById(R.id.send_button);
        mBackButton = findViewById(R.id.back_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void send() {
        WalletsMaster master = WalletsMaster.getInstance(this);
        BaseWalletManager wallet = master.getCurrentWallet(this);
        //get the current wallet used
        if (wallet == null) {
            Log.e(TAG, "onClick: Wallet is null and it can't happen.");
            RReportsManager.reportBug(new NullPointerException("Wallet is null and it can't happen."), true);
            return;
        }
        boolean allFilled = true;
        // todo makes it dynamic and remove tests
        String rawAddress = "R9i7i3VqXgVLzpaLo9g288y1omZSvFgVt6";
        String amountStr = "1";

        //inserted amount
        BigDecimal rawAmount = new BigDecimal(Utils.isNullOrEmpty(amountStr) ? "0" : amountStr);

        BigDecimal cryptoAmount = wallet.getSmallestCryptoForCrypto(this, rawAmount);
        CryptoRequest req = CryptoUriParser.parseRequest(this, rawAddress);
        if (req == null || Utils.isNullOrEmpty(req.address)) {
            sayInvalidClipboardData();
            return;
        }
        BRCoreAddress address = new BRCoreAddress(req.address);
        Activity app = this;
        if (!address.isValid()) {
            allFilled = false;

            RDialog.showCustomDialog(app, app.getString(R.string.Alert_error), app.getString(R.string.Send_noAddress),
                    app.getString(R.string.AccessibilityLabels_close), null, new RDialogView.BROnClickListener() {
                @Override
                public void onClick(RDialogView rDialogView) {
                    rDialogView.dismissWithAnimation();
                }
            }, null, null, 0);
            return;
        }
        if (cryptoAmount.doubleValue() <= 0) {
            allFilled = false;
            mWarningTxt.setVisibility(View.VISIBLE);
            SpringAnimator.failShakeAnimation(this, mAmountEdit);
        }
        if (cryptoAmount.longValue() > wallet.getCachedBalance(this)) {
            allFilled = false;
            mWarningTxt.setVisibility(View.VISIBLE);
            SpringAnimator.failShakeAnimation(this, mWarningTxt);
//            SpringAnimator.failShakeAnimation(this, feeText);
        }

        BRCoreTransaction tx = wallet.getWallet().createTransaction(cryptoAmount.longValue(), address);

        if (allFilled) {
            CryptoRequest item = new CryptoRequest(tx, null, false, null, req.address, cryptoAmount);
            SendManager.sendTransaction(this, item, wallet);
        }

    }


    private void sayInvalidClipboardData() {
        RDialog.showCustomDialog(this, "", getResources().getString(R.string.Send_invalidAddressTitle), getString(R.string.AccessibilityLabels_close), null, new RDialogView.BROnClickListener() {
            @Override
            public void onClick(RDialogView rDialogView) {
                rDialogView.dismiss();
            }
        }, null, null, 0);
        BRClipboardManager.putClipboard(this, "");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        app = this;

        ActivityUTILS.changeStatusBarColor(this, R.color.extra_light_blue_background);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
