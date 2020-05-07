/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionhybridstrings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {

	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey sessionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.KeyGenerator").addReturnObject(sessionKey).generate();
		return sessionKey;
	}

	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addReturnObject(keyPair);
		return keyPair;
	}

	public byte[] encryptSessionKey(javax.crypto.SecretKey sessionKey, java.security.KeyPair keyPair) throws GeneralSecurityException {
		byte[] wrappedKeyBytes = null;
		int mode = Cipher.WRAP_MODE;
		java.security.PublicKey publicKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(publicKey, "key").addParameter(sessionKey, "wrappedKey")
			.addReturnObject(wrappedKeyBytes).generate();
		return wrappedKeyBytes;
	}
	/**
	 * Encrypt data.
	 *
	 * @param plaintext to be encrypted
	 * @param key of type Secretkey
	 * @return the java.lang. string
	 * @throws GeneralSecurityException the general security exception
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * 		   see {@link javax.crypto.Cipher#getBytes("UTF-8") getInstance(Padding)}
	 * @throws IllegalBlockSizeException This exception is thrown when the input data (data that is being decoded or encoded) size is not a multiple of the block-size.
	 * @throws IOException This exception is thrown when the charset name is wrong.
	 * 		   see {@link java.lang.String#getBytes(String) charsetName}
	 * @throws NoSuchAlgorithmException This exception is thrown when cipher object is created using padding or modes that are not supported by chosen algorithm.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance()}
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException the invalid key exception
	 * @throws UnsupportedEncodingException 
	 */
	public java.lang.String encryptData(java.lang.String plaintext, javax.crypto.SecretKey key) throws IOException {
		byte[] ivBytes = new byte[16];
		byte[] cipherText = null;
		byte[] plaintextString = plaintext.getBytes("UTF-8");
		int mode = Cipher.ENCRYPT_MODE;
		System.out.println("ey daaaaaad");
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintextString, "plainText").addParameter(key, "key")
			.addReturnObject(cipherText).generate();

		byte[] ret = new byte[ivBytes.length + cipherText.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(cipherText, 0, ret, ivBytes.length, cipherText.length);
		return Base64.getEncoder().encodeToString(ret);

	}
	
	/**
	 * Decrypt data.
	 *
	 * @param ciphertext the encrypted string
	 * @param key the key
	 * @return the java.lang. string
	 * 
	 * @throws GeneralSecurityException This exception is thrown when the algorithm requested in Cipher.getInstance() is not available in the underlying library.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance(CipherAlgorithm)}
	 * 		   this can also be thrown of invalid ParameterSpec 
	 * 		   see {@link javax.crypto.spec.IvParameterSpec#IvParameterSpec(byte[]) IvParameterSpec()}!!!!!!!!
	 * 
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance(Padding)}
	 * @throws IllegalBlockSizeException This exception is thrown when the input data (data that is being decoded or encoded) size is not a multiple of the block-size.
	 * 
	 * @throws IOException This exception is thrown when the charset name is wrong.
	 * 		   see {@link java.lang.String.String#String(byte[], java.lang.String) charsetName}
	 * @throws NoSuchAlgorithmException This exception is thrown when cipher object is created using padding or modes that are not supported by chosen algorithm.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance()}
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * 
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * 		   see {@link javax.crypto.Cipher#init(int, javax.crypto.SecretKey, javax.crypto.spec.IvParameterSpec); init()}
	 * @throws InvalidAlgorithmParameterException This exception is thrown when cipher block size is not equal to ivBytes array.
	 *		   see {@link javax.crypto.Cipher#init(int, javax.crypto.SecretKey, javax.crypto.spec.IvParameterSpec); init()}
	 */
	public java.lang.String decryptData(java.lang.String ciphertext, javax.crypto.SecretKey key) throws IOException {
		byte[] ciphertextString = Base64.getDecoder().decode(ciphertext);
		byte[] ivBytes = new byte[16];
		byte[] data = new byte[ciphertextString.length - ivBytes.length];
		System.arraycopy(ciphertextString, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertextString, ivBytes.length, data, 0, data.length);
		//check check
//		dfgdfgdf
//		bfbf
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
			.addParameter(mode, "encmode").addParameter(data, "plainText").addParameter(key, "key").addReturnObject(res).generate();
		return new String(res, "UTF-8");
	}

}
