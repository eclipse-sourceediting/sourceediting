/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.sse.ui.internal.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;

/**
 * An implementation of a presentation damager and presentation repairer.
 * It uses a LineStyleProvider to retrieve the style ranges associated with
 * the calculated damaged region.
 * 
 * @see LineStyleProvider
 */
public class StructuredDocumentDamagerRepairer extends DefaultDamagerRepairer {

	private LineStyleProvider fProvider = null;
	
	public StructuredDocumentDamagerRepairer() {
		super(new RuleBasedScanner());
	}
	
	public StructuredDocumentDamagerRepairer(LineStyleProvider provider) {
		super(new RuleBasedScanner());
		fProvider = provider;
	}

	public void createPresentation(TextPresentation presentation, ITypedRegion region) {
		if(fProvider != null) {
			Collection styles = new ArrayList();
			boolean handled = fProvider.prepareRegions(region, region.getOffset(), region.getLength(), styles);

			for(Iterator it = styles.iterator(); handled && it.hasNext(); )
				presentation.addStyleRange((StyleRange)it.next());
		}
			
	}
	
	public void setProvider(LineStyleProvider provider) {
		fProvider = provider;
	}
	
}
