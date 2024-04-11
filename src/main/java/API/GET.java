package API;

import Main.Transactions;
import com.github.pwrlabs.pwrj.Utils.Hash;
import com.github.pwrlabs.pwrj.Utils.Hex;
import org.json.JSONArray;

import java.util.List;

import static spark.Spark.get;

public class GET {

    public void run() {

        get("/pendingVmTransactions", (request, response) -> {
            List<byte[]> pendingTransactions = Transactions.getPendingTransactions();

            JSONArray array = new JSONArray();
            for(byte[] txn: pendingTransactions) {
                array.put(Hex.toHexString(txn));
            }

            for(byte[] txn: pendingTransactions) {
                byte[] txnHash = Hash.sha256(txn);
                String txnHashHex = "0x" + Hex.toHexString(txnHash); //236457
                Transactions.remove(txnHashHex);
            }
        });
    }
}
