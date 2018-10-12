package de.cognicrypt.codegenerator.wizard;

import java.util.HashMap;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.cognicrypt.codegenerator.Activator;


public class AltConfigWizard extends Wizard {

	
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
		LocatorPage locatorPage = new LocatorPage("Locator");
		setForcePreviousAndNextButtons(true);
		addPage(locatorPage);
    }
	
	
	@Override
	public boolean performFinish() {
		return false;
	}

}
