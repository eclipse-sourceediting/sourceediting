package org.eclipse.wst.sse.core.internal.document;

import org.eclipse.jface.text.DocumentEvent;

/**
 * Listener for document events that should have the document's state
 * validated first.
 */
public interface IDocumentStateValidationListener {

	/**
	 * Validates the document's state.
	 * @param event the document event that triggered document state validation
	 * @return true if the document is in a state valid of being edited; false otherwise.
	 */
	public boolean validateDocumentState(DocumentEvent event);
}
