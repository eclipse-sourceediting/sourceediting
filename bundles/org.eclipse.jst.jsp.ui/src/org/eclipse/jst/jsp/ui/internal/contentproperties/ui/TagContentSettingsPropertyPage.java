package org.eclipse.jst.jsp.ui.internal.contentproperties.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.modelquery.TagModelQuery;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class TagContentSettingsPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
	private static final String[] fDisplayTypes = {"HTML", "XML"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final String[] fValues = {"text/html", "text/xml"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private ComboFieldEditor fComboFieldEditor;

	public TagContentSettingsPropertyPage() {
		super();
		noDefaultAndApplyButton();
		setDescription(JSPUIMessages.TagPropertyPage_desc);
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(GridDataFactory.fillDefaults());
		composite.setLayout(new GridLayout(2, true));

		Object adapter = getElement().getAdapter(IFile.class);
		if (adapter == null) {
			adapter = getElement().getAdapter(IResource.class);
		}
		if (adapter != null && adapter instanceof IResource) {
			String preferenceKey = TagModelQuery.createPreferenceKey(((IResource) adapter).getFullPath());
			new DefaultScope().getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName()).put(preferenceKey, fValues[0]);
			ScopedPreferenceStore store = new ScopedPreferenceStore(new ProjectScope(((IResource) adapter).getProject()), JSPCorePlugin.getDefault().getBundle().getSymbolicName());

			String[][] entryNamesAndValues = new String[][]{{fDisplayTypes[0], fValues[0]}, {fDisplayTypes[1], fValues[1]}};
			fComboFieldEditor = new ComboFieldEditor(preferenceKey, JSPUIMessages.JSPFContentSettingsPropertyPage_2, entryNamesAndValues, composite);
			fComboFieldEditor.fillIntoGrid(composite, 2);
			fComboFieldEditor.setPreferenceStore(store);
			fComboFieldEditor.load();

			// let the page save for us if needed
			setPreferenceStore(store);
		}
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		if (fComboFieldEditor != null) {
			fComboFieldEditor.store();
		}
		return super.performOk();
	}
}
