package com.ravencoin.presenter.activities.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.ravencoin.R;
import com.ravencoin.presenter.activities.util.RActivity;
import com.ravencoin.presenter.customviews.RDialogView;
import com.ravencoin.tools.animation.BRAnimator;
import com.ravencoin.tools.animation.RDialog;
import com.ravencoin.tools.manager.BRSharedPrefs;
import com.ravencoin.tools.threads.executor.RExecutor;
import com.ravencoin.tools.util.RConstants;
import com.ravencoin.wallet.WalletsMaster;


public class SyncBlockchainActivity extends RActivity {
    private static final String TAG = SyncBlockchainActivity.class.getName();
    private Button scanButton;
    public static boolean appVisible = false;
    private static SyncBlockchainActivity app;

    public static SyncBlockchainActivity getApp() {
        return app;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_blockchain);

        ImageButton faq = (ImageButton) findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                BRAnimator.showSupportFragment(app, RConstants.reScan);
            }
        });

        scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                RDialog.showCustomDialog(SyncBlockchainActivity.this, getString(R.string.ReScan_alertTitle),
                        getString(R.string.ReScan_footer), getString(R.string.ReScan_alertAction), getString(R.string.Button_cancel),
                        new RDialogView.BROnClickListener() {
                            @Override
                            public void onClick(RDialogView rDialogView) {
                                rDialogView.dismissWithAnimation();
                                RExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        BRSharedPrefs.putStartHeight(SyncBlockchainActivity.this, BRSharedPrefs.getCurrentWalletIso(SyncBlockchainActivity.this), 0);
                                        BRSharedPrefs.putAllowSpend(SyncBlockchainActivity.this, BRSharedPrefs.getCurrentWalletIso(SyncBlockchainActivity.this), false);
                                        WalletsMaster.getInstance(SyncBlockchainActivity.this).getCurrentWallet(SyncBlockchainActivity.this).getPeerManager().rescan();
                                        BRAnimator.startRavenActivity(SyncBlockchainActivity.this, false);

                                    }
                                });
                            }
                        }, new RDialogView.BROnClickListener() {
                            @Override
                            public void onClick(RDialogView rDialogView) {
                                rDialogView.dismissWithAnimation();
                            }
                        }, null, 0);



            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        appVisible = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
