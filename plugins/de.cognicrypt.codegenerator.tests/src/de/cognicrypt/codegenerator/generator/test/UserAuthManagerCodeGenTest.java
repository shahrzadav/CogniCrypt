package de.cognicrypt.codegenerator.generator.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
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

public class UserAuthManagerCodeGenTest {
	private Logger log = Logger.getLogger(UserAuthManagerCodeGenTest.class.getName());
	private IJavaProject testJavaProject;
	private CodeGenerator generatorAuthManager;
	private Task authManagerTask;
	private Configuration configAuthManager;
	private DeveloperProject developerProject;
	private IResource targetFile;
	
	@After
	public void tearDown() throws CoreException {
		TestUtils.deleteProject(this.testJavaProject.getProject());
	}

	@Before
	public void setUp() throws Exception {
		this.testJavaProject = TestUtils.createJavaProject(Constants.PROJECT_NAME);
		targetFile = TestUtils.generateJavaClassInJavaProject(this.testJavaProject, Constants.PACKAGE_NAME,
				Constants.CLASS_NAME);
		this.authManagerTask = TestUtils.getTask("UserAuthorityManager");
		this.generatorAuthManager = new CrySLBasedCodeGenerator(targetFile);
		this.developerProject = this.generatorAuthManager.getDeveloperProject();
		
	}
	
	@Test
	public void testCodeGenerationUserAuthentication() throws CoreException, IOException {
		final ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(this.developerProject,
				Constants.PACKAGE_NAME, Constants.JAVA_CLASS_NAME);
		TestUtils.openJavaFileInWorkspace(this.developerProject, Constants.PACKAGE_NAME, testClassUnit);
		this.configAuthManager = TestUtils.createCrySLConfiguration("userauthoritymanagerauth", testClassUnit.getResource(),
				generatorAuthManager, this.developerProject);
		final boolean encCheck = this.generatorAuthManager.generateCodeTemplates(this.configAuthManager,
				this.authManagerTask.getAdditionalResources());
		
		assertTrue(encCheck);
	}
	@Test
	public void testCodeGenerationPassGenerator() throws CoreException, IOException {
		final ICompilationUnit testClassUnit = TestUtils.getICompilationUnit(this.developerProject,
				Constants.PACKAGE_NAME, Constants.JAVA_CLASS_NAME);
		TestUtils.openJavaFileInWorkspace(this.developerProject, Constants.PACKAGE_NAME, testClassUnit);

		this.configAuthManager = TestUtils.createCrySLConfiguration("userauthoritymanagerpassgen", testClassUnit.getResource(),
				generatorAuthManager, this.developerProject);
		final boolean encCheck = this.generatorAuthManager.generateCodeTemplates(this.configAuthManager,
				this.authManagerTask.getAdditionalResources());
		
		assertTrue(encCheck);
	}
}
