package com.ravencoin.presenter.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ravencoin.R;
import com.ravencoin.presenter.activities.util.ActivityUTILS;
import com.ravencoin.presenter.tutorial.AppIntro;
import com.ravencoin.presenter.tutorial.AppIntroFragment;
import com.ravencoin.presenter.tutorial.model.SliderPage;

public class TutorialActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Welcome to Ravencoin wallet!");
        sliderPage1.setDescription("You pay by scanning a QR-code, and receive payments reliably and instantly.");
        sliderPage1.setImageDrawable(R.drawable.ic_slide5);
        sliderPage1.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("12 mnemonic recovery word phrase");
        sliderPage2.setDescription("Back up your funds to ensure you always have access.");
        sliderPage2.setImageDrawable(R.drawable.ic_slide2);
        sliderPage2.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("BTC/Fiat & Gold/Silver");
        sliderPage3.setDescription("Choose your preferred display currency: Settings/Display Currency.");
        sliderPage3.setImageDrawable(R.drawable.ic_slide3);
        sliderPage3.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Wallet generates new key pair for every transaction");
        sliderPage4.setDescription("Public address is changed with every transaction, RVN Wallet still" +
                " manages all keys for you, you can re-use old addresses but this is against best practices");
        sliderPage4.setImageDrawable(R.drawable.ic_slide4);
        sliderPage4.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle("RVN Wallet doesn't store your money");
        sliderPage5.setDescription("Your money is on the network, the wallet contains keys ... a keychain.");
        sliderPage5.setImageDrawable(R.drawable.ic_slide4);
        sliderPage5.setBgColor(Color.TRANSPARENT);
        addSlide(AppIntroFragment.newInstance(sliderPage5));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUTILS.changeStatusBarColor(this, R.color.logo_gradient_end);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}