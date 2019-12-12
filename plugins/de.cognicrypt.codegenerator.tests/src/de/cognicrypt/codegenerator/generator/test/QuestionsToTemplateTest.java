package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

import de.cognicrypt.codegenerator.actions.RunCodeGeneratorHandler;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.wizard.AltConfigWizard;
import de.cognicrypt.codegenerator.wizard.CogniCryptWizardDialog;
import de.cognicrypt.codegenerator.wizard.TaskSelectionPage;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerModeQuestionnaire;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerTaskQuestionPage;
import de.cognicrypt.core.Constants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QuestionsToTemplateTest {
	
	private Task selectedTask;
	private HashMap<Question, Answer> constraints;
//	private Question 
	private BeginnerModeQuestionnaire beginnerQuestions;
	private IWizard	wiz;
	private IWizardPage CurrentPage;

	@Test
	public void pages () throws IOException, ParseException {
		
		
		AltConfigWizard nn = new AltConfigWizard() ;
        Task selectedTask = new Task();
        selectedTask.setName("Encryption");
        selectedTask.setDescription("Encrypting data");
        selectedTask.setTaskDescription("When this use case is selected, CogniCrypt generates code for encrypting data. You can select which kind of plaintext (String, File, Byte[]), you wish to encrypt, as well as how to communicate the secret necessary for decryption.");
        selectedTask.setImage("Lock");
        selectedTask.setCodeGen(Constants.CodeGenerators.CrySL);
        selectedTask.setCodeTemplate("src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption");
        
        
        BeginnerModeQuestionnaire begmq = new BeginnerModeQuestionnaire(selectedTask, "../de.cognicrypt.codegenerator/src/main/resources/TaskDesc/Encryption.json");

        Answer answ = new Answer();
        answ.setValue("Byte Array");
        answ.setOption("");
        
        Answer answ2 = new Answer();
        answ2.setValue("Encrypted digital channel");
        answ2.setOption("");
        
        Question quest = new Question();
        quest.setId(1);
        quest.setQuestionText("What data type do you wish to encrypt?");
        quest.setEnteredAnswer(answ);
        ArrayList<Answer> answarray = new ArrayList<Answer>(); 
        answarray.add(answ);
        quest.setAnswers(answarray);
        
        Question quest2 = new Question();
        quest2.setId(0);
        quest2.setQuestionText("Which method of communication would you prefer to use for key exchange?");
        quest2.setEnteredAnswer(answ2);
        ArrayList<Answer> answ2array = new ArrayList<Answer>(); 
        answ2array.add(answ2);
        quest2.setAnswers(answ2array);
        
        
//        CurrentPage = new BeginnerTaskQuestionPage(begmq, quest, selectedTask);
//		final BeginnerTaskQuestionPage curQuestionPage = (BeginnerTaskQuestionPage) CurrentPage;
//		curQuestionPage.setSelectionMap(quest,answ);
//		curQuestionPage.setSelectionMap(quest2,answ2);
//		curQuestionPage.setTestMode();
//		curQuestionPage.setPageNextID(-1);
		
		nn.setSelectedTask(selectedTask);
//        nn.getNextPage(curQuestionPage);
        
        assertEquals(nn.constructTemplateName(), "src/main/java/de/cognicrypt/codegenerator/crysl/templates/encryption");
        
 
	}
}
