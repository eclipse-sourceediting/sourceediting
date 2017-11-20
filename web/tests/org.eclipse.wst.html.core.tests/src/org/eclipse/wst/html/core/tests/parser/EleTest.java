package org.eclipse.wst.html.core.tests.parser;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EleTest extends ModelTest {

	public void testModel() {
		IDOMModel model = createHTMLModel();
		try {
			IStructuredDocument structuredDocument = model.getStructuredDocument();
			Document document = model.getDocument();

			structuredDocument.replaceText(null, 0, 0, "<html><body><DL>\n\n<DD>Add a \"set top\" rule for the specified parameters1.\n<DD>Add a \"set top\" rule for the specified parameters2.\n<DD>Add a \"set top\" rule for the specified parameters3.\n<DD>Add a \"set top\" rule for the specified parameters4.\n<DD>Add a \"set top\" rule for the specified parameters5.\n<DD>Add a \"set top\" rule for the specified parameters6.\n<DD>Add a \"set top\" rule for the specified parameters7.\n<DD>Add a \"set top\" rule for the specified parameters8.\n<DD>Add a \"set top\" rule for the specified parameters9.\n<DD>Add a \"set top\" rule for the specified parameters10.\n<DD>Add a \"set top\" rule for the specified parameters11.\n<DD>Add a \"set top\" rule for the specified parameters12.\n<DD>Add a \"set top\" rule for the specified parameters.\n<DD>Add a \"set top\" rule for the specified parameters.\n<DD>Add a \"set top\" rule for the specified parameters.\n<DD>Add a \"set top\" rule for the specified parameters.\n<DD>Add a \"set top\" rule for the specified parameters.\n<DD>Add a \"set top\" rule for the specified parameters.\n<DD>Add a \"set top\" rule for the specified parameters.\n<DD>Add a \"set top\" rule for the specified parameters.\n</DL></body></html>");
			Node a = document.getElementsByTagName("DL").item(0);
			System.out.println(((Element) a).getElementsByTagName("DD").getLength());
		}
		finally {
			model.releaseFromEdit();
		}
	}

}
