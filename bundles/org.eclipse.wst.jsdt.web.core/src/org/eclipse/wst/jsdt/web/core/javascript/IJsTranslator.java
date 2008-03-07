/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.javascript;

import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;



/**
 * @author childsb
 *
 */
public interface IJsTranslator extends IDocumentListener{

	public String getJsText();

	public void setBuffer(IBuffer buffer);

	public Position[] getHtmlLocations();

	public int getMissingEndTagRegionStart();

	public Position[] getImportHtmlRanges();

	public String[] getRawImports();

	public void translate();

	public void translateInlineJSNode(IStructuredDocumentRegion container);

	public void translateJSNode(IStructuredDocumentRegion container);

	public void translateScriptImportNode(IStructuredDocumentRegion region);

	public void release();

}