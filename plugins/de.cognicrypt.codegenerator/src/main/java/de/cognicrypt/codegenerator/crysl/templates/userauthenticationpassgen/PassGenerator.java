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

import org.passay.*;
public class PassGenerator {

	protected static final String ERROR_CODE = null;

	public static String generatePassayPassword() {
	    PasswordGenerator gen = new PasswordGenerator();
	    
	    CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
	    CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
	    lowerCaseRule.setNumberOfCharacters(2);

	    CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
	    CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
	    upperCaseRule.setNumberOfCharacters(2);
	    
	    CharacterData digitChars = EnglishCharacterData.Digit;
	    CharacterRule digitRule = new CharacterRule(digitChars);	    
	    digitRule.setNumberOfCharacters(2);

	    CharacterData specialChars = new CharacterData() {
	        public String getErrorCode() {
	            return ERROR_CODE;
	        }

	        public String getCharacters() {
	            return "!@#$%^&*()_+";
	        }
	    };
	    CharacterRule splCharRule = new CharacterRule(specialChars);
	    splCharRule.setNumberOfCharacters(2);

	    String password = gen.generatePassword(8, splCharRule, lowerCaseRule, 
	      upperCaseRule, digitRule);
	    return password;
	}

	public static void main (String[] args) {
		String securePass = generatePassayPassword();
		System.out.println("the result: " + securePass);
	}

}
