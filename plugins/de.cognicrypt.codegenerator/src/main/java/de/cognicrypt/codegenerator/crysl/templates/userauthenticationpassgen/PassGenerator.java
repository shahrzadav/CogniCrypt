package de.cognicrypt.codegenerator.crysl.templates.userauthenticationpassgen;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class PassGenerator {
	
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

	public static org.passay.CharacterRule generateRule(org.passay.CharacterData characters, int amount) {
		org.passay.CharacterRule charRule = new org.passay.CharacterRule(characters);
		charRule.setNumberOfCharacters(amount);
		return charRule;
	}
}