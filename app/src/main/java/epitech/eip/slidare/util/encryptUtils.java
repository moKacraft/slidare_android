/*
 * Projet Slidare
 * Sharing anywhere, anytime
 *
 */
package epitech.eip.slidare.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

//Android import
import android.util.Base64;


/**
 *
 * Created by Madeline on 02/06/2017 at 02:11.
 *
 */
public class encryptUtils {

    private final String ENCRYPTION_INSTANCE = "AES/CBC/PKCS5Padding";
    private final String SECRET_KEY_FACTORY_INSTANCE = "PBKDF2WithHmacSHA1";

    private Cipher _cx;

    // String encrypt
    private byte[] _key;
    private byte[] _iv;

    private String _stringKey;
    private String _stringIV;

    // File encrypt
    private byte[] _fileSalt;
    private byte[] _fileIV;

    private String _fileKey;
    private String _fileEncryptedName;
    private String _fileName;
    private String _fileSHA1;

    private long   _fileSize;

    /**
     * encryptUtils class constructor.
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public encryptUtils()
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            UnsupportedEncodingException {
        _cx = Cipher.getInstance(ENCRYPTION_INSTANCE);

        // String encrypt
        _key = new byte[32];// 256 bit key space
        _iv = new byte[16];// 128 bit IV

        // File encrypt
        //_fileKey = new byte[32];
        _fileIV = new byte[16];
        _fileSalt = new byte[8];
        _fileSize = 0;
    }

    /**
     * Function to encrypt a file.
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
        // Encryption key
        _fileKey = _encryptionKey;
        // File to be encrypted
        FileInputStream inFile = new FileInputStream(toEncryptFileName);
        // File to be encrypted size
        _fileSize = inFile.getChannel().size();
        // File to be encrypted filename
        _fileName = toEncryptFileName;
        // File to be encrypted SHA1
        _fileSHA1 = SHA1(toEncryptFileName);
        // Encrypted file
        FileOutputStream outFile = new FileOutputStream(encryptedFileName);
        // Encrypted file filename
        _fileEncryptedName = encryptedFileName;

        // password, iv and salt should be transferred to the other end
        // in a secure manner

        // salt is used for encoding, writing it to a file
        // salt should be transferred to the recipient securely
        // for decryption

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(_fileSalt);
        //FileOutputStream saltOutFile = new FileOutputStream("salt.enc");
        //saltOutFile.write(_fileSalt);
        //saltOutFile.close();

        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_INSTANCE);
        // byte[] pass, byte[] salt, int ITERATIONS, int KEY_LENGTH
        KeySpec keySpec = new PBEKeySpec(_encryptionKey.toCharArray(), _fileSalt, 65536, 256);
        SecretKey tmp = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        _cx.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = _cx.getParameters();

        // iv adds randomness to the text and just makes the mechanism more
        // secure, used while initializing the cipher file to store the iv

        //FileOutputStream ivOutFile = new FileOutputStream("iv.enc");
        _fileIV = params.getParameterSpec(IvParameterSpec.class).getIV();
        //ivOutFile.write(_fileIV);
        //ivOutFile.close();

        // File encryption
        byte[] input = new byte[64];
        int bytesRead;

        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = _cx.update(input, 0, bytesRead);
            if (output != null)
                outFile.write(output);
        }

        byte[] output = _cx.doFinal();
        if (output != null)
            outFile.write(output);
        inFile.close();
        outFile.flush();
        outFile.close();
    }

    /**
     * This function decrypt a file.
     *
     * @param toDecryptFileName
     *          File name of file to decrypt
     * @param decryptedFileName
     *          File name of file that will be decrypted
     * @param fileSHA1
     * @param fileSalt
     * @param fileIV
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
            String fileSHA1,
            byte[] fileSalt,
            byte[] fileIV,
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
//        FileInputStream saltFis = new FileInputStream("salt.enc");
//        byte[] salt = new byte[8];
//        saltFis.read(salt);
//        saltFis.close();

        // reading the iv
//        FileInputStream ivFis = new FileInputStream("iv.enc");
//        byte[] iv = new byte[16];
//        ivFis.read(iv);
//        ivFis.close();

        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_INSTANCE);
        // byte[] pass, byte[] salt, int ITERATIONS, int KEY_LENGTH
        //KeySpec keySpec = new PBEKeySpec(_encryptionKey.toCharArray(), salt, 65536, 256);
        KeySpec keySpec = new PBEKeySpec(_encryptionKey.toCharArray(), fileSalt, 65536, 256);
        SecretKey tmp = factory.generateSecret(keySpec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        //_cx.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        _cx.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(fileIV));

        // File decryption
        byte[] in = new byte[64];
        int read;

        while ((read = fis.read(in)) != -1) {
            byte[] output = _cx.update(in, 0, read);
            if (output != null)
                fos.write(output);
        }

        byte[] output = _cx.doFinal();
        if (output != null)
            fos.write(output);
        fis.close();
        fos.flush();
        fos.close();

        String shaDecrypted = SHA1(decryptedFileName);
        if (shaDecrypted.equals(fileSHA1)) {
            System.out.println("Sha is same\n" + fileSHA1 + "\n" + shaDecrypted);
            //not working on android
            // TO DO!!
            //Path path = FileSystems.getDefault().getPath(".", toDecryptFileName);
            //Files.deleteIfExists(path);
        }
        else
            System.out.println("Sha is diff\n" + fileSHA1 + "\n" + shaDecrypted);
    }

    /**
     *
     * @return  Encrypted File filename
     */
    public String get_fileEncryptedName(){
        return _fileEncryptedName;
    }

    /**
     *
     * @return  To encrypt File filename
     */
    public String get_fileName(){
        return _fileName;
    }

    /**
     *
     * @return  To encrypt File size
     */
    public long get_fileSize(){
        return _fileSize;
    }

    /**
     *
     * @return  To encrypt File SHA1
     */
    public String get_fileSHA1(){
        return _fileSHA1;
    }

    /**
     *
     * @return  Encrypted File salt
     */
    public byte[] get_fileSalt(){
        return _fileSalt;
    }

    /**
     *
     * @return  Encrypted File IV
     */
    public byte[] get_fileIV(){
        return _fileIV;
    }

    /**
     *
     * @return  Encrypted File key
     */
    public String get_fileKey(){
        return _fileKey;
    }

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

        _stringKey = _encryptionKey;
        int keyLen = _encryptionKey.getBytes("UTF-8").length;
        if (_encryptionKey.getBytes("UTF-8").length > _key.length)
            keyLen = _key.length;
        System.arraycopy(_encryptionKey.getBytes("UTF-8"), 0, _key, 0, keyLen);

        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES");

        _stringIV = _initVector;
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

    /**
     *
     * @return  Encrypted String key
     */
    public String get_stringKey(){
        return _stringKey;
    }

    /**
     *
     * @return  Encrypted String IV
     */
    public String get_stringIV(){
        return _stringIV;
    }

    /**
     * This function calculate the SHA1 string for the filename provided as parameter.
     *
     * @param filename
     *          Filename of the file to get SHA1 from.
     * @return  Return SHA1 from file.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String SHA1(
            String filename)
            throws NoSuchAlgorithmException,
            IOException {
        File file = new File(filename);
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

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
    public static String SHA256(
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
            int length) {
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

    /**
     * Generate random 32 bits string.
     *
     * @return  Return rand generated string.
     */
    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();

        return new BigInteger(160, random).toString(32);
    }
}

