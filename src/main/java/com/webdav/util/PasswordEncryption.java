package com.webdav.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * 
 * @author M
 *
 */

public class PasswordEncryption {
	private static final int iterations = 20*1000;
	private static final int saltLength = 32;
	private static final int keyLength = 256;
	
	/**
	 * Computes a salted PBKDF2 hash of given plaintext password
	 * suitable for storing in a database.
	 * Empty passwords are not supported.
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String getSaltedHash(String password) {
		byte[] salt = null;
		try {
			salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
	}
	
	/**
	 * Checks whether given plaintext password corresponds
	 * to a stored salted hash of the password.
	 * 
	 * @param password
	 * @param stored
	 * @return
	 * @throws Exception
	 */
	public static boolean check(String password, String storedPassword) throws IllegalStateException {
		String[] saltAndPass = storedPassword.split("\\$");
		if (saltAndPass.length != 2) {
			throw new IllegalStateException("The stored password should have the form 'salt$hash'");
		}
		
		String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
		return hashOfInput.equals(saltAndPass[1]);
	}
	
	/**
	 * Uses PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt.
	 * See link: http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
	 * 
	 * @param password
	 * @param salt
	 * @return
	 * @throws IllegalArgumentException
	 */
	private static String hash(String password, byte[] salt) throws IllegalArgumentException {
		if (password == null || password.length() == 0) {
			throw new IllegalArgumentException("Empty passwords are not supported.");
		}
		
		SecretKeyFactory f = null;
		SecretKey key = null;
		
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			key = f.generateSecret(new PBEKeySpec(
					password.toCharArray(), salt, iterations, keyLength));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Base64.encodeBase64String(key.getEncoded());
	}
}