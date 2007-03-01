/* Gopyright (c) 2005 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.core.internal.java.jspel;

public class ASTStart extends SimpleNode {
	public ASTStart(int id) {
		super(id);
	}

	public ASTStart(JSPELParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. * */
	@Override
	public Object jjtAccept(JSPELParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
