package Synchronizer;

import Main.Transactions;
import Main.Main;
import com.github.pwrlabs.pwrj.record.transaction.VmDataTransaction;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class Synchronizer {

    public static void sync() {
        new Thread(() -> {
            long startingBlock = 76474;
            while(true) {
                try {
                    long latestBlock = Main.pwrj.getLatestBlockNumber();
                    if(latestBlock > startingBlock + 1000) latestBlock = startingBlock + 1000;

                    if(latestBlock > startingBlock) {
                        VmDataTransaction[] txns = Main.pwrj.getVMDataTransactions(startingBlock, latestBlock, Main.vmId);

                        for(VmDataTransaction txn: txns) {
                            Transactions.remove(txn.getHash());
                            String sender = txn.getSender();
                            String dataHex = txn.getData(); //0x41253ef
                            byte[] data = Hex.decode(dataHex.substring(2));

                            JSONObject object = new JSONObject(new String(data, StandardCharsets.UTF_8));
                            for(String key: object.keySet()) {
                                if(key.equalsIgnoreCase("message")) {
                                    System.out.println("Message from " + sender + ": " + object.getString(key));
                                } else {
                                    //...
                                }
                            }
                        }

                        startingBlock = latestBlock + 1;
                    }
                    Thread.sleep(1000);
                    //System.out.println("Syncing...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
