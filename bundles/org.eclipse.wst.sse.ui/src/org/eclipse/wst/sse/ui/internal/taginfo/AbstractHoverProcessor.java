/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.taginfo;

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.actions.ActionDefinitionIds;
import org.eclipse.wst.sse.ui.internal.derived.HTMLTextPresenter;

/**
 * Abstract class for providing hover information for Source editor. Includes
 * a hover control creator which has the "Press F2 for focus" message built
 * in.
 * 
 * @since WTP 1.5
 */
abstract public class AbstractHoverProcessor implements ITextHover, ITextHoverExtension {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true), getTooltipAffordanceString());
			}
		};
	}

	/**
	 * Returns the short cut assigned to the sub menu or <code>null</code>
	 * if no short cut is assigned.
	 * 
	 * @return the short cut as a human readable string or <code>null</code>
	 */
	private String getShortCutString() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IBindingService bindingService = (IBindingService) workbench.getAdapter(IBindingService.class);
		final TriggerSequence[] activeBindings = bindingService.getActiveBindingsFor(ActionDefinitionIds.INFORMATION);
		if (activeBindings.length > 0) {
			return activeBindings[0].format();
		}

		return null;
	}

	/**
	 * Returns the tool tip affordance string.
	 * 
	 * @return the affordance string or <code>null</code> if disabled or no
	 *         key binding is defined
	 * @since 3.0
	 */
	private String getTooltipAffordanceString() {
		String sticky = null;
		String keySequence = getShortCutString();
		if (keySequence != null) {
			sticky = NLS.bind(SSEUIMessages.textHoverMakeStickyHint, keySequence);
		}
		return sticky;
	}
}
