package com.webdav.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DataEncryption {
	
	private static final DataEncryption INSTANCE = new DataEncryption();
	
	private static final int AES_KEY_SIZE = 256;
	private static final int RSA_KEY_SIZE = 4096;
	
	private Cipher pkCipher, aesCipher;
	private byte[] aesKey;
	private SecretKeySpec aesKeySpec;
	private String PUBLIC_RSA = FileSystemUtil.getSystemRoot() + "security" + File.separator + "public.key";
	private String PRIVATE_RSA = FileSystemUtil.getSystemRoot() + "security" + File.separator + "private.key";
	private String AES_KEY = FileSystemUtil.getSystemRoot() + "security" + File.separator + "aes.key";
	
	private DataEncryption() {
		try {
			pkCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
			aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			Path path = Paths.get(FileSystemUtil.getSystemRoot() + "security");
			
			if (!Files.exists(path)) {
				FileSystemUtil.createDirectory(path);
				KeyPair rsaKeys = generateRSAKeyPair();
				saveRSAKeyPair(rsaKeys);
				generateAESKey();
				saveAESKey();
			}
			
			loadAESKey();
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static DataEncryption getInstance() {
		return INSTANCE;
	}
	
	public byte[] encrypt(byte[] unencryptedContent) {
		try {
			aesCipher.init(Cipher.ENCRYPT_MODE, aesKeySpec);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] ivAndEncData = null;
		
		try {
			byte[] encryptedData = aesCipher.doFinal(unencryptedContent);
			byte[] iv = aesCipher.getIV();
			ivAndEncData = new byte[iv.length + encryptedData.length];
			System.arraycopy(iv, 0, ivAndEncData, 0, iv.length);
			System.arraycopy(encryptedData, 0, ivAndEncData, iv.length, encryptedData.length);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ivAndEncData;
	}
	
	public byte[] decrypt(byte[] encryptedContent) {
		byte[] iv = new byte[aesCipher.getBlockSize()];
		System.arraycopy(encryptedContent, 0, iv, 0, iv.length);
		
		try {
			aesCipher.init(Cipher.DECRYPT_MODE, aesKeySpec, new IvParameterSpec(iv));
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] decryptedData = null;
		
		try {
			decryptedData = aesCipher.doFinal(encryptedContent, iv.length, encryptedContent.length - iv.length);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return decryptedData;
	}
	
	private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = new SecureRandom();
		keyGen.initialize(RSA_KEY_SIZE, random);
		return keyGen.genKeyPair();
	}
	
	private void saveRSAKeyPair(KeyPair rsaKeys) throws IOException {
		byte[] privateEncoded = rsaKeys.getPrivate().getEncoded();
		Path privatePath = Paths.get(PRIVATE_RSA);
		Files.write(privatePath, privateEncoded);
		
		byte[] publicEncoded = rsaKeys.getPublic().getEncoded();
		Path publicPath = Paths.get(PUBLIC_RSA);
		Files.write(publicPath, publicEncoded);
	}
	
	private void generateAESKey() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(AES_KEY_SIZE);
		SecretKey key = kgen.generateKey();
		aesKey = key.getEncoded();
		
		SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
		aesKeySpec = new SecretKeySpec(bytes, "AES");
	}
	
	private void saveAESKey() throws IOException, GeneralSecurityException {
		byte[] encodedKey = read(PUBLIC_RSA);
		
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pk = kf.generatePublic(publicKeySpec);
	   
		pkCipher.init(Cipher.ENCRYPT_MODE, pk);
		CipherOutputStream os = new CipherOutputStream(new FileOutputStream(AES_KEY), pkCipher);
		os.write(aesKey);
		os.close();
	}
	
	private void loadAESKey() throws GeneralSecurityException, IOException {
		byte[] encodedKey = read(PRIVATE_RSA);
		
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey pk = kf.generatePrivate(privateKeySpec);
	   
		pkCipher.init(Cipher.DECRYPT_MODE, pk);
		aesKey = new byte[AES_KEY_SIZE / 8];
		CipherInputStream is = new CipherInputStream(new FileInputStream(AES_KEY), pkCipher);
		is.read(aesKey);
		is.close();
		
		aesKeySpec = new SecretKeySpec(aesKey, "AES");
	}
	
    private byte[] read(String fileName) {
    	Path path = Paths.get(fileName);
    	byte[] fileData = null;
    	
    	try {
			fileData = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return fileData;
    }
}