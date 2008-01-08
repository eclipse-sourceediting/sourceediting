/*******************************************************************************
 * Copyright (c) 2005-2007 Orangevolt (www.orangevolt.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Lars Gersmann (www.orangevolt.com) 
 *     Doug Satchwell (http://www.chasetechnology.co.uk) 
 *     
 *******************************************************************************/

package org.eclipse.wst.xsl.internal.debug.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * <p>
 * A console line tracker for XSL launches.
 * </p>
 * 
 * <p>
 * Note: It is actually configured to run for all Java launches in
 * <code>plugin.xml</code>, but it only really becomes enabled when the
 * launch configuration type is
 * <code>org.eclipse.wst.xsl.launching.launchConfigurationType</code>.
 * </p>
 */
public class XSLConsoleLineTracker implements IConsoleLineTracker
{
	private IConsole console;
	private boolean enabled;

	public void dispose()
	{
		console = null;
	}

	public void init(IConsole console)
	{
		this.console = console;
		this.enabled = false;
		try
		{
			String launchTypeId = console.getProcess().getLaunch().getLaunchConfiguration().getType().getIdentifier();
			this.enabled = "org.eclipse.wst.xsl.launching.launchConfigurationType".equals(launchTypeId);
		}
		catch (CoreException e)
		{
			// do nothing
		}
	}

	public void lineAppended(IRegion line)
	{
		if (!enabled)
			return;

		int lineOffset = line.getOffset();
		int lineLength = line.getLength();
		try
		{
			String text = console.getDocument().get(lineOffset, lineLength);
			int i;
			if ((i = text.indexOf("file:/")) != -1 || (i = text.indexOf("http://")) != -1 //$NON-NLS-1$ //$NON-NLS-2$
					|| (i = text.indexOf("https://")) != -1 || (i = text.indexOf("ftp://")) != -1) //$NON-NLS-1$ //$NON-NLS-2$
			{
				int v = i + 9;
				StringBuffer sb = new StringBuffer(text.substring(i, v));
				for (; v < text.length(); v++)
				{
					char c = text.charAt(v);
					if (c != ' ' && c != ':' && c != ';' && c != '{' && c != '}' && c != '[' && c != ']' && c != '(' && c != ')')
					{
						sb.append(c);
					}
					else
					{
						break;
					}
				}

				ExternalFileConsoleHyperLink link = new ExternalFileConsoleHyperLink(sb.toString(), -1);
				console.addLink(link, lineOffset + i, v - i);
			}
		}
		catch (BadLocationException e)
		{
			// do nothing
		}
	}

	private static class ExternalFileConsoleHyperLink implements IHyperlink
	{
		private final int fLineNumber;
		private final String uri;

		public ExternalFileConsoleHyperLink(String uri, int lineNumber)
		{
			this.fLineNumber = lineNumber;
			this.uri = uri;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.debug.ui.console.IConsoleHyperlink#linkEntered()
		 */
		public void linkEntered()
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.debug.ui.console.IConsoleHyperlink#linkExited()
		 */
		public void linkExited()
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.debug.ui.console.IConsoleHyperlink#linkActivated()
		 */
		public void linkActivated()
		{
			URL url = null;
			try
			{
				url = new URL(uri);
			}
			catch (MalformedURLException e)
			{
				return;
			}
			if ("file".equals(url.getProtocol()))
			{
				String path = URLDecoder.decode(url.getPath());
				Path fExternalPath = new Path(path);
				IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(fExternalPath);
				if (files.length > 0)
				{
					for (int i = 0; i < files.length; i++)
					{
						IFile curr = files[0];
						IEditorPart part;
						try
						{
							part = IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), curr);
							if (part != null && fLineNumber != -1)
							{
								if (part instanceof ITextEditor)
								{
									try
									{
										revealLine((ITextEditor) part, fLineNumber);
									}
									catch (BadLocationException e)
									{
										// ignore
									}
								}
								return;
							}
						}
						catch (PartInitException e)
						{
							// ignore
						}
					}
				}
			}
			else
			{
				Program.launch(url.toExternalForm());
			}
		}

		private void revealLine(ITextEditor editor, int lineNumber) throws BadLocationException
		{
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			IRegion region = document.getLineInformation(lineNumber - 1);
			editor.selectAndReveal(region.getOffset(), 0);
		}
	}
}
