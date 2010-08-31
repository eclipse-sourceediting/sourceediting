/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Arrays;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Complex type definition for <code>HTML</code>.<br>
 * Content Model:
 * HEAD, (FRAMESET|BODY)<br>
 */
final class CtdHtml extends ComplexTypeDefinition {

	/**
	 */
	public CtdHtml(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML40Namespace.ElementName.HEAD;
	}

	/**
	 * (%html.content;).
	 * %html.content; is HEAD, (FRAMESET | BODY).
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		// ( )
		content = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		if (content == null)
			return;

		// HEAD
		CMNode edec = collection.getNamedItem(HTML40Namespace.ElementName.HEAD);
		if (edec != null)
			content.appendChild(edec);

		// ( | )
		CMGroupImpl group = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		content.appendChild(group);

		// FRAMESET, BODY
		String[] names = {HTML40Namespace.ElementName.FRAMESET, HTML40Namespace.ElementName.BODY};
		collection.getDeclarations(group, Arrays.asList(names).iterator());

		// since BODY start and end are omissable
		// adding valid children of BODY here under HTML
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=97342
	    edec = collection.getNamedItem(HTML40Namespace.ElementName.MAP);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.PRE);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.BDO);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.INPUT);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.P);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.NOSCRIPT);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.I);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.BUTTON);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.LABEL);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.U);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.H6);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.CENTER);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.BASEFONT);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.S);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.BLOCKQUOTE);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.H3);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.UL);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.B);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.SELECT);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.Q);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.STRIKE);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.SCRIPT);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.ABBR);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.BIG);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.H1);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.IMG);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.ACRONYM);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.DEL);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.NOFRAMES);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.TEXTAREA);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.H2);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.FONT);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.OBJECT);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.KBD);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.IFRAME);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.HR);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.H4);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.DIR);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.SAMP);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.INS);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.H5);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.SUP);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.A);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.DFN);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.ISINDEX);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.DL);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.VAR);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.FIELDSET);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.TABLE);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.BR);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.TT);
		if (edec != null)
			content.appendChild(edec);

		edec = collection.getNamedItem(HTML40Namespace.ElementName.APPLET);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.OL);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.SMALL);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.CITE);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.FORM);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.DIV);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.CODE);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.SPAN);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.SUB);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.EM);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.MENU);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.ADDRESS);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML40Namespace.ElementName.STRONG);
		if (edec != null)
			content.appendChild(edec);
		edec = collection.getNamedItem(HTML50Namespace.ElementName.ARTICLE);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML50Namespace.ElementName.ASIDE);
		if (edec != null)
			content.appendChild(edec);
		edec = collection.getNamedItem(HTML50Namespace.ElementName.NAV);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML50Namespace.ElementName.SECTION);
		if (edec != null)
			content.appendChild(edec);
		edec = collection.getNamedItem(HTML50Namespace.ElementName.AUDIO);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML50Namespace.ElementName.VIDEO);
		if (edec != null)
			content.appendChild(edec);
		edec = collection.getNamedItem(HTML50Namespace.ElementName.CANVAS);
		if (edec != null)
			content.appendChild(edec);

	    edec = collection.getNamedItem(HTML50Namespace.ElementName.COMMAND);
		if (edec != null)
			content.appendChild(edec);
		
	    edec = collection.getNamedItem(HTML50Namespace.ElementName.HEADER);
		if (edec != null)
			content.appendChild(edec);
		
	    edec = collection.getNamedItem(HTML50Namespace.ElementName.FOOTER);
		if (edec != null)
			content.appendChild(edec);
		
	    edec = collection.getNamedItem(HTML50Namespace.ElementName.MARK);
		if (edec != null)
			content.appendChild(edec);
		
		edec = collection.getNamedItem(HTML50Namespace.ElementName.FIGURE);
		if (edec != null)
			content.appendChild(edec);
		edec = collection.getNamedItem(HTML50Namespace.ElementName.RUBY);
		if (edec != null)
			content.appendChild(edec);
	}

	/**
	 * Element content.
	 */
	public int getContentType() {
		return CMElementDeclaration.ELEMENT;
	}

	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_HTML;
	}
}
