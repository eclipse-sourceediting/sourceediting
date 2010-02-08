/*******************************************************************************
 * Copyright (c) 2008, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (bug 271916) STAR - externalize strings for New XSL.
 *     Jesper Steen Moller - bug 289799 - React to the 'cursor' variable in the template
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.core.internal.provisional.contenttype.ContentTypeIdForXML;
import org.eclipse.wst.xsl.ui.internal.Messages;
import org.eclipse.wst.xsl.ui.internal.StructuredTextViewerConfigurationXSL;
import org.eclipse.wst.xsl.ui.internal.XSLUIConstants;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

public class NewXSLFileTemplatesWizardPage extends WizardPage
{
	private static final String XSL_UI_TEMPLATE_PREFERENCE_PAGE = "org.eclipse.wst.xsl.ui.template.preferencePage"; //$NON-NLS-1$
	private static final String NEW_STYLESHEET_TEMPLATES_WIZARD_PAGE = "NewStylesheetTemplatesWizardPage"; //$NON-NLS-1$
	private String fLastSelectedTemplateName;
	private SourceViewer fPatternViewer;
	private TableViewer fTableViewer;
	private TemplateStore fTemplateStore;
	private Button fUseTemplateButton;

	public NewXSLFileTemplatesWizardPage()
	{
		super(NEW_STYLESHEET_TEMPLATES_WIZARD_PAGE, Messages.NewXSLSelectTemplate, null); 
		setDescription(Messages.NewXSLTemplateDescription);
	}

	private void configureTableResizing(final Composite parent, final Table table, final TableColumn column1, final TableColumn column2)
	{
		parent.addControlListener(new ControlAdapter()
		{
			@Override
			public void controlResized(ControlEvent e)
			{
				Rectangle area = parent.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - 2 * table.getBorderWidth();
				if (preferredSize.y > area.height)
				{
					// Subtract the scrollbar width from the total column
					// width
					// if a vertical scrollbar will be required
					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}

				Point oldSize = table.getSize();
				if (oldSize.x > width)
				{
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					column1.setWidth(width / 2);
					column2.setWidth(width / 2);
					table.setSize(width, area.height);
				}
				else
				{
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					table.setSize(width, area.height);
					column1.setWidth(width / 2);
					column2.setWidth(width / 2);
				}
			}
		});
	}

	public void createControl(Composite ancestor)
	{
		Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		// create checkbox for user to use JSP Template
		fUseTemplateButton = new Button(parent, SWT.CHECK);
		fUseTemplateButton.setText(Messages.NewXSLUseTemplateButtonText);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		fUseTemplateButton.setLayoutData(data);
		fUseTemplateButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				enableTemplates();
			}
		});

		// create composite for Templates table
		Composite innerParent = new Composite(parent, SWT.NONE);
		GridLayout innerLayout = new GridLayout();
		innerLayout.numColumns = 2;
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerParent.setLayout(innerLayout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		innerParent.setLayoutData(gd);

		// Create linked text to just to templates preference page
		Link link = new Link(innerParent, SWT.NONE);
		link.setText(Messages.NewXSLLinkPreferencePage);
		data = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		link.setLayoutData(data);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				linkClicked();
			}
		});

		// create table that displays templates
		Table table = new Table(innerParent, SWT.BORDER | SWT.FULL_SELECTION);

		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(2);
		data.heightHint = convertHeightInCharsToPixels(10);
		data.horizontalSpan = 2;
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);

		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText(Messages.NewXSLColumnTemplateName);

		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText(Messages.NewXSLColumnTemplateDescription);

		fTableViewer = new TableViewer(table);
		fTableViewer.setLabelProvider(new TemplateLabelProvider());
		fTableViewer.setContentProvider(new TemplateContentProvider());

		fTableViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object object1, Object object2)
			{
				if ((object1 instanceof Template) && (object2 instanceof Template))
				{
					Template left = (Template) object1;
					Template right = (Template) object2;
					int result = left.getName().compareToIgnoreCase(right.getName());
					if (result != 0)
						return result;
					return left.getDescription().compareToIgnoreCase(right.getDescription());
				}
				return super.compare(viewer, object1, object2);
			}

			@Override
			public boolean isSorterProperty(Object element, String property)
			{
				return true;
			}
		});

		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				updateViewerInput();
			}
		});

		// create viewer that displays currently selected template's contents
		fPatternViewer = doCreateViewer(parent);

		fTemplateStore = XSLUIPlugin.getDefault().getTemplateStore();
		fTableViewer.setInput(fTemplateStore);

		configureTableResizing(innerParent, table, column1, column2);
		loadLastSavedPreferences();

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.JSP_NEWWIZARD_TEMPLATE_HELPID);
		Dialog.applyDialogFont(parent);
		setControl(parent);
	}

	private SourceViewer createViewer(Composite parent)
	{
		SourceViewerConfiguration sourceViewerConfiguration = new StructuredTextViewerConfiguration() {
			StructuredTextViewerConfiguration baseConfiguration = new StructuredTextViewerConfigurationXSL();

			@Override
			public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
				return baseConfiguration.getConfiguredContentTypes(sourceViewer);
			}

			@Override
			public LineStyleProvider[] getLineStyleProviders(ISourceViewer sourceViewer, String partitionType) {
				return baseConfiguration.getLineStyleProviders(sourceViewer, partitionType);
			}
		};
		SourceViewer viewer = new StructuredTextViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		((StructuredTextViewer) viewer).getTextWidget().setFont(JFaceResources.getFont("org.eclipse.wst.sse.ui.textfont")); //$NON-NLS-1$
		IStructuredModel scratchModel = StructuredModelManager.getModelManager().createUnManagedStructuredModelFor(ContentTypeIdForXML.ContentTypeID_XML);
		IDocument document = scratchModel.getStructuredDocument();
		viewer.configure(sourceViewerConfiguration);
		viewer.setDocument(document);
		return viewer;
	}

	private SourceViewer doCreateViewer(Composite parent)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.NewXSLTemplatePreviewTitle);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		SourceViewer viewer = createViewer(parent);
		viewer.setEditable(false);

		Control control = viewer.getControl();
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		data.heightHint = convertHeightInCharsToPixels(5);
		control.setLayoutData(data);

		return viewer;
	}

	void enableTemplates()
	{
		boolean enabled = fUseTemplateButton.getSelection();

		if (enabled)
		{
			setSelectedTemplate(fLastSelectedTemplateName);
		}
		else
		{
			// save last selected template
			Template template = getSelectedTemplate();
			if (template != null)
				fLastSelectedTemplateName = template.getName();
			else
				fLastSelectedTemplateName = ""; //$NON-NLS-1$

			fTableViewer.setSelection(null);
		}

		fTableViewer.getControl().setEnabled(enabled);
		fPatternViewer.getControl().setEnabled(enabled);
	}

	private Template getSelectedTemplate()
	{
		Template template = null;
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();

		if (selection.size() == 1)
		{
			template = (Template) selection.getFirstElement();
		}
		return template;
	}

	String getTemplateString(int[] offset)
	{
		String templateString = null;
		offset[0] = 0;

		Template template = getSelectedTemplate();
		if (template != null)
		{
			TemplateContextType contextType = XSLUIPlugin.getDefault().getTemplateContextRegistry().getContextType(XSLUIConstants.TEMPLATE_CONTEXT_XSL_NEW);
			IDocument document = new Document();
			TemplateContext context = new DocumentTemplateContext(contextType, document, 0, 0);
			try
			{
				TemplateBuffer buffer = context.evaluate(template);
				templateString = buffer.getString();
				for(TemplateVariable t : buffer.getVariables())
				{
					if (t.getName().equals(org.eclipse.jface.text.templates.GlobalTemplateVariables.Cursor.NAME)) {
						if (t.getOffsets().length > 0) offset[0] = t.getOffsets()[0];
					}
				}
			}
			catch (Exception e)
			{
				XSLUIPlugin.log(e);
			}
		}

		return templateString;
	}

	void linkClicked()
	{
		String pageId = XSL_UI_TEMPLATE_PREFERENCE_PAGE;
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(), pageId, new String[] { pageId }, null);
		dialog.open();
		fTableViewer.refresh();
	}

	private void loadLastSavedPreferences()
	{
		String templateName = XSLUIPlugin.getDefault().getPreferenceStore().getString(XSLUIConstants.NEW_FILE_TEMPLATE_NAME);
		if (templateName == null || templateName.length() == 0)
		{
			fLastSelectedTemplateName = ""; //$NON-NLS-1$
			fUseTemplateButton.setSelection(false);
		}
		else
		{
			fLastSelectedTemplateName = templateName;
			fUseTemplateButton.setSelection(true);
		}
		enableTemplates();
	}

	void saveLastSavedPreferences()
	{
		String templateName = ""; //$NON-NLS-1$

		Template template = getSelectedTemplate();
		if (template != null)
		{
			templateName = template.getName();
		}

		XSLUIPlugin.getDefault().getPreferenceStore().setValue(XSLUIConstants.NEW_FILE_TEMPLATE_NAME, templateName);
		XSLUIPlugin.getDefault().savePluginPreferences();
	}

	private void setSelectedTemplate(String templateName)
	{
		Object template = null;

		if (templateName != null && templateName.length() > 0)
		{
			// pick the last used template
			template = fTemplateStore.findTemplate(templateName, XSLUIConstants.TEMPLATE_CONTEXT_XSL_NEW);
		}

		// no record of last used template so just pick first element
		if (template == null)
		{
			// just pick first element
			template = fTableViewer.getElementAt(0);
		}

		if (template != null)
		{
			IStructuredSelection selection = new StructuredSelection(template);
			fTableViewer.setSelection(selection, true);
		}
	}

	void updateViewerInput()
	{
		Template template = getSelectedTemplate();
		if (template != null)
		{
			fPatternViewer.getDocument().set(template.getPattern());
		}
		else
		{
			fPatternViewer.getDocument().set(""); //$NON-NLS-1$
		}
	}
	

}
