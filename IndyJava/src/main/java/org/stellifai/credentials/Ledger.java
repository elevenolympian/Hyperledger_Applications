package org.stellifai.credentials;

import com.google.common.util.concurrent.*;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidJSONParameters;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import utils.PoolUtils;

import static utils.PoolUtils.PROTOCOL_VERSION;
import static org.junit.Assert.assertEquals;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
public class Ledger  {
    private static final Logger logger = Logger.getLogger(Ledger.class.getName());
    private static final String trusteeSeed = "000000000000000000000000Trustee1";
    public static void initialize() throws Exception{
        logger.info("Ledger --> initialize");

        String trusteeSeed = "000000000000000000000000Trustee1";
        guavaConnector();
        createDid();
    }

    public static void openPoolLedger(boolean create, String poolName) throws IndyException, ExecutionException, InterruptedException, IOException {
        // Set protocol version 2 to work with Indy Node 1.4
        //String poolName = "pool1";
        Pool.setProtocolVersion(PROTOCOL_VERSION).get();
        if(true == create) {
            Pool.openPoolLedger(poolName, "{}").get();
        } else{
            poolName = PoolUtils.createPoolLedgerConfig();
            Pool.openPoolLedger(poolName, "{}").get();
        }
    }

    public static Wallet createWallet() throws IndyException, ExecutionException, InterruptedException {
        String myWalletConfig = new JSONObject().put("id", "my").toString();
        String myWalletCredentials = new JSONObject().put("key", "my_wallet_key").toString();
        Wallet.createWallet(myWalletConfig, myWalletCredentials).get();
        Wallet myWallet = Wallet.openWallet(myWalletConfig, myWalletCredentials).get();
        logger.info("myWallet info: " + myWallet.toString());
        return myWallet;

    }

    public static Wallet trusteeWallet() throws IndyException, ExecutionException, InterruptedException {
        String trusteeWalletConfig = new JSONObject().put("id", "theirWallet").toString();
        String trusteeWalletCredentials = new JSONObject().put("key", "trustee_wallet_key").toString();
        Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).get();
        Wallet trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get();
        return trusteeWallet;
    }

    public static void createDid() throws InterruptedException, ExecutionException, IndyException {
        DidResults.CreateAndStoreMyDidResult createMyDidResult = Did.createAndStoreMyDid(createWallet(), "{}").get();
        String myDid = createMyDidResult.getDid();
        String myVerkey = createMyDidResult.getVerkey();
        //Create DiD from the trustee seed pattern
        DidJSONParameters.CreateAndStoreMyDidJSONParameter didJSON = new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null);
        DidResults.CreateAndStoreMyDidResult createDidResult = Did.createAndStoreMyDid(trusteeWallet(), didJSON.toJson()).get();
        logger.info("newly created did result is here: "  + createDidResult);
    }

    public static ListenableFuture<Long> guavaConnector() {
        ExecutorService threadpool = Executors.newCachedThreadPool();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(threadpool);
        ListenableFuture<Long> testResult = (ListenableFuture<Long>) service.submit(() -> {
            try {
                openPoolLedger(true, "pool1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return testResult;
    }
}
