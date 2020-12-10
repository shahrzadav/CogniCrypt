/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.cognicrypt.codegenerator.crysl.templates.userauthenticationpassgen;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class PasswordGenerator {

	// password will atleast be 4 letters
	public static String generateRandomPassword(int length) throws NoSuchAlgorithmException, GeneralSecurityException
	{
		final String capitalAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String smallAlphabet = "abcdefghijklmnopqrstuvwxyz";
		final String numbers = "0123456789";
		//removed dot slashes and quotations
		final String specialChars = "~!@#$%^&*()-_=+[{]};:,<>?";
		final String allCases = capitalAlphabet + smallAlphabet + numbers + specialChars;
		
		int capAlphLen = capitalAlphabet.length();
		int smlAlphLen = smallAlphabet.length();
		int numLen = numbers.length();
		int specCharLen = specialChars.length();
		int allCaseLen = allCases.length();
		
		StringBuilder password = new StringBuilder();
		
		password.append(capitalAlphabet.charAt(generateRandomNumber(capAlphLen)));
		password.append(smallAlphabet.charAt(generateRandomNumber(smlAlphLen)));
		password.append(numbers.charAt(generateRandomNumber(numLen)));
		password.append(specialChars.charAt(generateRandomNumber(specCharLen)));

		for (int i = 4; i < length; i++) {
			password.append(allCases.charAt(generateRandomNumber(allCaseLen)));
		}

		return password.toString();
	}
	
	public static int generateRandomNumber(int length) throws NoSuchAlgorithmException, GeneralSecurityException{
		
		int randIndex = 0;
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(length, "len").addParameter(randIndex, "randInt").generate();
		return randIndex;
	}

}
