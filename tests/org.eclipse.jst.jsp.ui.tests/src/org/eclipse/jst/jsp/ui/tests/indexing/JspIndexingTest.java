package org.eclipse.jst.jsp.ui.tests.indexing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.tests.util.ProjectUtil;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests thread safety of JSP indexing.
 * Created for: https://bugs.eclipse.org/bugs/show_bug.cgi?id=116523
 * 
 */
public class JspIndexingTest extends TestCase {

	private final String PROJECT_NAME = "PROJECT_116523";

	public void test_116523() {

		createProject();
		
		// try and do some concurrent editing/indexing
		for(int j=0; j<5; j++) {
			for(int i=0; i<100; i++) 
				doTest("test_" + i + ".jsp");
		}
	}
	
	private void doTest(String filePath) {
		
		IFile blankJspFile = getOrCreateFile(PROJECT_NAME + "/" + filePath);
		
		fileBufferInsert(blankJspFile,  "<logic:iterate id=\"iterateID\" type=\"java.util.List\" indexId=\"indexID\"></logic:iterate>\n",0);
		HashMap attrs = new HashMap();
		attrs.put("uri", "struts-logic.tld");
		attrs.put("prefix", "logic");
		domInsertElement(blankJspFile, "jsp:directive.taglib", attrs, 0);
	}
	
	private IFile getOrCreateFile(String filePath) {
		IFile blankJspFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		if (blankJspFile != null && !blankJspFile.exists()) {
			try {
				blankJspFile.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return blankJspFile;
	}

	public void domInsertElement(IFile file, String tagName, HashMap attributes, int insertionPoint) {
		IStructuredModel sModel = null;
		try {
			sModel = StructuredModelManager.getModelManager().getModelForEdit(file);
			if(sModel != null) {
				Document domDoc = (Document)sModel.getAdapter(Document.class );
				IndexedRegion r = sModel.getIndexedRegion(insertionPoint);
				if(r instanceof Node) {
					Element e = domDoc.createElement(tagName);
					Iterator iter = attributes.keySet().iterator();
					// set attributes
					while (iter.hasNext()) {
						String attr = (String) iter.next();
						String val =  (String)attributes.get(attr);
						
						Attr attrNode = domDoc.createAttribute(attr);
						attrNode.setValue(val);
						e.setAttributeNode(attrNode);
					}
					domDoc.insertBefore(e, (Node)r);
					domDoc.insertBefore(domDoc.createTextNode("\n"), (Node)r);
				}
				sModel.save();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		finally {
			if(sModel != null)
				sModel.releaseFromEdit();
		}	
		// System.out.println("inserted element, dom style");
	}
	
	public void fileBufferInsert(IFile file, String text, int insertionPoint) {
		
		// get the file's text buffer
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		IPath path = file.getFullPath();
		try {
			bufferManager.connect(path, null);

			IDocument doc = null;

			ITextFileBuffer buff = bufferManager.getTextFileBuffer(file.getFullPath());
			doc = buff.getDocument();

			boolean saveWhenDone = !buff.isDirty();

			InsertEdit insertEdit = new InsertEdit(insertionPoint, text);
			insertEdit.apply(doc);

			// if the document was not previously dirty, save it
			if (saveWhenDone)
				buff.commit(null, false);
		}
		catch (BadLocationException e) {
			e.printStackTrace();
		}		
		catch (CoreException e) {
			e.printStackTrace();
		}
		finally {
			try {
				bufferManager.disconnect(path, null);
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("inserted text, file buffer style");
	}

//	private void structuredDocInsert(String filePath) {
//		// PROJECT_NAME + "/blank.jsp"
//		IFile blankJspFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
//		if (blankJspFile != null) {
//			try {
//				blankJspFile.create(new ByteArrayInputStream(new byte[0]), true, new NullProgressMonitor());
//			}
//			catch (CoreException e) {
//				e.printStackTrace();
//			}
//		}
//
//		IStructuredModel sModel = null;
//		try {
//			sModel = StructuredModelManager.getModelManager().getModelForEdit(blankJspFile);
//			if (sModel != null) {
//				IStructuredDocument sDoc = sModel.getStructuredDocument();
//
//				String insertText1 = "<logic:iterate id=\"iterateID\" type=\"java.util.List\" indexId=\"indexID\"></logic:iterate>\n";
//				String insertText2 = "<%@taglib uri=\"struts-logic.tld\" prefix=\"logic\" %>\n";
//
//				sDoc.replace(0, 0, insertText1);
//				sDoc.replace(0, 0, insertText2);
//
//				JSPSearchSupport.getInstance().addJspFile(blankJspFile);
//			}
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		catch (CoreException e) {
//			e.printStackTrace();
//		}
//		catch (BadLocationException e) {
//			e.printStackTrace();
//		}
//		finally {
//			if (sModel != null)
//				sModel.releaseFromEdit();
//		}
//	}

	private void createProject() {

		// create project
		String[] natureIds = {ProjectUtil.JAVA_NATURE_ID};
		IProject proj = ProjectUtil.createProject(PROJECT_NAME, null, natureIds);

		// copy files into workspace
		IFile f = ProjectUtil.copyBundleEntryIntoWorkspace("/testfiles/116523/struts.jar", PROJECT_NAME + "/struts.jar");
		assertTrue(f.exists());
		f = ProjectUtil.copyBundleEntryIntoWorkspace("/testfiles/116523/struts-logic.tld", PROJECT_NAME + "/struts-logic.tld");
		assertTrue(f.exists());

		// add struts to classpath
		ProjectUtil.addLibraryEntry(proj, "struts.jar");
	}
}
