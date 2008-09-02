package org.eclipse.wst.xsl.ui.internal;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.model.StylesheetModel;

public class StylesheetView extends ViewPart
{

	private TreeViewer tv;

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);
		site.getPage().addPartListener(new IPartListener(){

			private IEditorPart currentEditor;
			
			public void partActivated(IWorkbenchPart part)
			{
				if (part instanceof IEditorPart)
				{
					IEditorPart editor = (IEditorPart)part;
					IEditorInput edInput = editor.getEditorInput();
					if (edInput instanceof IFileEditorInput)
					{
						IFileEditorInput edFileInput = (IFileEditorInput)edInput;
						if (XSLCore.isXSLFile(edFileInput.getFile()))
						{
							currentEditor = editor;
							StylesheetModel input = XSLCore.getInstance().getStylesheet(edFileInput.getFile());
							tv.setInput(input);
						}
					}
				}
			}

			public void partBroughtToTop(IWorkbenchPart part)
			{
			}

			public void partClosed(IWorkbenchPart part)
			{
				if (part == currentEditor)
				{
					tv.setInput(null);
					currentEditor = null;
				}
			}

			public void partDeactivated(IWorkbenchPart part)
			{
			}

			public void partOpened(IWorkbenchPart part)
			{
			}
			
		});
	}
	
	@Override
	public void createPartControl(Composite parent)
	{
		Tree tree = new Tree(parent,SWT.NONE);
		this.tv = new TreeViewer(tree);
		tv.setContentProvider(new BaseWorkbenchContentProvider());
		tv.setLabelProvider(new WorkbenchLabelProvider());
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

}
