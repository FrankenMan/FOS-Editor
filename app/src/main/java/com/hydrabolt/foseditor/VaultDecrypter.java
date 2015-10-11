package com.hydrabolt.foseditor;

import android.util.Base64;
import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by amish on 11/10/2015.
 */
public class VaultDecrypter {

    DecryptState state;
    String fpath = "";
    String rawData = "";
    JSONObject data;

    public VaultDecrypter(String fname) {

        fpath = fname;

        state = DecryptState.WAITING;

        try {
            FileInputStream f = new FileInputStream(new File(fname));
            byte[] bytes = IOUtils.toByteArray(f);
            f.close();
            rawData = decryptBytes(bytes);

            state = DecryptState.SUCCESS;

            JSONParser parser = new JSONParser();
            data = (JSONObject) parser.parse(rawData);

        } catch (FileNotFoundException e){

            state = DecryptState.FILE_NOT_FOUND;

        } catch (Exception e) {

            state = DecryptState.ERROR;
        }
    }

    public EncryptState saveChanges(){

        try {
            byte[] encrypted = encrypt();

            FileOutputStream fo = new FileOutputStream(new File(fpath));
            fo.write(encrypted);
            fo.flush();
            fo.close();

            return EncryptState.SUCCESS;

        } catch (Exception e){
            return EncryptState.FAILURE;
        }

    }

    public String getVaultName(){
        return (String) ((JSONObject) this.data.get("vault")).get("VaultName");
    }

    public byte[] encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, DecoderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        byte[] toEncrypt = data.toJSONString().getBytes("UTF-8");

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte [] iv = Hex.decodeHex("7475383967656A693334307438397532".toCharArray());
        byte [] keyBytes = Hex.decodeHex("a7ca9f3366d892c2f0bef417341ca971b69ae9f7bacccffcf43c62d1d7d021f9".toCharArray());

        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

        c.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] encrypted = Base64.encode(c.doFinal(toEncrypt), Base64.DEFAULT);

        return encrypted;
    }

    public String decryptBytes(byte[] bytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, DecoderException, UnsupportedEncodingException {

        byte [] cipherBytes = Base64.decode(bytes, Base64.DEFAULT);

        byte [] iv = Hex.decodeHex("7475383967656A693334307438397532".toCharArray());
        byte [] keyBytes = Hex.decodeHex("a7ca9f3366d892c2f0bef417341ca971b69ae9f7bacccffcf43c62d1d7d021f9".toCharArray());

        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

        byte[] result = cipher.doFinal(cipherBytes);
        String s = new String(result, "US-ASCII");

        return s;
    }


}
