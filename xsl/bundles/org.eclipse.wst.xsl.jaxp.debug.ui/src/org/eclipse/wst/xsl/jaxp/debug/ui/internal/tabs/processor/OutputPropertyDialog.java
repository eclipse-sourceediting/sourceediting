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
import org.eclipse.wst.xsl.jaxp.launching.IOutputProperty;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorType;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;
import org.eclipse.wst.xsl.jaxp.launching.LaunchProperties;

public class OutputPropertyDialog extends StatusDialog
{
	private Table table;
	private TableViewer tViewer;
	private Text text;
	private List<IOutputProperty> properties = new ArrayList<IOutputProperty>();
	private List<IOutputProperty> selectedProperties = new ArrayList<IOutputProperty>();

	public OutputPropertyDialog(Shell parent, LaunchProperties launchProperties)
	{
		super(parent);
		setTitle(Messages.getString("OutputPropertyDialog_0")); //$NON-NLS-1$
		
		Set<String> propertySet = new HashSet<String>();
		for (String att : launchProperties.getProperties().keySet())
		{
			propertySet.add(att);
		}
		for (IProcessorType type : JAXPRuntime.getProcessorTypes())
		{
			for (IOutputProperty property : type.getOutputProperties())
			{
				if (!propertySet.contains(property.getURI()))
				{
					propertySet.add(property.getURI());
					properties.add(property);
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
		label.setText(Messages.getString("OutputPropertyDialog_1")); //$NON-NLS-1$
		
		
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
				return properties.toArray(new IOutputProperty[0]);
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
				IOutputProperty property = (IOutputProperty)element;
				return property.getURI();
			}
		});
		tViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				IOutputProperty a1 = (IOutputProperty) e1;
				IOutputProperty a2 = (IOutputProperty) e2;
				return a1.getURI().compareTo(a2.getURI());
			}
		});
		tViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection sel = (IStructuredSelection)event.getSelection();
				selectedProperties = sel.toList();
				if (sel.isEmpty())
					text.setText(""); //$NON-NLS-1$
				else
				{
					IOutputProperty property = (IOutputProperty)sel.getFirstElement(); 
					text.setText(property.getDescription());
				}
			}
		});
		tViewer.addDoubleClickListener(new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event)
			{
				buttonPressed(IDialogConstants.OK_ID);
			}
		});
		tViewer.setInput(properties);
		
		text = new Text(comp,SWT.BORDER | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
		text.setEditable(false);
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		gd.heightHint = 80;
		text.setLayoutData(gd);
		
		if (properties.size() > 0)
		{
			tViewer.setSelection(new StructuredSelection(tViewer.getElementAt(0)), true);
		}
		
		return comp;
	}
	
	public List<IOutputProperty> getOutpuProperties()
	{
		return selectedProperties;
	}
}
