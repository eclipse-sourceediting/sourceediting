package org.eclipse.wst.xsl.jaxp.debug.ui.internal.tabs.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.jaxp.launching.IAttribute;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;
import org.eclipse.wst.xsl.jaxp.launching.LaunchAttributes;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;

public class AttributeDialog extends StatusDialog
{
	private Table table;
	private TableViewer tViewer;
	private Text text;
	private List<IAttribute> attributes = new ArrayList<IAttribute>();
	private List<IAttribute> selectedAttributes = new ArrayList<IAttribute>();

	public AttributeDialog(Shell parent, LaunchAttributes launchAttributes)
	{
		super(parent);
		setTitle(Messages.getString("AttributeDialog_0")); //$NON-NLS-1$
		
		Set<String> attributeSet = new HashSet<String>();
		for (LaunchAttribute att : launchAttributes.getAttributes())
		{
			attributeSet.add(att.uri);
		}
		for (IProcessorType type : JAXPRuntime.getProcessorTypes())
		{
			for (IAttribute attribute : type.getAttributes())
			{
				if (!attributeSet.contains(attribute.getURI()))
				{
					attributeSet.add(attribute.getURI());
					attributes.add(attribute);
				}
			}
		}
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite comp = new Composite(parent,SWT.NONE);
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		gd.widthHint = 400;
		comp.setLayoutData(gd);
		comp.setLayout(new GridLayout());
		
		Label label = new Label(comp,SWT.NONE);
		label.setText(Messages.getString("AttributeDialog_1")); //$NON-NLS-1$
		
		
		table = new Table(comp,SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.MULTI);
		table.setHeaderVisible(false);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		gd.verticalIndent = 10;
		gd.heightHint = 200;
		table.setLayoutData(gd);
		
		tViewer = new TableViewer(table);
		tViewer.setContentProvider(new IStructuredContentProvider()
		{
			public Object[] getElements(Object inputElement)
			{
				return attributes.toArray(new IAttribute[0]);
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}
		});
		tViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element)
			{
				IAttribute attribute = (IAttribute)element;
				return attribute.getURI();
			}
		});
		tViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				IAttribute a1 = (IAttribute) e1;
				IAttribute a2 = (IAttribute) e2;
				return a1.getURI().compareTo(a2.getURI());
			}
		});
		tViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				selectedAttributes = sel.toList();
				if (sel.isEmpty())
					text.setText(Messages.getString("AttributeDialog_2")); //$NON-NLS-1$
				else
				{
					IAttribute attribute = (IAttribute)sel.getFirstElement(); 
					text.setText(attribute.getDescription());
				}
			}
		});
		tViewer.addDoubleClickListener(new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event)
			{
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
		tViewer.setInput(attributes);
		
		text = new Text(comp,SWT.BORDER | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
		text.setEditable(false);
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.heightHint = 80;
		text.setLayoutData(gd);
		
		if (attributes.size() > 0)
		{
			tViewer.setSelection(new StructuredSelection(tViewer.getElementAt(0)), true);
		}
		
		return comp;
	}
	
	public List<IAttribute> getAttributes()
	{
		return selectedAttributes;
	}
}
