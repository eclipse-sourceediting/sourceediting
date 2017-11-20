/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;

/**
 * Complex type definition for the head content.
 * Content Model: (TITLE & ISINDEX? & BASE?)
 */
final class CtdHead extends ComplexTypeDefinition {

	/**
	 */
	public CtdHead(ElementCollection elementCollection) {
		super(elementCollection);
		primaryCandidateName = HTML40Namespace.ElementName.TITLE;
	}

	/**
	 * for HEAD.
	 * To avoid using inclusion, the content model comes from the XHTML 1.0.
	 *
	 * (%head.misc;, ((title, %head.misc;, (base, %head.misc;)?) | (base, %head.misc;, (title, %head.misc;))))
	 * And %head.misc; is:
	 * (script|style|meta|link|object|isindex)*
	 *
	 * 0: (%head.misc, A)
	 * A: (B | C)
	 * B: (title, %head.misc;, D)
	 * C: (base, %head.misc;, E)
	 * D: (base, %head.misc;)?
	 * E: (title, %head.misc;)
	 */
	protected void createContent() {
		if (content != null)
			return; // already created.
		if (collection == null)
			return;

		// At 1st, create %head.misc; content.
		// %head.misc;
		//   ( | )*
		CMGroupImpl misc = new CMGroupImpl(CMGroup.CHOICE, 0, CMContentImpl.UNBOUNDED);
		if (misc == null)
			return;
		String[] names = {HTML40Namespace.ElementName.SCRIPT, HTML40Namespace.ElementName.STYLE, HTML40Namespace.ElementName.META, HTML40Namespace.ElementName.LINK, HTML40Namespace.ElementName.OBJECT, HTML40Namespace.ElementName.ISINDEX};
		collection.getDeclarations(misc, Arrays.asList(names).iterator());
		// 2nd, get a title
		CMNode title = collection.getNamedItem(HTML40Namespace.ElementName.TITLE);
		// 3rd, get a base
		CMNode base = collection.getNamedItem(HTML40Namespace.ElementName.BASE);
		if (title == null || base == null)
			return;

		// Top level content is a sequence of %head.misc; and A.
		// 0: (%head.misc;, A)
		//   create a sequence
		content = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
		if (content == null)
			return;
		//   append %head.misc;
		content.appendChild(misc);
		//   create A and append it to the top level.
		{
			// A is a choice of B and C.
			// A: (B | C)
			//   create a choice
			CMGroupImpl gA = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
			if (gA == null)
				return;
			//   append A to the top level.
			content.appendChild(gA);

			// create B and append it to A
			{
				// B is a sequence of title, %head.misc;, and D.
				// B: (title, %head.misc;, D)
				//   create a sequence
				CMGroupImpl gB = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
				if (gB == null)
					return;
				//   append B to A.
				gA.appendChild(gB);

				//   append title to B
				gB.appendChild(title);
				//   append %head.misc; to B
				gB.appendChild(misc);
				//   create D and append it to B.
				{
					// D is a sequence of base, %head.misc;.
					// D: (base, %head.misc;)?
					//   create a sequence
					CMGroupImpl gD = new CMGroupImpl(CMGroup.SEQUENCE, 0, 1);
					if (gD == null)
						return;
					//   append D to B.
					gB.appendChild(gD);

					//   append base to D
					gD.appendChild(base);
					//   append %head.misc; to D.
					gD.appendChild(misc);
				}
			}
			// create C and append it to A
			{
				// C is a sequence of base, %head.misc;, and E
				// C: (base, %head.misc;, E)
				//   create a sequence
				CMGroupImpl gC = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
				if (gC == null)
					return;
				//   append C to A.
				gA.appendChild(gC);

				//   append base to C
				gC.appendChild(base);
				//   append %head.misc; to C
				gC.appendChild(misc);

				//   create E and append it to C.
				{
					// E is a sequence of title and %head.misc;.
					// E: (title, %head.misc;)
					//   create a sequence
					CMGroupImpl gE = new CMGroupImpl(CMGroup.SEQUENCE, 1, 1);
					if (gE == null)
						return;
					//   append E to C.
					gC.appendChild(gE);

					//   append title to E
					gE.appendChild(title);
					//   append %head.misc; to E.
					gE.appendChild(misc);
				}
			}
		}
	}

	/**
	 * Element content.
	 */
	public int getContentType() {
		return CMElementDeclaration.ELEMENT;
	}

	/**
	 * Name of complex type definition.
	 * Each singleton must know its own name.
	 * All names should be defined in
	 * {@link <code>ComplexTypeDefinitionFactory</code>} as constants.<br>
	 * @return java.lang.String
	 */
	public String getTypeName() {
		return ComplexTypeDefinitionFactory.CTYPE_HEAD;
	}
}
