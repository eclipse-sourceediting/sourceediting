/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.contentassist.href;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImageHelper;
import org.eclipse.wst.xsl.ui.internal.util.XSLPluginImages;
import org.eclipse.wst.xsl.ui.provisional.contentassist.AbstractXSLContentAssistRequest;
import org.w3c.dom.Node;

/**
 * <p>This provides content assistance proposals for href attributes with a mode attribute.</p>
 * <p>Content assistance is restricted to files with the XSL content type that are in the same project as the file currently 
 * being edited. This is because projects in the workspace can have completely different physical file locations, leading to 
 * confusion in relative file paths across projects. Similarly, no content assistance is given for files external to the workspace. 
 * Such files should be assigned an absolute 'file:///' URL.</p>
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class HrefContentAssistRequest extends AbstractXSLContentAssistRequest
{
	private List<IPath> pathList = new ArrayList<IPath>();

	private final class XSLFileResourceVisitor implements IResourceVisitor
	{
		private final String precedingText;
		private final IFile editorFile;

		private XSLFileResourceVisitor(IFile editorFile, String precedingText)
		{
			this.precedingText = precedingText;
			this.editorFile = editorFile;
		}

		public boolean visit(IResource resource) throws CoreException
		{
			if (resource.getType() == IResource.FILE)
			{
				IFile file = (IFile)resource;
				if (XSLCore.isXSLFile(file) && !file.equals(editorFile))
				{
					IPath path = getRelativePath(editorFile, file);
					if (path.toString().startsWith(precedingText))
					{
						pathList.add(path);
					}
				}
			}
			return true;
		}
	}

	/**
	 * Constructor for creating the HrefContentAssistRequest class.
	 * 
	 * @param node
	 * @param parent
	 * @param documentRegion
	 * @param completionRegion
	 * @param begin
	 * @param length
	 * @param filter
	 * @param textViewer
	 */
	public HrefContentAssistRequest(Node node, IStructuredDocumentRegion documentRegion, ITextRegion completionRegion, int begin, int length, String filter, ITextViewer textViewer)
	{
		super(node, documentRegion, completionRegion, begin, length, filter, textViewer);
	}

	/**
	 * The main method that returns an array of proposals. Returns relative paths to files in the current project.
	 * 
	 * @return ICompletionProposal[] 
	 * @see org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest#getCompletionProposals()
	 */
	@Override
	public ArrayList<ICompletionProposal> getCompletionProposals()
	{
		pathList.clear();
		proposals.clear();
		
		try
		{
			String text = getText();
			String precedingText;
			
			int length = getCursorPosition()-getStartOffset();
			if (length > 0 && text.length() > length + 1)
				precedingText = text.substring(1,length);
			else
				precedingText = ""; //$NON-NLS-1$
			IFile editorFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getLocation()));
			editorFile.getProject().accept(new XSLFileResourceVisitor(editorFile,precedingText));

			Collections.sort(pathList,new PathComparator());
			for (IPath path : pathList)
			{
				String pathString = path.toString();
				CustomCompletionProposal proposal = new CustomCompletionProposal(
						pathString,
						getStartOffset() + 1,
						text.length()-2,
						pathString.length(),
						XSLPluginImageHelper.getInstance().getImage(XSLPluginImages.IMG_XSL_FILE),
						pathString,
						null, 
						null, 
						0,
						true
					);
				proposals.add(proposal);
			}
		}
		catch (CoreException e)
		{
			XSLUIPlugin.log(e);
		}

		return proposals;
	}
	
	/**
	 * Do not wish to sort the proposals - they are already sorted
	 * 
	 * @return the same list, in the same order
	 */
	protected List<ICompletionProposal> sortProposals(List<ICompletionProposal> proposalsIn) {
		return proposalsIn;
	}

	private IPath getRelativePath(IFile relativeTo, IFile file)
	{
		IPath filePath = file.getFullPath();
		IPath relativeToPath = relativeTo.getFullPath();
		
		IPath relPath;
		if (filePath.segmentCount() > relativeToPath.segmentCount())
		{
			relPath = filePath.removeFirstSegments(filePath.matchingFirstSegments(relativeToPath));
		}
		else if (filePath.segmentCount() < relativeToPath.segmentCount())
		{
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < relativeToPath.segmentCount() - filePath.segmentCount(); i++)
			{
				sb.append("../"); //$NON-NLS-1$
			}
			sb.append(file.getName());
			relPath = new Path(sb.toString());
		}
		else
		{
			relPath = new Path(file.getName());
		}
		return relPath;
	}
	
}
