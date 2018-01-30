package com.soriole.wallet.sqrapp.omni;

import com.soriole.wallet.lib.ByteUtils;
import com.soriole.wallet.lib.KeyGenerator;
import com.soriole.wallet.lib.WIF;
import com.soriole.wallet.lib.exceptions.ValidationException;
import com.soriole.wallet.sqrapp.neo.Neo;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author github.com/mesudip
 * Public Key and Wallet Address generation test for Neo wallet implementation
 */
public class OmniTest {

    private Omni omni=new Omni();
    JSONArray testArray;
    boolean success=true;
    public OmniTest() throws IOException, JSONException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL nemWalletJsonURL=classLoader.getResource("wallets/omniWallet.json");
        File file=new File(nemWalletJsonURL.getFile());
        testArray= new   JSONArray(FileUtils.readFileToString(file,"UTF-8"));

    }

    /**
     * <strong>Performs following:</strong>
     * <ol>
     *    <li>read private wif from json data</li>
     *    <li>extract private key from it</li>
     *    <li>generate public key using the private key</li>
     *    <li>generate address from the public key</li>
     *    <li>check the generated address with correct one</li>
     *    <li>also produce wif fromm public key</li>
     *    <li>chekc if it's same</li>
     * </ol>
     * @throws ValidationException
     * @throws IOException
     * @throws JSONException
     * @throws WIF.InvalidWIFException
     */
    @Test
    public void testAddress() throws ValidationException, IOException, JSONException, WIF.InvalidWIFException {
        boolean success=true;
        // iterate over each data for testing
        for(int i=0;i<testArray.length();i++){
            // get i'th data in the array
            JSONObject wallet= (JSONObject) testArray.get(i);

            // get the private wif string
            String correctWif=wallet.getString("privatewif");

            // generate public key from private key
            byte[] publicKey=omni.parseWIF(correctWif).getPublic();

            // generate wallet address from public key
            String address=omni.address(publicKey);

            // get the expected address form json data
            String correctAddress=wallet.getString("address");

            // now compare them. even if it is error, keep testing on  other data.
            if(!address.equals(correctAddress)){
                if(success) {
                    System.err.println("Omni Address Test");
                    success=false;
                }
                System.err.println("Expected : " + correctAddress);
                System.err.println("Got       : " + address);
            }
        }
        assert(success);
    }

    @Test
    public void testWIF() throws ValidationException, IOException, JSONException, WIF.InvalidWIFException {
        boolean success=true;
        // iterate over each data for testing
        for(int i=0;i<testArray.length();i++){
            // get i'th data in the array
            JSONObject wallet= (JSONObject) testArray.get(i);

            // get the private wif string
            String correctWif=wallet.getString("privatewif");

            // generate wif by encoding then decoding it
            String wif= omni.serializeWIF(omni.parseWIF(correctWif).getPrivate());
            if(!wif.equals(correctWif)){
                if(success) {
                    System.err.println("Omni Wif Test");
                    success=false;
                }
                System.err.println("Expected : " + correctWif);
                System.err.println("Got      : " + wif);
            }
        }
        assert(success);
    }


}
