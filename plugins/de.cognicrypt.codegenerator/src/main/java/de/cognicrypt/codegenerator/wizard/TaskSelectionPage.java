/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.core.Constants;

public class TaskSelectionPage extends WizardPage {

	public static final String KEY_IMAGE = "key.png";
	public static final String KEY_IMAGE_INVERTED = "key_invert.png";
	
	public static final String WIFI_IMAGE = "wifi.png";
	public static final String WIFI_IMAGE_INVERTED = "wifi_invert.png";
	
	private static final String LOCK_IMAGE = "lock.png";
	private static final String LOCK_IMAGE_INVERTED = "lock_invert.png";
	
	private static final String HAT_IMAGE = "hat.png";
	private static final String HAT_IMAGE_INVERTED = "hat_invert.png";
	
	private Composite container;
	private ComboViewer taskComboSelection;
	private IProject selectedProject = null;

	public TaskSelectionPage() {
		super(Constants.SELECT_TASK);
		setTitle(Constants.TASK_LIST);
		setDescription(Constants.DESCRIPTION_TASK_SELECTION_PAGE);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {	
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.container = new Composite(sc, SWT.NONE);
		this.container.setBounds(10, 10, 450, 200);

		//To display the Help view after clicking the help icon
		PlatformUI.getWorkbench().getHelpSystem().setHelp(sc, "de.cognicrypt.codegenerator.TaskSelectionHelp");
		
		GridLayout gl = new GridLayout(2,false);
		gl.verticalSpacing = -6;
		this.container.setLayout(gl);

		
		final GridData firstButton = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		
		Button encryptionButton = new Button(container, SWT.WRAP);
		Image encImage = loadImage(LOCK_IMAGE);
		Image encImageInvert = loadImage(LOCK_IMAGE_INVERTED);
		Rectangle encBounds = encImage.getBounds();
		encryptionButton.setSize(encBounds.width, encBounds.width);
		encryptionButton.setImage(encImage);
		encryptionButton.setLayoutData(firstButton);
		
		final Label useCaseDescriptionLabel = new Label(this.container, SWT.WRAP);
		final GridData gd_selectProjectLabel = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4);
		gd_selectProjectLabel.heightHint = 200;
		gd_selectProjectLabel.widthHint = 600;
		useCaseDescriptionLabel.setLayoutData(gd_selectProjectLabel);
		
		Button hashButton = new Button(container, SWT.WRAP);
		Image hashImage = loadImage(KEY_IMAGE);
		Image hashImageInvert = loadImage(KEY_IMAGE_INVERTED);
		Rectangle hashBounds = hashImage.getBounds();
		hashButton.setSize(hashBounds.width, hashBounds.width);
		hashButton.setImage(hashImage);
		
		Button secChanButton = new Button(container, SWT.WRAP);
		Image secChanImage = loadImage(WIFI_IMAGE);
		Image secChanImageInvert = loadImage(WIFI_IMAGE_INVERTED);
		Rectangle secChanBounds = secChanImage.getBounds();
		secChanButton.setSize(secChanBounds.width, secChanBounds.width);
		secChanButton.setImage(secChanImage);
		
		Button crcButton = new Button(container, SWT.WRAP);
		Image crcImage = loadImage(HAT_IMAGE);
		Image crcImageInvert = loadImage(HAT_IMAGE_INVERTED);
		Rectangle crcBounds = crcImage.getBounds();
		crcButton.setSize(crcBounds.width, crcBounds.width);
		crcButton.setImage(crcImage);
		
		final Button[] buttons = new Button[] {encryptionButton, hashButton, secChanButton, crcButton};
		final Image[] unclickedImages = new Image[] {encImage, hashImage, secChanImage, crcImage};
		final Image[] clickedImages = new Image[] {encImageInvert, hashImageInvert, secChanImageInvert, crcImageInvert};
		
		// Get Tasks 
		final List<Task> tasks = TaskJSONReader.getTasks();
		String[] taskdescs = new String[] { 
				// TODO we should organize that file correctly and don't do such dirty hacks
				tasks.get(0).getTaskDescription(),
				tasks.get(2).getTaskDescription(),
				tasks.get(3).getTaskDescription(),
				tasks.get(4).getTaskDescription()};
		
		for(Button button : buttons) {
			button.addListener(SWT.Selection, new SelectionButtonListener(
				buttons,
				unclickedImages,
				clickedImages,
				taskdescs,
				useCaseDescriptionLabel));
		}
		
//		final ComboViewer projectComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
//		final Combo projectCombo = projectComboSelection.getCombo();
//		final GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
//		gd_combo.widthHint = 356;
//		projectCombo.setLayoutData(gd_combo);
//		projectCombo.setToolTipText(Constants.PROJECTLIST_TOOLTIP);
//		projectCombo.setEnabled(true);
//		projectComboSelection.setContentProvider(ArrayContentProvider.getInstance());
//
//		final Map<String, IProject> javaProjects = new HashMap<>();
//		for (final IProject project : CodeGenUtils.complileListOfJavaProjectsInWorkspace()) {
//			javaProjects.put(project.getName(), project);
//		}
//
//		if (javaProjects.isEmpty()) {
//			final String[] errorMessage = { Constants.ERROR_MESSAGE_NO_PROJECT };
//			projectComboSelection.setInput(errorMessage);
//			projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
//		} else {
//			projectComboSelection.setInput(javaProjects.keySet().toArray());
//			projectComboSelection.setComparator(new ViewerComparator());
//			projectComboSelection.addSelectionChangedListener(event -> {
//				final IStructuredSelection selected = (IStructuredSelection) event.getSelection();
//				this.selectedProject = javaProjects.get(selected.getFirstElement());
//				projectComboSelection.refresh();
//
//			});
//
//			final IProject currentProject = CodeGenUtils.getCurrentProject();
//			if (currentProject == null) {
//				projectComboSelection.setSelection(new StructuredSelection(projectComboSelection.getElementAt(0)));
//			} else {
//				projectComboSelection.setSelection(new StructuredSelection(currentProject.getName()));
//			}
//		}
//
//		final Label selectTaskLabel = new Label(this.container, SWT.NONE);
//		final GridData gd_selectTaskLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
//		gd_selectTaskLabel.heightHint = 28;
//		gd_selectTaskLabel.widthHint = 139;
//		selectTaskLabel.setLayoutData(gd_selectTaskLabel);
//		selectTaskLabel.setText(Constants.SELECT_TASK);
//
//		this.taskComboSelection = new ComboViewer(this.container, SWT.DROP_DOWN | SWT.READ_ONLY);
//		Combo taskCombo = taskComboSelection.getCombo();
//		GridData gd_taskCombo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
//		gd_taskCombo.widthHint = 223;
//		taskCombo.setLayoutData(gd_taskCombo);
//		taskCombo.setToolTipText(Constants.TASKLIST_TOOLTIP);
//		taskCombo.setEnabled(true);
//		this.taskComboSelection.setContentProvider(ArrayContentProvider.getInstance());
//
//		
//
//		this.taskComboSelection.setLabelProvider(new LabelProvider() {
//
//			@Override
//			public String getText(final Object task) {
//				if (task instanceof Task) {
//					final Task current = (Task) task;
//					return current.getDescription();
//
//				}
//				return super.getText(task);
//			}
//		});
//
//		this.taskComboSelection.setInput(tasks);
//		this.taskComboSelection.setComparator(new ViewerComparator());
//		//Label for task description
//		final Label taskDescription = new Label(this.container, SWT.NONE);
//		final GridData gd_taskDescription = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
//		gd_taskDescription.widthHint = 139;
//		taskDescription.setLayoutData(gd_taskDescription);
//		taskDescription.setText(Constants.TASK_DESCRIPTION);
//
//		// Adding description text for the cryptographic task that has been selected from the combo box
//		final Text descriptionText = new Text(this.container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
//		final GridData gd_descriptionText = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
//		gd_descriptionText.widthHint = 297;
//		gd_descriptionText.heightHint = 96;
//		descriptionText.setLayoutData(gd_descriptionText);
//		descriptionText.setToolTipText("Description for the selected cryptographic task ");
//		descriptionText.setEditable(false);
//		descriptionText.setCursor(null);
//
//		//Hide scroll bar 
//		Listener scrollBarListener = new Listener() {
//
//			@Override
//			public void handleEvent(Event event) {
//				Text t = (Text) event.widget;
//				Rectangle r1 = t.getClientArea();
//				// use r1.x as wHint instead of SWT.DEFAULT
//				Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
//				Point p = t.computeSize(r1.x, SWT.DEFAULT, true);
//				t.getVerticalBar().setVisible(r2.height <= p.y);
//				if (event.type == SWT.Modify) {
//					t.getParent().layout(true);
//					t.showSelection();
//				}
//			}
//		};
//		descriptionText.addListener(SWT.Resize, scrollBarListener);
//		descriptionText.addListener(SWT.Modify, scrollBarListener);
//
//		this.taskComboSelection.addSelectionChangedListener(event -> {
//			final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
//			final Task selectedTask = (Task) selection.getFirstElement();
//			TaskSelectionPage.this.taskComboSelection.refresh();
//			setPageComplete(selectedTask != null && this.selectedProject != null);
//			// To display the description text
//			descriptionText.setText(selectedTask.getTaskDescription());
//		});
//
//		this.taskComboSelection.setSelection(new StructuredSelection(tasks.get(0)));
		setControl(this.container);
		new Label(this.container, SWT.NONE);
		new Label(this.container, SWT.NONE);

		sc.setContent(container);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		setControl(sc);

//		//Primitive Integration
//		Button btnPrimitiveIntegration = new Button(container, SWT.NONE);
//		btnPrimitiveIntegration.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				WizardDialog wizardDialog = new WizardDialog(parent.getShell(), new PrimitiveIntegrationWizard());
//				if (wizardDialog.open() == Window.CANCEL) {
//					System.out.println("Ok pressed");
//				} else {
//					System.out.println("Cancel pressed");
//				}
//			}
//		});
//		btnPrimitiveIntegration.setText("Primitive Integration");
//		//Visibility SET TO FALSE. REMOVE FOLLOWING LINE WHEN WORKING ON TASK INTEGRATION.
//		btnPrimitiveIntegration.setVisible(false);
//		//Task Integration
//		Button btnTaskIntegration = new Button(container, SWT.NONE);
//		btnTaskIntegration.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				WizardDialog wizardDialog = new WizardDialog(parent.getShell(), new TaskIntegrationWizard());
//				if (wizardDialog.open() == Window.CANCEL) {
//					System.out.println("Ok pressed");
//				} else {
//					System.out.println("Cancel pressed");
//				}
//			}
//		});
//		btnTaskIntegration.setText("Task Integration");
//		//Visibility SET TO FALSE. REMOVE FOLLOWING LINE WHEN WORKING ON PRIMITIVE INTEGRATION.
//		btnTaskIntegration.setVisible(false);
//
//		new Label(container, SWT.NONE);

	}

	public IProject getSelectedProject() {
		return this.selectedProject;
	}

	public Task getSelectedTask() {
		return (Task) ((IStructuredSelection) this.taskComboSelection.getSelection()).getFirstElement();
	}


	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			this.container.setFocus();
		}
	}
	
	private Image loadImage(String image) {
		try {
			final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
			if(bundle == null) {
				return null;
			}
			
			URL entry = bundle.getEntry("src/main/resources/images/" + image);
			URL resolvedURL = FileLocator.toFileURL(entry);
			URI resolvedURI = null;
			if(resolvedURL != null) {
				resolvedURI = new URI(
					resolvedURL.getProtocol(), 
					resolvedURL.getPath(),
					null);
			} else {
				resolvedURI = FileLocator.resolve(entry).toURI();
			}
			
			System.out.println(resolvedURI.toString());
			
			File file = new File(resolvedURI);
			InputStream is = new FileInputStream(file);
			
			return new Image(PlatformUI.getWorkbench().getDisplay(), is);
		} catch(final Exception ex) {
			Activator.getDefault().logError(ex);
		}
		
		return null;
	}
}

class SelectionButtonListener implements Listener {

	private final Button[] buttons;
	private final Image[] unclicked;
	private final Image[] clicked;
	private final String[] descs;
	
	private final Label targetLabel;
	
	public SelectionButtonListener(
		Button[] buttons,
		Image[] unclicked,
		Image[] clicked,
		String[] descs,
		Label targetLabel) {
		
		if(buttons.length != unclicked.length ||
			buttons.length != clicked.length ||
			buttons.length != descs.length) {
				throw new IllegalArgumentException(
					"All arrays are required to have the same length."
					+ "If not it indicates an incomplete setup for buttons and their images");
		}
		
		this.buttons = buttons;
		this.unclicked = unclicked;
		this.clicked = clicked;
		this.descs = descs;
		this.targetLabel = targetLabel;
	}
	
	
	
	@Override
	public void handleEvent(Event event) {
		Button eventButton = (Button)event.widget;
		for(int i = 0; i < buttons.length; i++) {
			Button b = buttons[i];
			if(eventButton.equals(b)) {
				b.setSelection(true);
				b.setImage(clicked[i]);
				targetLabel.setText(descs[i]);
			} else {
				b.setSelection(false);
				b.setImage(unclicked[i]);
			}
		}
	}
}