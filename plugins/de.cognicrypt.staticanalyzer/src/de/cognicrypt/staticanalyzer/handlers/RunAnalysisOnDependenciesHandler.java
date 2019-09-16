package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;

public class RunAnalysisOnDependenciesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		IProject ip = Utils.getCurrentlySelectedIProject();
		IJavaProject javaProject = JavaCore.create(ip);
		if (javaProject == null) {
			Activator.getDefault().logInfo("JavaCore could not create IJavaProject for project " + ip.getName() + ".");
			return false;
		}

		final AnalysisKickOff akf = new AnalysisKickOff();
		akf.analyzeDependenciesOnly(true);
		if (akf.setUp(javaProject)) {
			akf.run();
		}

		return null;
	}

}
