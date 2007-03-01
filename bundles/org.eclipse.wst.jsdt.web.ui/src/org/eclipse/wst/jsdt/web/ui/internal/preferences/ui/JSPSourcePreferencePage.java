package org.eclipse.wst.jsdt.web.ui.internal.preferences.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class JSPSourcePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public void init(IWorkbench workbench) {
		// do nothing
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = createScrolledComposite(parent);

		Text label = new Text(composite, SWT.READ_ONLY);
		label.setText(JSPUIMessages.JSPSourcePreferencePage_0);
		GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		data.horizontalIndent = 0;
		label.setLayoutData(data);

		PreferenceLinkArea fileEditorsArea = new PreferenceLinkArea(
				composite,
				SWT.NONE,
				"org.eclipse.wst.html.ui.preferences.source", JSPUIMessages.JSPSourcePreferencePage_1,//$NON-NLS-1$
				(IWorkbenchPreferenceContainer) getContainer(), null);

		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.horizontalIndent = 5;
		fileEditorsArea.getControl().setLayoutData(data);

		PreferenceLinkArea contentTypeArea = new PreferenceLinkArea(
				composite,
				SWT.NONE,
				"org.eclipse.wst.sse.ui.preferences.xml.source", JSPUIMessages.JSPSourcePreferencePage_2,//$NON-NLS-1$
				(IWorkbenchPreferenceContainer) getContainer(), null);

		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.horizontalIndent = 5;
		contentTypeArea.getControl().setLayoutData(data);

		setSize(composite);
		return composite;
	}

	private Composite createScrolledComposite(Composite parent) {
		// create scrollbars for this parent when needed
		final ScrolledComposite sc1 = new ScrolledComposite(parent,
				SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite composite = createComposite(sc1);
		sc1.setContent(composite);

		// not calling setSize for composite will result in a blank composite,
		// so calling it here initially
		// setSize actually needs to be called after all controls are created,
		// so scrolledComposite
		// has correct minSize
		setSize(composite);
		return composite;
	}

	private void setSize(Composite composite) {
		if (composite != null) {
			// Note: The font is set here in anticipation that the class
			// inheriting
			// this base class may add widgets to the dialog. setSize
			// is assumed to be called just before we go live.
			applyDialogFont(composite);
			Point minSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			composite.setSize(minSize);
			// set scrollbar composite's min size so page is expandable but
			// has scrollbars when needed
			if (composite.getParent() instanceof ScrolledComposite) {
				ScrolledComposite sc1 = (ScrolledComposite) composite
						.getParent();
				sc1.setMinSize(minSize);
				sc1.setExpandHorizontal(true);
				sc1.setExpandVertical(true);
			}
		}
	}

	private Composite createComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);

		// GridData
		GridData data = new GridData(GridData.FILL);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		composite.setFont(parent.getFont());
		return composite;
	}
}
