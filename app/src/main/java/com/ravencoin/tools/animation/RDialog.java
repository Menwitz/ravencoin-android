package com.ravencoin.tools.animation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.util.Log;

import com.ravencoin.R;
import com.ravencoin.presenter.customviews.RDialogView;
import com.ravencoin.tools.threads.executor.RExecutor;

public class RDialog {
    private static final String TAG = RDialog.class.getName();
    private static RDialogView dialog;

    /**
     * Safe from any threads
     *
     * @param app needs to be activity
     */
    public static void showCustomDialog(@NonNull final Context app, @NonNull final String title, @NonNull final String message,
                                        @NonNull final String posButton, final String negButton, final RDialogView.BROnClickListener posListener,
                                        final RDialogView.BROnClickListener negListener, final DialogInterface.OnDismissListener dismissListener, final int iconRes) {
        if (((Activity) app).isDestroyed()) {
            Log.e(TAG, "showCustomDialog: FAILED, context is destroyed");
            return;
        }

        RExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                dialog = new RDialogView();
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setPosButton(posButton);
                dialog.setNegButton(negButton);
                dialog.setPosListener(posListener);
                dialog.setNegListener(negListener);
                dialog.setDismissListener(dismissListener);
                dialog.setIconRes(iconRes);
                if (!((Activity) app).isDestroyed())
                    dialog.show(((Activity) app).getFragmentManager(), dialog.getClass().getName());
            }
        });

    }

    public static void showHelpDialog(@NonNull final Context app, @NonNull final String title, @NonNull final String message, @NonNull final String posButton, @NonNull final String negButton, final RDialogView.BROnClickListener posListener, final RDialogView.BROnClickListener negListener, final RDialogView.BROnClickListener helpListener) {

        if (((Activity) app).isDestroyed()) {
            Log.e(TAG, "showCustomDialog: FAILED, context is destroyed");
            return;
        }

        RExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                dialog = new RDialogView();
                dialog.setTitle(title);
                dialog.setMessage(message);
                dialog.setPosButton(posButton);
                dialog.setNegButton(negButton);
                dialog.setPosListener(posListener);
                dialog.setNegListener(negListener);
                dialog.setHelpListener(helpListener);
                dialog.showHelpIcon(true);
                dialog.show(((Activity) app).getFragmentManager(), dialog.getClass().getName());

            }
        });
    }

    public static void showSimpleDialog(@NonNull final Context app, @NonNull final String title, @NonNull final String message) {
        showCustomDialog(app, title, message, app.getString(R.string.AccessibilityLabels_close), null, new RDialogView.BROnClickListener() {
            @Override
            public void onClick(RDialogView rDialogView) {
                rDialogView.dismissWithAnimation();
            }
        }, null, null, 0);
    }

    //same but with a SpannableString as message to be able to click on a portion of the address with a listener
    public static void showCustomDialog(@NonNull final Context app, @NonNull final String title, @NonNull final SpannableString message,
                                        @NonNull final String posButton, final String negButton, final RDialogView.BROnClickListener posListener,
                                        final RDialogView.BROnClickListener negListener, final DialogInterface.OnDismissListener dismissListener, final int iconRes) {
        if (((Activity) app).isDestroyed()) {
            Log.e(TAG, "showCustomDialog: FAILED, context is destroyed");
            return;
        }

        RExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                dialog = new RDialogView();
                dialog.setTitle(title);
                dialog.setSpan(message);//setting Span instead of String
                dialog.setPosButton(posButton);
                dialog.setNegButton(negButton);
                dialog.setPosListener(posListener);
                dialog.setNegListener(negListener);
                dialog.setDismissListener(dismissListener);
                dialog.setIconRes(iconRes);
                dialog.show(((Activity) app).getFragmentManager(), dialog.getClass().getName());
            }
        });
    }

    public static void hideDialog() {
        if (dialog != null) dialog.dismiss();
    }
}
