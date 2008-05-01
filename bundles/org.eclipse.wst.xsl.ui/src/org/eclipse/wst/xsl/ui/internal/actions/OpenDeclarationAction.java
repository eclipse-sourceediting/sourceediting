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
package org.eclipse.wst.xsl.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xsl.ui.internal.editor.XSLHyperlinkDetector;

public class OpenDeclarationAction extends Action
{
	private XSLHyperlinkDetector detector = new XSLHyperlinkDetector();
	private ITextViewer textViewer;
	private IRegion region;
	
	public OpenDeclarationAction(ITextViewer textViewer)
	{
		this.textViewer = textViewer;
	}
	
	public void setRegion(IRegion region)
	{
		this.region = region;
	}
	
	public void run()
	{
		IHyperlink[] links = detector.detectHyperlinks(textViewer, region, true);
		if (links.length > 0)
		{
			IHyperlink link = links[0];
			link.open();
		}
	}
}
