import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricCryptography {
	
	public String keygen;
	public String encypher;
	private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	private static final String NUMBER = "0123456789";
	private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";
	private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
	// optional, make it more random
	private static final String PASSWORD_ALLOW_BASE_SHUFFLE = shuffleString(PASSWORD_ALLOW_BASE);
	private static final String PASSWORD_ALLOW = PASSWORD_ALLOW_BASE_SHUFFLE;

	private static SecureRandom random = new SecureRandom();
	private static int keybit = 256;
	

	public String getEncypher() {
		return encypher;
	}

	public void setEncypher(String encypher) {
		this.encypher = encypher;
	}

	public String getkey() {
		return this.keygen;
	}

	public void setkey(String key) {
		this.keygen = key;
	}

	public String generateRandomPassword(int length) {
		if (length < 1)
			throw new IllegalArgumentException();

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {

			int rndCharAt = random.nextInt(PASSWORD_ALLOW.length());
			char rndChar = PASSWORD_ALLOW.charAt(rndCharAt);

			sb.append(rndChar);
		}
		return sb.toString();
	}

	public static String shuffleString(String string) {
		List<String> letters = Arrays.asList(string.split(""));
		Collections.shuffle(letters);
		return letters.stream().collect(Collectors.joining());
	}

	public void secureKet(String plaintext, String secret) throws Exception {
		System.out.println(plaintext + "\n" + secret);
		String cypher = encrypt(plaintext, secret ,keybit);
		setEncypher(cypher);
	}

	public void randomKey(String plaintext) throws Exception {
		String secret = generateRandomPassword(8);
		setkey(secret);
		String cypher = encrypt(plaintext, secret ,keybit);
		setEncypher(cypher);
		System.out.println(cypher);
	}

	public String defile(String secret, String cypher) throws Exception {
		System.out.println(cypher + "\n" + secret);
		String text = decrypt(cypher, secret , keybit);
		return text;
	}

	public void writeToFileText(String filename, String text) throws Exception {
		FileOutputStream outputStream = new FileOutputStream(filename);
		byte[] strToBytes = text.getBytes();
		outputStream.write(strToBytes);
		outputStream.flush();
		outputStream.close();
	}

	public String readfile(String filename) throws IOException {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		System.out.println(new File(filename).toPath());
		return Base64.getEncoder().encodeToString(keyBytes);
	}

	// Method to write the List of byte[] to a file
	public void writeToFile(byte[] list) throws FileNotFoundException, IOException {
		String filename = "MyData/cypherAES.txt";
		File f = new File(filename);
		f.getParentFile().mkdirs();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
		out.writeObject(list);
		out.close();
		System.out.println("Your file is ready.");
	}

	public static String encrypt(String strToEncrypt, String secret, int bit) {

		String salt = "ssshhhhhhhhhhh!!!!";
		System.out.println();
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, bit);
			SecretKey tmp = factory.generateSecret(spec);

			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret, int bit) throws Exception {
		String salt = "ssshhhhhhhhhhh!!!!";
		System.out.println(bit);
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, bit);
			SecretKey tmp = factory.generateSecret(spec);

			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt.getBytes("UTF-8"))));

		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

}
