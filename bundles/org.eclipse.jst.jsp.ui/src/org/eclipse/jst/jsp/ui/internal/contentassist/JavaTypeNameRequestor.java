package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.TypeNameRequestor;
import org.eclipse.jdt.ui.text.java.CompletionProposalLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.jsp.ui.internal.editor.JSPEditorPluginImageHelper;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.internal.contentassist.IRelevanceConstants;

/**
 * Use w/ JDT search engine to find all type names.
 * Creates proposals from matches reported.
 *
 * @plannedfor 1.0
 */
public class JavaTypeNameRequestor extends TypeNameRequestor {
	
	private int fJSPOffset = -1;
	private int fReplacementLength = -1;
	private List fProposals = new ArrayList();
	private boolean fIgnoreAbstractClasses = false;
	
	private CompletionProposalLabelProvider fLabelProvider = new CompletionProposalLabelProvider();
	
	public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {

        int offset = getJSPOffset();
        int length = getReplacementLength();
		
		// somehow offset or length was set incorrectly
		if(offset == -1 || length == -1)
			return;
		// ignore abstract classes?
		if(ignoreAbstractClasses() && Flags.isAbstract(modifiers))
			return;
		
		Image image = calculateImage(modifiers);
		
        String containerNameString = new String(packageName);
        String typeNameString = String.valueOf(simpleTypeName);
			
        // containername can be null
        String fullName = concatenateName(containerNameString, typeNameString);

        String simpleName = Signature.getSimpleName(fullName);
        StringBuffer buf = new StringBuffer(simpleName);
        String typeQualifier = Signature.getQualifier(fullName);
        if (typeQualifier.length() > 0) {
            buf.append(" - "); //$NON-NLS-1$
            buf.append(typeQualifier);
        }
		
        JavaTypeCompletionProposal proposal = new JavaTypeCompletionProposal(fullName, offset, length, fullName, image, typeNameString, typeQualifier, IRelevanceConstants.R_NONE, true);
        proposal.setTriggerCharacters(JSPProposalCollector.getTypeTriggers());
		fProposals.add(proposal);
	}


	private Image calculateImage(int modifiers) {
		CompletionProposal p = CompletionProposal.create(CompletionProposal.TYPE_REF, getJSPOffset());
		p.setFlags(modifiers);
		
		//https://bugs.eclipse.org/bugs/show_bug.cgi?id=102206
		char[] sig = new char[]{Signature.C_UNRESOLVED};
		p.setSignature(sig);
		
		ImageDescriptor descriptor = fLabelProvider.createImageDescriptor(p);
		Image image = JSPEditorPluginImageHelper.getInstance().getImage(descriptor);
		return image;
	}
	
    /**
     * Concatenates two names. Uses a dot for separation. Both strings can be
     * empty or <code>null</code>.
     */
    public String concatenateName(String name1, String name2) {
        StringBuffer buf = new StringBuffer();
        if (name1 != null && name1.length() > 0) {
            buf.append(name1);
        }
        if (name2 != null && name2.length() > 0) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(name2);
        }
        return buf.toString();
    }
	
	int getJSPOffset() {
		return fJSPOffset;
	}

	void setJSPOffset(int offset) {
		fJSPOffset = offset;
	}

	int getReplacementLength() {
		return fReplacementLength;
	}

	void setReplacementLength(int replacementLength) {
		fReplacementLength = replacementLength;
	}
	void setIgnoreAbstractClasses(boolean ignore) {
		fIgnoreAbstractClasses = ignore;
	}
	boolean ignoreAbstractClasses() {
		return fIgnoreAbstractClasses;
	}
	
	JavaTypeCompletionProposal[] getProposals() {
		return (JavaTypeCompletionProposal[])fProposals.toArray(new JavaTypeCompletionProposal[fProposals.size()]);
	}
}
