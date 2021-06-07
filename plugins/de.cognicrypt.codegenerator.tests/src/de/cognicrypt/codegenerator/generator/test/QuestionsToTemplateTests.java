package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.AltConfigWizard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The JUnit Plug-in tests check the correctness of CogniCrypt's code generation
 * feature in determining the template path when the user chooses an arbitrary
 * task and a set of given question-answers in the wizard dialog. This is
 * important since the code generation depends on the computed path to generate
 * the proper template for a given input from the user through CogniCrypt's
 * wizard.
 * 
 * @author Shahrzad Asghari
 * @author Enri Ozuni
 */
public class QuestionsToTemplateTests {

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Digital);
		answers.add(Constants.Byte_Array);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Digital);
		answers.add(Constants.File);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Digital);
		answers.add(Constants.String);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedDigitalChannelAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Digital);
		answers.add(Constants.Others);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Hard);
		answers.add(Constants.Byte_Array);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Hard);
		answers.add(Constants.File);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Hard);
		answers.add(Constants.String);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithEncryptedHardDriveAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Encrypted_Hard);
		answers.add(Constants.Others);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Digital);
		answers.add(Constants.Byte_Array);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionhybrid";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Digital);
		answers.add(Constants.File);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionhybridfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Digital);
		answers.add(Constants.String);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionhybridstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedDigitalChannelAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Digital);
		answers.add(Constants.Others);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionhybrid";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Analog);
		answers.add(Constants.Byte_Array);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Analog);
		answers.add(Constants.File);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Analog);
		answers.add(Constants.String);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithUnencryptedAnalogChannelAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.Unencrypted_Analog);
		answers.add(Constants.Others);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndByteArray() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.No_Sharing);
		answers.add(Constants.Byte_Array);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndFile() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.No_Sharing);
		answers.add(Constants.File);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionfiles";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndString() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.No_Sharing);
		answers.add(Constants.String);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryptionstrings";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testEncryptionTaskWithNoSharingAndOther() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add(Constants.No_Sharing);
		answers.add(Constants.Others);
		wizard = templateConstructorWithAnswers(wizard, "Encryption", answers);
		String expected = Constants.PathToTemplates + "encryption";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	@Test
	public void testSecurePasswordTask() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task securepassword = TestUtils.getTask("SecurePassword");
		wizard.setSelectedTask(securepassword);
		String expected = Constants.PathToTemplates + "securepassword";
		assertEquals(expected, wizard.constructTemplateName());
	}

	@Test
	public void testDigitalSignaturesTask() {
		AltConfigWizard wizard = new AltConfigWizard();
		Task digitalsignatures = TestUtils.getTask("DigitalSignatures");
		wizard.setSelectedTask(digitalsignatures);
		String expected = Constants.PathToTemplates + "digitalsignatures";
		assertEquals(expected, wizard.constructTemplateName());
	}
	
	@Test
	public void testAuthorityManagerTaskWithPassGen() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add("A secure random password generator");
		wizard = templateConstructorWithAnswers(wizard, "UserAuthorityManager", answers);
		String expected = Constants.PathToTemplates + "userauthoritymanagerpassgen";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}
	@Test
	public void testAuthorityManagerTaskWithUserAuth() {
		AltConfigWizard wizard = new AltConfigWizard();
		ArrayList<String> answers = new ArrayList<String>();
		answers.add("User Authentication service that checks user's username and password from SQL database");
		wizard = templateConstructorWithAnswers(wizard, "UserAuthorityManager", answers);
		String expected = Constants.PathToTemplates + "userauthoritymanagerauth";
		String actual = wizard.constructTemplateName();
		assertEquals(expected, actual);
	}

	/**
	 * Constructs templates for tasks that have question/answer page.
	 *
	 * @param wizard the wizard.
	 * @param taskName the task name.
	 * @param answers array of the answer(s) in wizard page.
	 * @return the wizard.
	 */
	private AltConfigWizard templateConstructorWithAnswers(AltConfigWizard wizard, String taskName, ArrayList<String> answers) {
		Task task = TestUtils.getTask(taskName);
		wizard.setSelectedTask(task);
		HashMap<Question, Answer> constraints = TestUtils.setConstraintsForTask(task, answers);
		wizard.addConstraints(constraints);
		return wizard;
	}
}