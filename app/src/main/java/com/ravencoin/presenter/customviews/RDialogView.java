package com.ravencoin.presenter.customviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ravencoin.R;
import com.ravencoin.tools.animation.BRAnimator;
import com.ravencoin.tools.manager.RReportsManager;
import com.ravencoin.tools.util.Utils;

public class RDialogView extends DialogFragment {

    private static final String TAG = RDialogView.class.getName();

    private String title = "";
    private String message = "";
    private String posButton = "";
    private String negButton = "";
    private RDialogView.BROnClickListener posListener;
    private RDialogView.BROnClickListener negListener;
    private RDialogView.BROnClickListener helpListener;
    private DialogInterface.OnDismissListener dismissListener;
    private int iconRes = 0;
    private RButton negativeButton;
    private RButton positiveButton;
    private LinearLayout buttonsLayout;
    private ImageButton helpButton;
    private LinearLayout mainLayout;

    //provide the way to have clickable span in the message
    private SpannableString spanMessage;

    private boolean showHelpIcon;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bread_alert_dialog, null);
        TextView titleText =  view.findViewById(R.id.dialog_title);
        TextView messageText =  view.findViewById(R.id.dialog_text);
        RButton positiveButton = view.findViewById(R.id.pos_button);
        negativeButton = view.findViewById(R.id.neg_button);
//        ImageView icon =  view.findViewById(R.id.dialog_icon);
        mainLayout = view.findViewById(R.id.main_layout);
        buttonsLayout =  view.findViewById(R.id.linearLayout3);
        helpButton = view.findViewById(R.id.help_icon);


        // Resize the title address if it is greater than 4 lines
        titleText.setText(title);
        if (titleText.getLineCount() > 4) {
            titleText.setTextSize(16);
        }


        // Resize the message address if it is greater than 4 lines
        messageText.setText(message);
        if (messageText.getLineCount() > 4) {
            messageText.setTextSize(16);
        }
        if (spanMessage != null) {
            messageText.setText(spanMessage);
            messageText.setMovementMethod(LinkMovementMethod.getInstance());
        }

        positiveButton.setColor(Color.parseColor("#4b77f3"));
        positiveButton.setHasShadow(false);
        positiveButton.setText(posButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                if (posListener != null)
                    posListener.onClick(RDialogView.this);
            }
        });
        if (Utils.isNullOrEmpty(negButton)) {
            Log.e(TAG, "onCreateDialog: removing negative button");
            buttonsLayout.removeView(negativeButton);
            buttonsLayout.requestLayout();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.weight = 1.0f;
            positiveButton.setLayoutParams(params);
        }

        negativeButton.setColor(Color.parseColor("#b3c0c8"));
        negativeButton.setHasShadow(false);
        negativeButton.setText(negButton);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                if (negListener != null)
                    negListener.onClick(RDialogView.this);
            }
        });
//        if (iconRes != 0)
//            icon.setImageResource(iconRes);

        builder.setView(view);

        if (showHelpIcon) {
            helpButton.setVisibility(View.VISIBLE);

            messageText.setPadding(0, 0, 0, Utils.getPixelsFromDps(getContext(), 16));

            helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!BRAnimator.isClickAllowed()) return;
                    if (helpListener != null)
                        helpListener.onClick(RDialogView.this);
                }
            });

        } else {
            helpButton.setVisibility(View.INVISIBLE);

        }
//        builder.setOnDismissListener(dismissListener);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void showHelpIcon(boolean show) {
        this.showHelpIcon = show;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null)
            dismissListener.onDismiss(dialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSpan(SpannableString message) {
        if (message == null) {
            RReportsManager.reportBug(new NullPointerException("setSpan with null message"));
            return;
        }
        this.spanMessage = message;
    }

    public void setPosButton(@NonNull String posButton) {
        this.posButton = posButton;
    }

    public void setNegButton(String negButton) {
        this.negButton = negButton;
    }

    public void setPosListener(RDialogView.BROnClickListener posListener) {
        this.posListener = posListener;
    }

    public void setNegListener(RDialogView.BROnClickListener negListener) {
        this.negListener = negListener;
    }

    public void setHelpListener(BROnClickListener helpListener) {
        this.helpListener = helpListener;
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public static interface BROnClickListener {
        void onClick(RDialogView rDialogView);
    }

    public void dismissWithAnimation() {
        RDialogView.this.dismiss();

    }

//    public interface SpanClickListener {
//        void onClick();
//
//    }

}
