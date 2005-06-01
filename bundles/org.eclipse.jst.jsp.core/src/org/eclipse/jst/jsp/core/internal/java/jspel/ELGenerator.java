/**
 * B E A   S Y S T E M S
 *
 * Copyright (c) 2004  BEA Systems, Inc.
 *
 * All Rights Reserved. Unpublished rights reserved under the copyright laws
 * of the United States. The software contained on this media is proprietary
 * to and embodies the confidential technology of BEA Systems, Inc. The
 * possession or receipt of this information does not convey any right to
 * disclose its contents, reproduce it, or use,  or license the use, for
 * manufacture or sale, the information or anything described therein. Any
 * use, disclosure, or reproduction without BEA System's prior written
 * permission is strictly prohibited.
 *
 */

package org.eclipse.jst.jsp.core.internal.java.jspel;

import java.util.Map;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;

/**
 * The code checker for the jsf/jsp EL.
 */
public final class ELGenerator {
	/**
     * Constructor.
     *
     * @param locator A valid ELLocator instance, may not be null.
     */
    public ELGenerator() {
    } // constructor
	

    /**
     * Check the netuiel AST and set diagnostics as necessary.
     * @param translator TODO
     * @param jspReferenceRegion TODO
     * @param contentStart 
     * @param contentLength 
     */
    public void generate(ASTExpression root, StringBuffer result, Map codeMap, JSPTranslator translator, ITextRegionCollection jspReferenceRegion, int contentStart, int contentLength) {
		ELGeneratorVisitor visitor = new ELGeneratorVisitor(result, codeMap, translator, jspReferenceRegion, contentStart);
		root.jjtAccept(visitor, null);
    }
}
