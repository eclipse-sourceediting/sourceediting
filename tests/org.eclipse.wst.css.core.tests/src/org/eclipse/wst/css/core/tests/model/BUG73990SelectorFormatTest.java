package org.eclipse.wst.css.core.tests.model;

import java.io.IOException;

import org.eclipse.wst.css.core.document.ICSSModel;
import org.eclipse.wst.css.core.format.FormatProcessorCSS;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.eclipse.wst.sse.core.text.IStructuredDocument;

public class BUG73990SelectorFormatTest extends AbstractModelTest {
	public void testInsertText1() throws IOException {
		ICSSModel model = getModel();
		IStructuredDocument structuredDocument = model.getStructuredDocument();
		structuredDocument.set(FileUtil.createString(
				"src/org/eclipse/wst/css/core/tests/testfiles",
				"BUG73990_selector_unformatted.css"));

		(new FormatProcessorCSS()).formatModel(model);

		String expected = FileUtil.createString(
				"src/org/eclipse/wst/css/core/tests/testfiles/results",
				"BUG73990_selector_formatted.css");
		// Note: FileUtil.createString() added a commonEOL to the expected
		// string. We have to concat a commonEOL to the actual string.
		String actual = structuredDocument.get().concat(FileUtil.commonEOL);
		assertEquals(expected, actual);
	}
}
