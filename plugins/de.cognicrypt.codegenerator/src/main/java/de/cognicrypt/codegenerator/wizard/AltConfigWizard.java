package de.cognicrypt.codegenerator.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Optional;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.clafer.ast.AstConcreteClafer;
import org.clafer.instance.InstanceClafer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.DeveloperProject;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.generator.CodeGenerator;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.codegenerator.wizard.advanced.AdvancedUserValueSelectionPage;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerModeQuestionnaire;
import de.cognicrypt.codegenerator.wizard.beginner.BeginnerTaskQuestionPage;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.GUIElements;
import de.cognicrypt.utils.Utils;

public class AltConfigWizard extends Wizard {

	private TaskSelectionPage taskListPage;
	private WizardPage preferenceSelectionPage;
	private LocatorPage locatorPage;
	private ClaferModel claferModel;
	private HashMap<Question, Answer> constraints;
	private BeginnerModeQuestionnaire beginnerQuestions;
	private int prevPageId;
	private List<Integer> protocolList;

	public AltConfigWizard() {
		super();
		// Set the Look and Feel of the application to the operating
		// system's look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			Activator.getDefault().logError(e);
		}
		setWindowTitle("Cryptography Task Configurator");
		final ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("de.cognicrypt.codegenerator", "icons/cognicrypt-medium.png");
		setDefaultPageImageDescriptor(image);
	}

	public void addPages() {
		this.taskListPage = new TaskSelectionPage();
		setForcePreviousAndNextButtons(true);
		addPage(this.taskListPage);
	}

	@Override
	public boolean canFinish() {
		final IWizardPage page = getContainer().getCurrentPage();
		return page instanceof LocatorPage && page.isPageComplete();

	}

	private boolean checkifInUpdateRound() {
		boolean updateRound = false;
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (final StackTraceElement el : stack) {
			if (el.getMethodName().contains("updateButtons")) {
				updateRound = true;
				break;
			}
		}
		return updateRound;
	}

	/**
	 * Creates a new {@link BeginnerTaskQuestionPage}.
	 * 
	 * @param curPage
	 *        Current page
	 * @param beginnerQuestionnaire
	 *        updated this variable from a list of questions to have access to the method to get specific Questions.
	 */
	private void createBeginnerPage(final Page curPage, final BeginnerModeQuestionnaire beginnerQuestionnaire) {
		List<String> selection = null;
		if (curPage.getContent().size() == 1) {
			final Question curQuestion = curPage.getContent().get(0);
			if (curQuestion.getElement().equals(GUIElements.itemselection)) {
				selection = new ArrayList<>();
				for (final AstConcreteClafer childClafer : this.claferModel.getModel().getRoot().getSuperClafer().getChildren()) {
					if (childClafer.getSuperClafer().getName().endsWith(curQuestion.getSelectionClafer())) {
						selection.add(ClaferModelUtils.removeScopePrefix(childClafer.getName()));
					}
				}
			}
		}
		// Pass the questionnaire instead of the all of the questions.
		this.preferenceSelectionPage = new BeginnerTaskQuestionPage(curPage, this.beginnerQuestions.getTask(), beginnerQuestionnaire, selection);
	}

	/**
	 * This method returns the next page. If current page is task list or any but the last question page, the first/next question page is returned. If the current page is the last
	 * question page, the instance list page is returned.
	 *
	 * @param currentPage
	 *        current page
	 * @return either next question page or instance list page
	 */
	@Override
	public IWizardPage getNextPage(final IWizardPage currentPage) {

		// if page was already created, return the existing object
		if (currentPage instanceof BeginnerTaskQuestionPage) {
			addPage(currentPage);
			final BeginnerTaskQuestionPage beginnerTaskQuestionPage = (BeginnerTaskQuestionPage) currentPage;

			// remove set constraints if the user press the previous button
			if (prevPageId != beginnerTaskQuestionPage.getCurrentPageID()) {
				protocolList.add(beginnerTaskQuestionPage.getCurrentPageID());
				if (protocolList.size() > 2) {
					if (protocolList.get(protocolList.size() - 3) == beginnerTaskQuestionPage.getCurrentPageID()) {
						if (this.constraints != null) {
							BeginnerTaskQuestionPage previousPage = (BeginnerTaskQuestionPage) getPages()[prevPageId];
							Set<Question> previousPageQuestions = previousPage.getMap().keySet();
							for (Question q : previousPageQuestions) {
								this.constraints.remove(q);
							}
							protocolList.remove(protocolList.size() - 1);	//remove last page index
							protocolList.remove(protocolList.size() - 1);	//remove duplicate
						}
					}
				}
				prevPageId = beginnerTaskQuestionPage.getCurrentPageID();
			}

			if (this.beginnerQuestions.hasMorePages()) {
				int nextPageid =  beginnerTaskQuestionPage.getPageNextID();
				Optional<IWizardPage> nextPage = Arrays.asList(getPages()).stream().filter(e -> e instanceof BeginnerTaskQuestionPage && ((BeginnerTaskQuestionPage)e).getCurrentPageID() == nextPageid && ((BeginnerTaskQuestionPage)e).isActive()).findFirst();

				if (nextPage.isPresent()) {
					return nextPage.get();
				}
			
			}
		}
		if (currentPage instanceof TaskSelectionPage) {
			prevPageId = 0;
			this.protocolList = new ArrayList<>();
			protocolList.add(0);
			
			Arrays.asList(getPages()).parallelStream().forEach(e -> { 
			if (e instanceof BeginnerTaskQuestionPage) {
				((BeginnerTaskQuestionPage) e).setPageInactive();
			}
			});
		}

		// if page is shown for the first time, create the new object
		final Task selectedTask = this.taskListPage.getSelectedTask();
		if (currentPage == this.taskListPage && this.taskListPage.isPageComplete()) {
			this.claferModel = new ClaferModel(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile()));

			this.beginnerQuestions = new BeginnerModeQuestionnaire(selectedTask, selectedTask.getQuestionsJSONFile());
			// It is possible that now questions are within a BeginnerModeQuestionnaire
			if (this.beginnerQuestions.getPages().size() > 0) {
				this.preferenceSelectionPage = new BeginnerTaskQuestionPage(this.beginnerQuestions.nextPage(), this.beginnerQuestions.getTask(), null);
				if (this.constraints != null) {
					this.constraints = null;
				}
				if (this.preferenceSelectionPage != null) {
					addPage(this.preferenceSelectionPage);
				}
				return this.preferenceSelectionPage;
				// Return the Locator page if no questions exists.
				// TODO Remove code duplication. 
			} else {
				this.locatorPage = new LocatorPage("Locator");
				addPage(this.locatorPage);
				return this.locatorPage;
			}
		} else if (currentPage instanceof BeginnerTaskQuestionPage) {
			/**
			 * If current page is either question or properties page (in Advanced mode)
			 */
			if (this.constraints == null) {
				this.constraints = new HashMap<>();
			}

			final BeginnerTaskQuestionPage beginnerTaskQuestionPage = (BeginnerTaskQuestionPage) currentPage;
			final HashMap<Question, Answer> selectionMap = beginnerTaskQuestionPage.getMap();

			// Looping through all the entries that were added to the BeginnerTaskQuestionPage
			for (final Entry<Question, Answer> entry : selectionMap.entrySet()) {
				if (entry.getKey().getElement().equals(GUIElements.itemselection)) {
					handleItemSelection(entry);
				}
				this.constraints.put(entry.getKey(), entry.getValue());
			}

			if (this.beginnerQuestions.hasMorePages()) {

				final int nextID = beginnerTaskQuestionPage.getPageNextID();

				if (nextID > -1) {
					final Page curPage = this.beginnerQuestions.setPageByID(nextID);
					// Pass the variable for the questionnaire here instead of all the questions.
					createBeginnerPage(curPage, this.beginnerQuestions);
					if (checkifInUpdateRound()) {
						this.beginnerQuestions.previousPage();
					}

					final IWizardPage[] pages = getPages();
					for (int i = 1; i < pages.length; i++) {
						if (!(pages[i] instanceof BeginnerTaskQuestionPage)) {
							continue;
						}
						final BeginnerTaskQuestionPage oldPage = (BeginnerTaskQuestionPage) pages[i];
						if (oldPage.equals(this.preferenceSelectionPage)) {
							return oldPage;
						}
					}
					if (this.preferenceSelectionPage != null) {
						addPage(this.preferenceSelectionPage);
					}
					return this.preferenceSelectionPage;
				}
			}

			final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
				.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

			instanceGenerator.generateInstances(this.constraints);
			if (currentPage instanceof BeginnerTaskQuestionPage) {
				//default algorithm page will be added only for beginner mode
				if (instanceGenerator.getNoOfInstances() != 0) {
					this.locatorPage = new LocatorPage("Locator");
					addPage(this.locatorPage);
					return this.locatorPage;

				} else {
					if ("nextPressed".equalsIgnoreCase(Thread.currentThread().getStackTrace()[3].getMethodName())) {
						final String message = Constants.NO_POSSIBLE_COMBINATIONS_BEGINNER;
						MessageDialog.openError(new Shell(), "Error", message);
					}
				}
			}
		}
		//adding instance details page after default algorithm page in beginner mode
		else if (currentPage instanceof DefaultAlgorithmPage) {
			final InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
				.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

			instanceGenerator.generateInstances(this.constraints);

		} 
		return currentPage;
	}

	/**
	 * This method returns previous page. If currentPage is the first question, the task list page is returned. If it is any other question page or the instance list page, the
	 * previous question page is returned.
	 *
	 * @param currentPage
	 *        current page, either instance list page or question page
	 * @return either previous question or task selection page
	 */
	@Override
	public IWizardPage getPreviousPage(final IWizardPage currentPage) {
		final boolean lastPage = currentPage instanceof InstanceListPage || currentPage instanceof DefaultAlgorithmPage;
		if (!checkifInUpdateRound() && (currentPage instanceof AdvancedUserValueSelectionPage || currentPage instanceof BeginnerTaskQuestionPage || lastPage)) {
			if (!this.beginnerQuestions.isFirstPage()) {
				this.beginnerQuestions.previousPage();
			}

		}

		return super.getPreviousPage(currentPage);
	}

	private void handleItemSelection(final Entry<Question, Answer> entry) {
		final Answer ans = entry.getValue();
		ArrayList<ClaferDependency> claferDependencies = ans.getClaferDependencies();
		if (null == claferDependencies) {
			claferDependencies = new ArrayList<>();
		}

		String operand = "";
		for (final AstConcreteClafer childClafer : this.claferModel.getModel().getRoot().getSuperClafer().getChildren()) {
			if (childClafer.getSuperClafer().getName().endsWith("Task")) {
				for (final AstConcreteClafer grandChildClafer : childClafer.getChildren()) {
					if (grandChildClafer.getRef().getTargetType().getName().endsWith(entry.getKey().getSelectionClafer())) {
						operand = ClaferModelUtils.removeScopePrefix(grandChildClafer.getName());
						break;
					}
				}
			}
		}
		final ClaferDependency cd = new ClaferDependency();
		cd.setAlgorithm(this.taskListPage.getSelectedTask().getName());
		cd.setOperand(operand);
		cd.setOperator("++");
		cd.setValue(ans.getValue());
		claferDependencies.add(cd);
		ans.setClaferDependencies(claferDependencies);
	}

	/**
	 * This method is called once the user selects an instance. It writes the instance to an xml file and calls the code generation.
	 *
	 * @return <code>true</code>/<code>false</code> if writing instance file and code generation are (un)successful
	 */
	@Override
	public boolean performFinish() {
		boolean ret = true;
		final Task selectedTask = this.taskListPage.getSelectedTask();
		this.constraints = (this.constraints != null) ? this.constraints : new HashMap<>();
		InstanceGenerator instanceGenerator = new InstanceGenerator(CodeGenUtils.getResourceFromWithin(selectedTask.getModelFile())
			.getAbsolutePath(), "c0_" + selectedTask.getName(), selectedTask.getDescription());

		instanceGenerator.generateInstances(this.constraints);
		Map<String, InstanceClafer> instances = instanceGenerator.getInstances();
		InstanceClafer instance = instances.values().iterator().next();
		final LocatorPage currentPage = (LocatorPage) getContainer().getCurrentPage();

		// Initialize Code Generation
		final CodeGenerator codeGenerator = new XSLBasedGenerator(Utils.getIProjectFromISelection(currentPage.getSelectedResource()), selectedTask.getXslFile());
		final DeveloperProject developerProject = codeGenerator.getDeveloperProject();

		// Generate code template
		ret &= codeGenerator.generateCodeTemplates(
			new Configuration(instance, this.constraints, developerProject.getProjectPath() + Constants.innerFileSeparator + Constants.pathToClaferInstanceFile),
			selectedTask.getAdditionalResources());
		return ret;
	}

	public HashMap<Question, Answer> getConstraints() {
		return this.constraints;
	}

}
