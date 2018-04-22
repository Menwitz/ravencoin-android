package com.ravencoin.database;

import android.app.Activity;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ravencoin.presenter.activities.settings.TestActivity;
import com.ravencoin.presenter.entities.BRMerkleBlockEntity;
import com.ravencoin.presenter.entities.BRPeerEntity;
import com.ravencoin.presenter.entities.BRTransactionEntity;
import com.ravencoin.presenter.entities.BlockEntity;
import com.ravencoin.presenter.entities.CurrencyEntity;
import com.ravencoin.presenter.entities.PeerEntity;
import com.ravencoin.tools.sqlite.RvnTransactionDataStore;
import com.ravencoin.tools.sqlite.CurrencyDataSource;
import com.ravencoin.tools.sqlite.MerkleBlockDataSource;
import com.ravencoin.tools.sqlite.PeerDataSource;
import com.ravencoin.tools.threads.executor.BRExecutor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 9/30/16.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest

public class DatabaseTests {
    public static final String TAG = DatabaseTests.class.getName();
    final CountDownLatch signal = new CountDownLatch(1000);



    @Rule
    public ActivityTestRule<TestActivity> mActivityRule = new ActivityTestRule<>(TestActivity.class);

    @Before
    public void setUp() {
        Log.e(TAG, "setUp: ");
//        Activity app = mActivityRule.getActivity();
//        BRCoreMasterPubKey pubKey = new BRCoreMasterPubKey("cat circle quick rotate arena primary walnut mask record smile violin state".getBytes(), true);
//        BRKeyStore.putMasterPublicKey(pubKey.serialize(), app);
        cleanUp();

    }

    private void cleanUp() {
        Activity app = mActivityRule.getActivity();

        RvnTransactionDataStore.getInstance(app).deleteAllTransactions(app, "RVN");

        CurrencyDataSource.getInstance(app).deleteAllCurrencies(app, "RVN");

        MerkleBlockDataSource.getInstance(app).deleteAllBlocks(app, "RVN");

        PeerDataSource.getInstance(app).deleteAllPeers(app, "RVN");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testSetLocal() {

        // Test RVN transaction insert
        Activity app = mActivityRule.getActivity();
        RvnTransactionDataStore tds = RvnTransactionDataStore.getInstance(app);
        tds.putTransaction(app, "RVN", new BRTransactionEntity(new byte[0], 1234, 4314123, "some hash", "RVN"));
        List<BRTransactionEntity> txs = tds.getAllTransactions(app, "RVN");
        Assert.assertNotNull(txs);
        Assert.assertEquals(txs.size(), 1);
        Assert.assertArrayEquals(txs.get(0).getBuff(), new byte[0]);
        Assert.assertEquals(txs.get(0).getBlockheight(), 1234);
        Assert.assertEquals(txs.get(0).getTimestamp(), 4314123);
        Assert.assertEquals(txs.get(0).getTxHash(), "some hash");


        // Test BCH Transaction insert
        RvnTransactionDataStore bchInsert = RvnTransactionDataStore.getInstance(app);
        tds.putTransaction(app, "BCH", new BRTransactionEntity("some bytes".getBytes(), 4321, 5674123, "some hash", "BCH"));
        List<BRTransactionEntity> bchTxs = bchInsert.getAllTransactions(app, "BCH");
        Assert.assertNotNull(bchTxs);
        Assert.assertEquals(bchTxs.size(), 1);
        Assert.assertArrayEquals(bchTxs.get(0).getBuff(), "some bytes".getBytes());
        Assert.assertEquals(bchTxs.get(0).getBlockheight(), 4321);
        Assert.assertEquals(bchTxs.get(0).getTimestamp(), 5674123);
        Assert.assertEquals(bchTxs.get(0).getTxHash(), "some hash");
        Assert.assertEquals(bchTxs.get(0).getTxISO().toLowerCase(), "BCH".toLowerCase());

        //Test BCH transaction update
        BRTransactionEntity toUpdate = bchTxs.get(0);
        Assert.assertNotNull(toUpdate);
        toUpdate.setBlockheight(8123);
        toUpdate.setTimestamp(9382312);
        boolean b = bchInsert.updateTransaction(app, "BCH", toUpdate);
        Assert.assertTrue(b);
        bchTxs = bchInsert.getAllTransactions(app, "BCH");
        Assert.assertNotNull(bchTxs);
        Assert.assertEquals(bchTxs.size(), 1);
        Assert.assertArrayEquals(bchTxs.get(0).getBuff(), "some bytes".getBytes());
        Assert.assertEquals(bchTxs.get(0).getBlockheight(), 8123);
        Assert.assertEquals(bchTxs.get(0).getTimestamp(), 9382312);
        Assert.assertEquals(bchTxs.get(0).getTxHash(), "some hash");
        Assert.assertEquals(bchTxs.get(0).getTxISO().toLowerCase(), "bch");


        MerkleBlockDataSource mds = MerkleBlockDataSource.getInstance(mActivityRule.getActivity());
        mds.putMerkleBlocks(app, "RVN", new BlockEntity[]{new BlockEntity("SOme cool stuff".getBytes(), 123343)});
        List<BRMerkleBlockEntity> ms = mds.getAllMerkleBlocks(app, "RVN");
        Assert.assertNotNull(ms);
        Assert.assertEquals(ms.size(), 1);
        Assert.assertArrayEquals(ms.get(0).getBuff(), "SOme cool stuff".getBytes());
        Assert.assertEquals(ms.get(0).getBlockHeight(), 123343);

        MerkleBlockDataSource bchMds = MerkleBlockDataSource.getInstance(mActivityRule.getActivity());
        bchMds.putMerkleBlocks(app, "BCH", new BlockEntity[]{new BlockEntity("SOme cool stuff BCH".getBytes(), 123343)});
        List<BRMerkleBlockEntity> bchMs = bchMds.getAllMerkleBlocks(app, "BCH");
        Assert.assertNotNull(bchMs);
        Assert.assertEquals(bchMs.size(), 1);
        Assert.assertArrayEquals(bchMs.get(0).getBuff(), "SOme cool stuff BCH".getBytes());
        Assert.assertEquals(bchMs.get(0).getBlockHeight(), 123343);

        PeerDataSource pds = PeerDataSource.getInstance(mActivityRule.getActivity());
        pds.putPeers(app, "RVN", new PeerEntity[]{new PeerEntity("someAddress".getBytes(), "somePort".getBytes(), "someTimestamp".getBytes())});
        List<BRPeerEntity> ps = pds.getAllPeers(app, "RVN");
        Assert.assertNotNull(ps);
        Assert.assertEquals(ps.size(), 1);
        Assert.assertArrayEquals(ps.get(0).getAddress(), "someAddress".getBytes());
        Assert.assertArrayEquals(ps.get(0).getPort(), "somePort".getBytes());
        Assert.assertArrayEquals(ps.get(0).getTimeStamp(), "someTimestamp".getBytes());


        // Test inserting OMG as a currency
        CurrencyDataSource cds = CurrencyDataSource.getInstance(mActivityRule.getActivity());
        List<CurrencyEntity> toInsert = new ArrayList<>();
        CurrencyEntity ent = new CurrencyEntity();
        ent.code = "OMG";
        ent.name = "OmiseGo";
        ent.rate = 8.43f;
        toInsert.add(ent);
        cds.putCurrencies(app, "RVN", toInsert);
        List<CurrencyEntity> cs = cds.getAllCurrencies(app, "RVN");
        Assert.assertNotNull(cs);
        Assert.assertEquals(cs.size(), 1);
        Assert.assertEquals(cs.get(0).name, "OmiseGo");
        Assert.assertEquals(cs.get(0).code, "OMG");
        Assert.assertEquals(cs.get(0).rate, 8.43f, 0);

        // Test inserting RVN as a currency
        toInsert = new ArrayList<>();

        CurrencyEntity rvnEntity = new CurrencyEntity();
        rvnEntity.code = "ETH";
        rvnEntity.name = "Ether";
        rvnEntity.rate = 6f;
        toInsert.add(rvnEntity);
        cds.putCurrencies(app, "bch", toInsert);

        List<CurrencyEntity> btcCurs = cds.getAllCurrencies(app, "RVN");
        List<CurrencyEntity> bchCurs = cds.getAllCurrencies(app, "BCH");
        Assert.assertNotNull(btcCurs);
        Assert.assertNotNull(bchCurs);
        Assert.assertEquals(btcCurs.size(), 1);
        Assert.assertEquals(bchCurs.size(), 1);
        Assert.assertEquals(bchCurs.get(0).name, "Ether");
        Assert.assertEquals(bchCurs.get(0).code, "ETH");
        Assert.assertEquals(bchCurs.get(0).rate, 6f, 0);

        Assert.assertEquals(btcCurs.get(0).name, "OmiseGo");
        Assert.assertEquals(btcCurs.get(0).code, "OMG");
        Assert.assertEquals(btcCurs.get(0).rate, 8.43f, 0);


    }

    private class InsertionThread extends Thread {

        @Override
        public void run() {
            super.run();

            // Test BCH Transaction insert
            Activity app = mActivityRule.getActivity();
            RvnTransactionDataStore tds = RvnTransactionDataStore.getInstance(app);
            RvnTransactionDataStore bchInsert = RvnTransactionDataStore.getInstance(app);
            tds.putTransaction(app, "RVN", new BRTransactionEntity(new byte[0], 4321, 5674123, "some hash", "RVN"));
            List<BRTransactionEntity> bchTxs = bchInsert.getAllTransactions(app, "RVN");
            Assert.assertNotNull(bchTxs);
            Assert.assertEquals(bchTxs.size(), 1);
            Assert.assertArrayEquals(bchTxs.get(0).getBuff(), new byte[0]);
            Assert.assertEquals(bchTxs.get(0).getBlockheight(), 4321);
            Assert.assertEquals(bchTxs.get(0).getTimestamp(), 5674123);
            Assert.assertEquals(bchTxs.get(0).getTxHash(), "some hash");
            Assert.assertEquals(bchTxs.get(0).getTxISO(), "bch");

        }
    }

    private synchronized void done() {
        signal.countDown();
    }

    @Test
    public void testConcurrentTransactionInsertion() {

        final Activity app = mActivityRule.getActivity();


        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                RvnTransactionDataStore tds = RvnTransactionDataStore.getInstance(app);
                tds.putTransaction(app, "RVN", new BRTransactionEntity(new byte[0], 1989, 00112233, "first", "RVN"));
                done();
            }
        });


        RvnTransactionDataStore tds2 = RvnTransactionDataStore.getInstance(app);
        tds2.putTransaction(app, "RVN", new BRTransactionEntity(new byte[1], 1990, 11223344, "second", "RVN"));
        done();

        RvnTransactionDataStore tds3 = RvnTransactionDataStore.getInstance(app);
        tds3.putTransaction(app, "RVN", new BRTransactionEntity(new byte[2], 1991, 22334455, "third", "RVN"));
        done();


    }

    @Test
    public void testAsynchronousInserts() {
        final Activity app = mActivityRule.getActivity();
        for (int i = 0; i < 1000; i++) {
            final int finalI = i;
            BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                @Override
                public void run() {

                    RvnTransactionDataStore tds = RvnTransactionDataStore.getInstance(app);
                    tds.putTransaction(app, "RVN", new BRTransactionEntity(String.valueOf(finalI).getBytes(), finalI, finalI, String.valueOf(finalI), "RVN"));
                    done();
                }
            });
        }
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "testAsynchronousInserts: Done waiting!");
        RvnTransactionDataStore tds = RvnTransactionDataStore.getInstance(app);
        List<BRTransactionEntity> txs = tds.getAllTransactions(app, "RVN");
        Assert.assertNotNull(txs);
        Assert.assertEquals(txs.size(), 1000);


//        MerkleBlockDataSource mds = MerkleBlockDataSource.getInstance(mActivityRule.getActivity());
//        mds.putMerkleBlocks(new BlockEntity[]{new BlockEntity("SOme cool stuff".getBytes(), 123343)});
//        List<BRMerkleBlockEntity> ms = mds.getAllMerkleBlocks();
//        Assert.assertNotNull(ms);
//        Assert.assertEquals(ms.size(), 1);
//        Assert.assertArrayEquals(ms.get(0).getBuff(), "SOme cool stuff".getBytes());
//        Assert.assertEquals(ms.get(0).getBlockHeight(), 123343);
//
//        PeerDataSource pds = PeerDataSource.getInstance(mActivityRule.getActivity());
//        pds.putPeers(new PeerEntity[]{new PeerEntity("someAddress".getBytes(), "somePort".getBytes(), "someTimestamp".getBytes())});
//        List<BRPeerEntity> ps = pds.getAllPeers();
//        Assert.assertNotNull(ps);
//        Assert.assertEquals(ps.size(), 1);
//        Assert.assertArrayEquals(ps.get(0).getAddress(), "someAddress".getBytes());
//        Assert.assertArrayEquals(ps.get(0).getPort(), "somePort".getBytes());
//        Assert.assertArrayEquals(ps.get(0).getTimeStamp(), "someTimestamp".getBytes());
//
//        CurrencyDataSource cds = CurrencyDataSource.getInstance(mActivityRule.getActivity());
//        List<CurrencyEntity> toInsert = new ArrayList<>();
//        CurrencyEntity ent = new CurrencyEntity();
//        ent.code = "OMG";
//        ent.name = "OmiseGo";
//        ent.rate = 8.43f;
//        toInsert.add(ent);
//        cds.putCurrencies(toInsert);
//        List<CurrencyEntity> cs = cds.getAllCurrencies();
//        Assert.assertNotNull(cs);
//        Assert.assertEquals(cs.size(), 1);
//        Assert.assertEquals(cs.get(0).name, "OmiseGo");
//        Assert.assertEquals(cs.get(0).code, "OMG");
//        Assert.assertEquals(cs.get(0).rate, 8.43f, 0);

    }

}
