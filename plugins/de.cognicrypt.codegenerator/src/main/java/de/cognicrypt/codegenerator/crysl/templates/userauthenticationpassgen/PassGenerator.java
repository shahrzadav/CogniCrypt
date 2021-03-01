/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.userauthenticationpassgen;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class PassGenerator, generates a secure password.
 */
public class PassGenerator {
	
	/**
	 * Generates a password using Passay library with 8 characters. It involves
	 * 2 of each set of characters, capital case letters, lower case letters,
	 * digits and special characters, which are distributed randomly in the password.
	 *
	 * @return the password.
	 */
	public static String generatePassayPassword() {
		int length = 8;
	   
	  org.passay.CharacterData lowerCaseChars = org.passay.EnglishCharacterData.LowerCase;  
	  org.passay.CharacterData upperCaseChars = org.passay.EnglishCharacterData.UpperCase;	   
	  org.passay.CharacterData digitChars = org.passay.EnglishCharacterData.Digit;
	  org.passay.CharacterData specialChars = org.passay.EnglishCharacterData.Special;
	  
	  
	  org.passay.CharacterRule[] rules = {generateRule(lowerCaseChars, 2),generateRule(upperCaseChars, 2),
			  generateRule(specialChars, 2), generateRule(digitChars, 2)};
	  String res = null;
	  
	  CrySLCodeGenerator.getInstance().includeClass("org.passay.PasswordGenerator").addParameter(length, "len").addParameter(rules, "rules").addParameter(res, "res").generate();

	  return res;
	}

	/**
	 * Defines a set of character and a number of that character in the password.
	 *
	 * @param characters the characters, digits, letters, etc.
	 * @param amount the amount of those characters in the password.
	 * @return the character rule.
	 */
	public static org.passay.CharacterRule generateRule(org.passay.CharacterData characters, int amount) {
		org.passay.CharacterRule charRule = new org.passay.CharacterRule(characters);
		charRule.setNumberOfCharacters(amount);
		return charRule;
	}
}