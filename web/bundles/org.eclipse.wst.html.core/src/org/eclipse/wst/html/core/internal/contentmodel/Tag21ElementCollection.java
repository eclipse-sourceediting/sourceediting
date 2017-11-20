/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;

public class Tag21ElementCollection extends Tag20ElementCollection {
	
	class TACreater21 extends TACreater20{

		void createForDirTag() {
			super.createForDirTag();
			AttrDecl adec;
			// ("trimDirectiveWhitespaces" ENUM DECLARED (true|false) "false")
			adec = createBoolType(JSP21Namespace.ATTR_NAME_TRIM_DIRECTIVE_WHITESPACES, ATTR_VALUE_FALSE);
			if (adec != null) {
				adec.usage = CMAttributeDeclaration.OPTIONAL;
				declarations.putNamedItem(JSP21Namespace.ATTR_NAME_TRIM_DIRECTIVE_WHITESPACES, adec);
			}
			// ("deferredSyntaxAllowedAsLiteral" ENUM DECLARED (true|false) "false")
			adec = createBoolType(JSP21Namespace.ATTR_NAME_DEFERRED_SYNTAX_ALLOWED_AS_LITERAL, ATTR_VALUE_FALSE);
			if (adec != null) {
				adec.usage = CMAttributeDeclaration.OPTIONAL;
				declarations.putNamedItem(JSP21Namespace.ATTR_NAME_DEFERRED_SYNTAX_ALLOWED_AS_LITERAL, adec);
			}
		}
	}
	
	protected TACreater20 getAttributeCreater(){
		return new TACreater21();
	}
}
