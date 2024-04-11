package Main;

import com.github.pwrlabs.pwrj.Utils.Hash;
import com.github.pwrlabs.pwrj.Utils.Hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions {
    private static Map<String /*Txn Hash*/, byte[]> transactionsAwaitingApproval = new HashMap<>();

    public static void add(byte[] txn) {
        byte[] txnHash = Hash.sha256(txn);
        String txnHashHex = "0x" + Hex.toHexString(txnHash); //236457

        transactionsAwaitingApproval.put(txnHashHex, txn);
    }

    public static void remove(String txnHash) {
        transactionsAwaitingApproval.remove(txnHash);
    }

    public static List<byte[]> getPendingTransactions() {
        return new ArrayList<>(transactionsAwaitingApproval.values());
    }
}
