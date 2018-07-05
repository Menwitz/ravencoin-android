package com.ravencoin.presenter.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ravencoin.R;
import com.ravencoin.presenter.activities.util.ActivityUTILS;
import com.ravencoin.presenter.activities.util.RActivity;
import com.ravencoin.presenter.entities.AddressItem;
import com.ravencoin.presenter.entities.BRSecurityCenterItem;
import com.ravencoin.presenter.entities.CryptoRequest;
import com.ravencoin.tools.animation.BRAnimator;
import com.ravencoin.tools.security.BRKeyStore;
import com.ravencoin.tools.util.RConstants;

import java.util.ArrayList;
import java.util.List;

public class AddressBookActivity extends RActivity {
    private static final String TAG = AddressBookActivity.class.getName();

    public ListView mListView;
    public RelativeLayout layout;
    public List<AddressItem> itemList;
    public static boolean appVisible = false;
    private static AddressBookActivity app;
    private ImageButton close;

    public static AddressBookActivity getApp() {
        return app;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        itemList = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.address_listview);
        mListView.addFooterView(new View(this), null, true);
        mListView.addHeaderView(new View(this), null, true);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setClickable(false);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address = ((TextView) view.findViewById(R.id.item_text)).getText().toString();
                CryptoRequest addressRequest = new CryptoRequest(address, true);
                BRAnimator.showSendFragment(AddressBookActivity.this, addressRequest);
            }
        });
        close = (ImageButton) findViewById(R.id.close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                onBackPressed();
            }
        });

        updateList();

        ImageButton faq = (ImageButton) findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                BRAnimator.showSupportFragment(app, RConstants.securityCenter);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
        appVisible = true;
        app = this;

        ActivityUTILS.changeStatusBarColor(this, R.color.logo_gradient_end);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (ActivityUTILS.isLast(this)) {
//            BRAnimator.startBreadActivity(this, false);
//        } else {
//            super.onBackPressed();
//        }
//        overridePendingTransition(R.anim.fade_up, R.anim.exit_to_bottom);
//    }

    @Override
    public void onBackPressed() {
        int c = getFragmentManager().getBackStackEntryCount();
        if (c > 0) {
            super.onBackPressed();
            return;
        }
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        if (!isDestroyed()) {
            finish();
        }
    }

    public RelativeLayout getMainLayout() {
        return layout;
    }

    public class AddressBookListAdapter extends ArrayAdapter<BRSecurityCenterItem> {

        private List<AddressItem> items;
        private Context mContext;
        private int defaultLayoutResource = R.layout.security_center_list_item;

        public AddressBookListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<AddressItem> items) {
            super(context, resource);
            this.items = items;
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

//            Log.e(TAG, "getView: pos: " + position + ", item: " + items.get(position));
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(defaultLayoutResource, parent, false);
            }
            TextView title = (TextView) convertView.findViewById(R.id.item_title);
            TextView text = (TextView) convertView.findViewById(R.id.item_text);
            ImageView checkMark = (ImageView) convertView.findViewById(R.id.check_mark);

            title.setText(items.get(position).title);
            text.setText(items.get(position).address);
//            convertView.setOnClickListener(items.get(position).listener);
            return convertView;

        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        appVisible = false;
    }

    private void updateList() {
        boolean isPinSet = BRKeyStore.getPinCode(this).length() == 6;
        itemList.clear();
//        itemList.add(new AddressItem(getString(R.string.SecurityCenter_pinTitle), getString(R.string.SecurityCenter_pinDescription)));
        itemList.add(new AddressItem("RVN", "Roshii", "RUoctN3wNK7DSzh6LV8fQvcgGWehVQEuu3"));
        itemList.add(new AddressItem("RVN","CryptoBridge", "RATPcCoqzMgmJ44Tkv1jY3o9KHTxmhPQao"));
        itemList.add(new AddressItem("RVN","Wallet Development", "RATPcCoqzMgmJ44Tkv1jY3o9KHTxmhPQao"));
        itemList.add(new AddressItem("RVN","Raven Marketing", "R9Ygjp3JTxnmpXMVczgRnknd7ebF8cGmQi"));
        itemList.add(new AddressItem("RVN","Donation", "R9Ygjp3JTxnmpXMVczgRnknd7ebF8cGmQi"));
        itemList.add(new AddressItem("RVN","Testnet", "RATPcCoqzMgmJ44Tkv1jY3o9KHTxmhPQao"));
        itemList.add(new AddressItem("RVN","Tokenize Assets", "R9Ygjp3JTxnmpXMVczgRnknd7ebF8cGmQi"));
        itemList.add(new AddressItem("RVN","Nano Exchange", "RATPcCoqzMgmJ44Tkv1jY3o9KHTxmhPQao"));
        itemList.add(new AddressItem("RVN","Ravencoin.org", "R9Ygjp3JTxnmpXMVczgRnknd7ebF8cGmQi"));

        mListView.setAdapter(new AddressBookListAdapter(this, R.layout.menu_list_item, itemList));
    }
}
