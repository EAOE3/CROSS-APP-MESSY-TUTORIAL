package Main;

import com.github.pwrlabs.pwrj.Utils.Hash;
import com.github.pwrlabs.pwrj.Utils.Hex;
import com.github.pwrlabs.pwrj.protocol.PWRJ;
import com.github.pwrlabs.pwrj.protocol.TransactionBuilder;
import com.github.pwrlabs.pwrj.record.response.Response;
import com.github.pwrlabs.pwrj.record.validator.Validator;
import com.github.pwrlabs.pwrj.wallet.PWRWallet;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static final long vmId = 4567890;

    private static final String RPC_URL = "https://pwrrpc.pwrlabs.io/";
    private static final BigInteger PRIVATE_KEY_1 = new BigInteger("13441705239710856426490937717111545450041915423641316365679523930528732611559");
    private static final BigInteger PRIVATE_KEY_2 = new BigInteger("65667622470184592671268428677185924916315539718461627986432216206742674338707");

    public static final PWRJ pwrj = new PWRJ(RPC_URL);

    public static void main(String[] args) throws Exception {
       // PWRJ pwrj = new PWRJ(RPC_URL);
        PWRWallet wallet1 = new PWRWallet(PRIVATE_KEY_1, pwrj);
        PWRWallet wallet2 = new PWRWallet(PRIVATE_KEY_2, pwrj);

        System.out.println(wallet1.getAddress());
        System.out.println(wallet2.getAddress());

        Response r = wallet1.claimVmId(vmId, wallet1.getNonce());
        System.out.println(r.isSuccess());
        System.out.println(r.getTransactionHash());
        System.out.println(r.getError());

        //Claiming VM ID
        while(pwrj.getOwnerOfVm(vmId) == null) {
            Thread.sleep(1000);
        }

        System.out.println("Owner of VM " + vmId + " is " + pwrj.getOwnerOfVm(vmId));

        //Creating our own valdiator
        if(!isValidator(wallet2.getAddress(), pwrj)) {
            Response r1 = wallet2.join("567.678.789.789", wallet2.getNonce());
            System.out.println(r1.isSuccess());
            System.out.println(r1.getTransactionHash());
            System.out.println(r1.getError());
        }

        while (!isValidator(wallet2.getAddress(), pwrj)) {
            Thread.sleep(1000);
        }

        System.out.println(wallet2.getAddress()  + " is now a validator");

        //Setting our conduits
        byte[] conduitAddress = Hex.decode(wallet2.getAddress());
        Response r2 = wallet1.setConduits(vmId, List.of(conduitAddress), wallet1.getNonce());
        System.out.println(r2.isSuccess());
        System.out.println(r2.getTransactionHash());
        System.out.println(r2.getError());

        List<Validator> conduits = pwrj.getConduitsOfVm(vmId);
        while(conduits.size() == 0) {
            Thread.sleep(1000);
            conduits = pwrj.getConduitsOfVm(vmId);
        }

        for(Validator v : conduits) {
            System.out.println("Conduit of our vm: " + v.getAddress());
        }

        System.out.println("Conduits of VM " + vmId + " set");

        //Step 3
        String vmAddress = pwrj.getVmIdAddress(vmId);
        System.out.println(vmAddress);
        int nonce = pwrj.getNonceOfAddress(vmAddress);

        long targetVMId = 1234567;

        JSONObject object = new JSONObject();
        object.put("message", "Hello from VM " + vmId);

        byte[] txn = TransactionBuilder.getVmDataTransaction(targetVMId, object.toString().getBytes(), nonce, pwrj.getChainId());
        Transactions.add(txn);
    }

    private static boolean isValidator(String validatorAddress, PWRJ pwrj) throws Exception {
        List<Validator> validators = pwrj.getAllValidators();
        for (Validator v : validators) {
            if (v.getAddress().equalsIgnoreCase(validatorAddress)) {
                return true;
            }
        }
        return false;
    }


}
