package com.ravencoin.presenter.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ravencoin.R;
import com.ravencoin.presenter.activities.util.ActivityUTILS;
import com.ravencoin.presenter.activities.util.RActivity;

public class DonationActivity extends RActivity {
    private static final String TAG = DonationActivity.class.getName();

    private static DonationActivity app;
    private ImageButton mBackButton;

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

        mBackButton = findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
