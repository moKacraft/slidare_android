package project.eip.epitech.slidare.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

//Android import
import android.util.Base64;
//import org.apache.commons.codec.binary.Base64;

/**
 *
 * Created by Madeline on 02/06/2017 at 02:11.
 *
 */
public class encryptUtils {

//    /**
//     * Encryption mode enumeration
//     */
//    private enum EncryptMode {
//        ENCRYPT, DECRYPT;
//    }

    private final String ENCRYPTION_INSTANCE = "AES/CBC/PKCS5Padding";
    // cipher to be used for encryption and decryption
    private Cipher _cx;
    // encryption key and initialization vector
    private byte[] _key, _iv;

    /**
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public encryptUtils()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            UnsupportedEncodingException {
        // initialize the cipher with transformation AES/CBC/PKCS5Padding
        _cx = Cipher.getInstance(ENCRYPTION_INSTANCE);
        // 256 bit key space
        _key = new byte[32];
        // 128 bit IV
        _iv = new byte[16];
    }

//    /**
//     * Note: This function is no longer used.
//     * This function generates md5 hash of the input string
//     * @param inputString
//     * @return md5 hash of the input string
//     */
//    public static final String md5(final String inputString) {
//        final String MD5 = "MD5";
//        try {
//            // Create MD5 Hash
//            MessageDigest digest = java.security.MessageDigest
//                    .getInstance(MD5);
//            digest.update(inputString.getBytes());
//            byte messageDigest[] = digest.digest();
//
//            // Create Hex String
//            StringBuilder hexString = new StringBuilder();
//            for (byte aMessageDigest : messageDigest) {
//                String h = Integer.toHexString(0xFF & aMessageDigest);
//                while (h.length() < 2)
//                    h = "0" + h;
//                hexString.append(h);
//            }
//            return hexString.toString();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

//    private SecretKeySpec genSecretKeySpec(
//            String key,
//            int size)
//            throws UnsupportedEncodingException,
//            NoSuchAlgorithmException {
//        String shaReturn = this.SHA256(key, size);
//        this.setKey(shaReturn);
//        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES");
//        return keySpec;
//    }
//
//    private void setKey(
//            String key)
//            throws UnsupportedEncodingException {
//        int keyLen = key.getBytes("UTF-8").length;
//        if (key.getBytes("UTF-8").length > _key.length)
//            keyLen = _key.length;
//        System.arraycopy(key.getBytes("UTF-8"), 0, _key, 0, keyLen);
//    }

    /**
     * A routine for encrypting and decrypting bytes.
     *
     * @param _inputTextBytes
     *          Bytes to be encrypted or decrypted
     * @param _encryptionKey
     *          Encryption key to used for encryption / decryption
     * @param _initVector
     *          Initialization vector
     * @param cipherMode
     *          Specify the cipher mode ENCRYPTION / DECRYPTION
     * @return  Encrypted or decrypted bytes based on the mode
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws UnsupportedEncodingException
     */
    private byte[] doCrypto(
            byte[] _inputTextBytes,
            String _encryptionKey,
            String _initVector,
            int cipherMode)
            throws InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException,
            UnsupportedEncodingException {
        byte[] outputBytes;

        int keyLen = _encryptionKey.getBytes("UTF-8").length;
        if (_encryptionKey.getBytes("UTF-8").length > _key.length)
            keyLen = _key.length;
        System.arraycopy(_encryptionKey.getBytes("UTF-8"), 0, _key, 0, keyLen);

        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES");

        int ivLen = _initVector.getBytes("UTF-8").length;
        if(_initVector.getBytes("UTF-8").length > _iv.length)
            ivLen = _iv.length;
        System.arraycopy(_initVector.getBytes("UTF-8"), 0, _iv, 0, ivLen);

        IvParameterSpec ivSpec = new IvParameterSpec(_iv);

        _cx.init(cipherMode, keySpec, ivSpec);
        outputBytes = _cx.doFinal(_inputTextBytes);

        return outputBytes;
    }

    /**
     *
     * @param toEncryptFileName
     *          Filename of file to encrypt
     * @param encryptedFileName
     *          File name of file that will be encrypted
     * @param _encryptionKey
     *          Encryption key
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public void encryptFile(
            String toEncryptFileName,
            String encryptedFileName,
            String _encryptionKey)
            throws IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidParameterSpecException,
            BadPaddingException,
            IllegalBlockSizeException {

        // file to be encrypted
        FileInputStream inFile = new FileInputStream(toEncryptFileName);
        // encrypted file
        FileOutputStream outFile = new FileOutputStream(encryptedFileName);

        // password, iv and salt should be transferred to the other end
        // in a secure manner

        // salt is used for encoding, writing it to a file
        // salt should be transferred to the recipient securely
        // for decryption
        byte[] salt = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        FileOutputStream saltOutFile = new FileOutputStream("salt.enc");
        saltOutFile.write(salt);
        saltOutFile.close();

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // byte[] pass, byte[] salt, int ITERATIONS, int KEY_LENGTH
        KeySpec keySpec = new PBEKeySpec(_encryptionKey.toCharArray(), salt, 65536, 256);
        SecretKey tmp= factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance(ENCRYPTION_INSTANCE);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();

        // iv adds randomness to the text and just makes the mechanism more
        // secure, used while initializing the cipher file to store the iv
        FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        ivOutFile.write(iv);
        ivOutFile.close();

        // File encryption
        byte[] input = new byte[64];
        int bytesRead;

        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null)
                outFile.write(output);
        }

        byte[] output = cipher.doFinal();

        if (output != null)
            outFile.write(output);
        inFile.close();
        outFile.flush();
        outFile.close();
    }

    /**
     *
     * @param toDecryptFileName
     *          File name of file to decrypt
     * @param decryptedFileName
     *          File name of file that will be decrypted
     * @param _encryptionKey
     *          Encryption key
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public void decryptFile(
            String toDecryptFileName,
            String decryptedFileName,
            String _encryptionKey)
            throws IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {
        // File to be decrypted
        FileInputStream fis = new FileInputStream(toDecryptFileName);
        // Decrypted file
        FileOutputStream fos = new FileOutputStream(decryptedFileName);

        // reading the salt
        // user should have secure mechanism to transfer the
        // salt, iv and password to the recipient
        FileInputStream saltFis = new FileInputStream("salt.enc");
        byte[] salt = new byte[8];
        saltFis.read(salt);
        saltFis.close();

        // reading the iv
        FileInputStream ivFis = new FileInputStream("iv.enc");
        byte[] iv = new byte[16];
        ivFis.read(iv);
        ivFis.close();

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // byte[] pass, byte[] salt, int ITERATIONS, int KEY_LENGTH
        KeySpec keySpec = new PBEKeySpec(_encryptionKey.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance(ENCRYPTION_INSTANCE);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

        // File decryption
        byte[] in = new byte[64];
        int read;

        while ((read = fis.read(in)) != -1) {
            byte[] output = cipher.update(in, 0, read);
            if (output != null)
                fos.write(output);
        }

        byte[] output = cipher.doFinal();

        if (output != null)
            fos.write(output);
        fis.close();
        fos.flush();
        fos.close();
    }

    /**
     * This function encrypts the plain text to cipher text using the key
     * provided. You'll have to use the same key for decryption.
     *
     * @param toEncryptString
     *          String to be encrypted
     * @param _encryptionKey
     *          Encryption key to used for encryption
     * @param _initVector
     *          Initialization vector
     * @return  Encrypted string
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */
    public String encryptString(
            String toEncryptString,
            String _encryptionKey,
            String _initVector)
            throws UnsupportedEncodingException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidAlgorithmParameterException,
            InvalidKeyException {
        byte[] toEncryptBytes = toEncryptString.getBytes("UTF-8");
        byte[] doCryptoReturn = doCrypto(toEncryptBytes, _encryptionKey, _initVector, Cipher.ENCRYPT_MODE);
        //return Base64.encodeBase64String(doCryptoReturn);
        // Android
        return Base64.encodeToString(doCryptoReturn, Base64.DEFAULT);
    }

    /**
     * This function decrypts the encrypted from {@link #encryptString(String, String, String)} text to plain text using the key
     * provided. You'll have to use the same key which you used during
     * encryption.
     *
     * @param toDecryptString
     *          String to be decrypted
     * @param _encryptionKey
     *          Encryption key to used for decryption
     * @param _initVector
     *          Initialization vector
     * @return  Decrypted string
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public String decryptString(
            String toDecryptString,
            String _encryptionKey,
            String _initVector)
            throws IllegalBlockSizeException,
            BadPaddingException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            UnsupportedEncodingException {
        //byte[] toDecryptBytes = Base64.decodeBase64(toDecryptString.getBytes());
        // Android
        byte[] toDecryptBytes = Base64.decode(toDecryptString.getBytes(),Base64.DEFAULT);
        byte[] doCryptoReturn = doCrypto(toDecryptBytes, _encryptionKey, _initVector, Cipher.DECRYPT_MODE);
        return new String(doCryptoReturn);
    }

//    /**
//     *
//     * @param _inputText
//     *            Text to be encrypted or decrypted
//     * @param _encryptionKey
//     *            Encryption key to used for encryption / decryption
//     * @param _mode
//     *            specify the mode encryption / decryption
//     * @param _initVector
//     * 	      Initialization vector
//     * @return encrypted or decrypted string based on the mode
//     * @throws UnsupportedEncodingException
//     * @throws InvalidKeyException
//     * @throws InvalidAlgorithmParameterException
//     * @throws IllegalBlockSizeException
//     * @throws BadPaddingException
//     */
//    private String encryptDecrypt(
//            String _inputText,
//            String _encryptionKey,
//            EncryptMode _mode,
//            String _initVector)
//            throws UnsupportedEncodingException,
//            InvalidKeyException, InvalidAlgorithmParameterException,
//            IllegalBlockSizeException, BadPaddingException {
//        // output string
//        String _out = "";
//
//        //_encryptionKey = md5(_encryptionKey);
//        //System.out.println("key="+_encryptionKey);
//
//        // length of the key	provided
//        int len = _encryptionKey.getBytes("UTF-8").length;
//
//        if (_encryptionKey.getBytes("UTF-8").length > _key.length)
//            len = _key.length;
//
//        int ivlen = _initVector.getBytes("UTF-8").length;
//
//        if(_initVector.getBytes("UTF-8").length > _iv.length)
//            ivlen = _iv.length;
//
//        System.arraycopy(_encryptionKey.getBytes("UTF-8"), 0, _key, 0, len);
//        System.arraycopy(_initVector.getBytes("UTF-8"), 0, _iv, 0, ivlen);
//
//        //KeyGenerator _keyGen = KeyGenerator.getInstance("AES");
//        //_keyGen.init(128);
//
//        // Create a new SecretKeySpec for the specified key data and
//        // algorithm name.
//        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES");
//
//        // Create a new IvParameterSpec instance with the bytes from the
//        // specified buffer iv used as initialization vector.
//        IvParameterSpec ivSpec = new IvParameterSpec(_iv);
//
//        // encryption
//        if (_mode.equals(EncryptMode.ENCRYPT)) {
//            // Potentially insecure random numbers on Android 4.3 and older.
//            // Read
//            // https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
//            // for more info.
//
//            // Initialize this cipher instance
//            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//            // Finish multi-part transformation (encryption)
//            byte[] results = _cx.doFinal(_inputText.getBytes("UTF-8"));
//
//            // ciphertext output Android
//            //_out = Base64.encodeToString(results, Base64.DEFAULT);
//            // ciphertext output
//            _out = Base64.encodeBase64String(results);
//        }
//
//        // decryption
//        if (_mode.equals(EncryptMode.DECRYPT)) {
//            // Initialize this cipher instance
//            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//            //byte[] decodedValue = Base64.decode(_inputText.getBytes(),Base64.DEFAULT);
//            byte[] decodedValue = Base64.decodeBase64(_inputText.getBytes());
//
//            // Finish multi-part transformation(decryption)
//            byte[] decryptedVal = _cx.doFinal(decodedValue);
//            // decrypttext output
//            _out = new String(decryptedVal);
//        }
//        // return encrypted/decrypted string
//        return _out;
//    }
//
//    /***
//     * This function encrypts the plain text to cipher text using the key
//     * provided. You'll have to use the same key for decryption
//     *
//     * @param _plainText
//     *            Plain text to be encrypted
//     * @param _key
//     *            Encryption Key. You'll have to use the same key for decryption
//     * @param _iv
//     * 	    initialization Vector
//     * @return returns encrypted (cipher) text
//     * @throws InvalidKeyException
//     * @throws UnsupportedEncodingException
//     * @throws InvalidAlgorithmParameterException
//     * @throws IllegalBlockSizeException
//     * @throws BadPaddingException
//     */
//    public String encrypt(
//            String _plainText,
//            String _key,
//            String _iv)
//            throws InvalidKeyException, UnsupportedEncodingException,
//            InvalidAlgorithmParameterException, IllegalBlockSizeException,
//            BadPaddingException {
//        return encryptDecrypt(_plainText, _key, EncryptMode.ENCRYPT, _iv);
//    }
//
//    /***
//     * This funtion decrypts the encrypted text to plain text using the key
//     * provided. You'll have to use the same key which you used during
//     * encryprtion
//     *
//     * @param _encryptedText
//     *            Encrypted/Cipher text to be decrypted
//     * @param _key
//     *            Encryption key which you used during encryption
//     * @param _iv
//     * 	    initialization Vector
//     * @return encrypted value
//     * @throws InvalidKeyException
//     * @throws UnsupportedEncodingException
//     * @throws InvalidAlgorithmParameterException
//     * @throws IllegalBlockSizeException
//     * @throws BadPaddingException
//     */
//    public String decrypt(
//            String _encryptedText,
//            String _key,
//            String _iv)
//            throws InvalidKeyException, UnsupportedEncodingException,
//            InvalidAlgorithmParameterException, IllegalBlockSizeException,
//            BadPaddingException {
//        return encryptDecrypt(_encryptedText, _key, EncryptMode.DECRYPT, _iv);
//    }

    /***
     * This function computes the SHA256 hash of input string.
     *
     * @param text
     *          input text whose SHA256 hash has to be computed
     * @param length
     *          length of the text to be returned
     * @return  Returns SHA256 hash of input text.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String SHA256 (
            String text,
            int length)
            throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        String resultStr;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes("UTF-8"));
        byte[] digest = md.digest();
        StringBuffer result = new StringBuffer();
        // convert to hex
        for (byte b : digest)
            result.append(String.format("%02x", b));
        if(length > result.toString().length())
            resultStr = result.toString();
        else
            resultStr = result.toString().substring(0, length);
        return resultStr;

    }

    /**
     * This function generates random string for given length.
     *
     * @param length
     *          Desired length
     * @return  Returns random generated IV.
     */
    public static String generateRandomIV(
            int length)
    {
        SecureRandom ranGen = new SecureRandom();
        byte[] aesKey = new byte[16];
        ranGen.nextBytes(aesKey);
        StringBuffer result = new StringBuffer();
        // convert to hex
        for (byte b : aesKey)
            result.append(String.format("%02x", b));
        if(length> result.toString().length())
            return result.toString();
        else
            return result.toString().substring(0, length);
    }
}

