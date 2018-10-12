package de.cognicrypt.codegenerator.wizard;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.cognicrypt.codegenerator.Activator;


public class AltConfigWizard extends Wizard {

	WizardPage taskListPage;
	
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
		// TODO do within task selection
		// LocatorPage locatorPage = new LocatorPage("Locator");
		// addPage(locatorPage);
    }
	
	
	@Override
	public boolean performFinish() {
		return false;
	}

}
