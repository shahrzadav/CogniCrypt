/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.core.properties;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.osgi.service.prefs.BackingStoreException;
import de.cognicrypt.core.Activator;

public class CogniCryptPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static List<PreferenceListener> preferenceListeners = new ArrayList<>();

	@Override
	public void init(IWorkbench CogniWorkbench) {}

	@Override
	protected Control createContents(Composite parent) {
		final Composite container = new Composite(parent, SWT.FILL);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(1, true));
		notifyBasicPreferenceListeners(container);
		
		new Label(container, SWT.NONE);
		final ExpandableComposite collap = new ExpandableComposite(container, SWT.Collapse);
		collap.setText("Advanced Options");

		final Composite advancedOptions = new Composite(collap, SWT.None);
		collap.setClient(advancedOptions);
		collap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		advancedOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		advancedOptions.setLayout(new RowLayout(SWT.VERTICAL));

		notifyAdvancedPreferenceListeners(advancedOptions);
		
		collap.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
//				System.out.println("SIZE OF COLLAP : " + collap.getSize());
//				System.out.println("SIZE OF whole : " + container.getSize());
				container.layout(true);

			}
		});
		return container;
	}

	@Override
	public boolean performOk() {
		storeValues();
		try {
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		}
		catch (BackingStoreException e) {
			Activator.getDefault().logError(e, "Failed to store preferences. Please report this bug to the maintainers.");
		}
		return true;
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		setDefaultValues();
	}

	public static void registerPreferenceListener(PreferenceListener listener) {
		preferenceListeners.add(listener);
	}

	public static void notifyBasicPreferenceListeners(Composite basic) {
		preferenceListeners.stream().forEach(e -> e.compileBasicPreferences(basic));
	}

	public static void notifyAdvancedPreferenceListeners(Composite advanced) {
		preferenceListeners.stream().forEach(e -> e.compileAdvancedPreferences(advanced));
	}

	public static void setDefaultValues() {
		preferenceListeners.parallelStream().forEach(e -> e.setDefaultValues());
	}

	private void storeValues() {
		preferenceListeners.parallelStream().forEach(e -> e.storeValues());
	}

}
