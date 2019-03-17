package com.android.billingclient.api;

import android.util.Base64;

public class StringSecurity {
    private static String mKey = "KEEP YOUR ENCRYPTED KEY";

    private static String[] mCompositeKeys = {"nu2kPHr74GRkTuhV8xiHk8aolkzZ8rC7ZaLb","34PAThWShEQlPph1tG318afG/x+8k8LYDcep"};
    public static int weakEncrypt(byte[] msg, byte[] key){
        int i;
        for(i = 0; i < msg.length; i++){
            int keyOffset = i % key.length;
            msg[i] = (byte) (msg[i] ^ key[keyOffset]);
        }
        return i;
    }

    public static String decrypt(String encodedString){
        byte[] parts0 = Base64.decode(mCompositeKeys[0], Base64.DEFAULT);
        byte[] parts1 = Base64.decode(mCompositeKeys[1], Base64.DEFAULT);

        byte[] key = new byte[parts0.length];
        for(int i = 0; i < parts1.length; i++){
            key[i] = (byte) (parts0[i] ^ parts1[i]);
        }

        byte[] bytes = Base64.decode(encodedString, Base64.DEFAULT);
        weakEncrypt(bytes, key);
        return new String(bytes);
    }

    public static String encrypt(String msg){
        byte[] parts0 = Base64.decode(mCompositeKeys[0], Base64.DEFAULT);
        byte[] parts1 = Base64.decode(mCompositeKeys[1], Base64.DEFAULT);

        byte[] key = new byte[parts0.length];
        for(int i = 0; i < parts1.length; i++){
            key[i] = (byte) (parts0[i] ^ parts1[i]);
        }

        byte [] bytes = msg.getBytes();
        weakEncrypt(bytes, key);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String getKey(){
        return decrypt(mKey);
    }

    /**
     * A convenience method that generates a XOR key pair for a given key.
     *
     * @param key The source key to use in generating the XOR key halves
     * @return a two-value string array containing both parts of the XOR key
     */
    public static String[] generateKeyXorParts(String key){
        String[] keyParts = new String[2];

        byte[] xorRandom = new byte[key.length()];
        byte[] xorMatch = new byte[key.length()];
        byte[] keyBytes = key.getBytes();
        for(int i = 0; i < key.length(); i++){
            xorRandom[i] = (byte)(256 * Math.random());
            xorMatch[i] = (byte) (xorRandom[i] ^ keyBytes[i]);
        }
        keyParts[0] = Base64.encodeToString(xorRandom, 0);
        keyParts[1] = Base64.encodeToString(xorMatch, 0);
        return keyParts;
    }
}