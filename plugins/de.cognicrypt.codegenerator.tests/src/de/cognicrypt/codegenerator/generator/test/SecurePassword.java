package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.CrySLBasedCodeGenerator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.testutilities.TestUtils;
import de.cognicrypt.codegenerator.wizard.Configuration;
import de.cognicrypt.utils.DeveloperProject;

public class SecurePassword {
	
	Logger log = Logger.getLogger(SecurePassword.class.getName());
	IJavaProject testJavaProject;
	CodeGenerator generatorSecPassword;
	Task secPasswordTask;
	Configuration configSecPassword;
	DeveloperProject developerProject;
	IResource targetFile;
	
	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		this.testJavaProject = TestUtils.createJavaProject("TestProject");
		targetFile = TestUtils.generateJavaClassInJavaProject(this.testJavaProject, "testPackage", "Test");
		this.secPasswordTask = TestUtils.getTask("SecurePassword");
		this.generatorSecPassword = new CrySLBasedCodeGenerator(targetFile);
		this.developerProject = this.generatorSecPassword.getDeveloperProject();
	}
	@Test
	public void testCodeGenerationSecurePassword() throws CoreException, IOException {
		final ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(this.developerProject, "testPackage", "Test.java");
		TestUtils.openJavaFileInWorkspace(this.developerProject, "testPackage", testClassUnit);

		this.configSecPassword = TestUtils.createCrySLConfiguration("securepassword", testClassUnit.getResource(), generatorSecPassword, this.developerProject);
		final boolean encCheck = this.generatorSecPassword.generateCodeTemplates(this.configSecPassword, this.secPasswordTask.getAdditionalResources());
		assertTrue(encCheck);
		
	}
}
