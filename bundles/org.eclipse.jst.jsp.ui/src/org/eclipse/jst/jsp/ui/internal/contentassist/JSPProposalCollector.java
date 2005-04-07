package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jst.jsp.core.internal.java.JSP2ServletNameUtil;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.swt.graphics.Image;

/**
 * Passed into ICodeComplete#codeComplete(int offset, CompletionRequestor requestor).
 * Adapts IJavaCompletionProposals to JSPCompletion proposals.
 * This includes:
 *  - translating offsets
 *  - "fixing" up display strings
 *  - filtering some unwanted proposals
 *
 * @since 1.0
 */
public class JSPProposalCollector extends CompletionProposalCollector {

	private String fJspName;
	private String fMangledName;
	private JSPTranslation fTranslation;
	
	public JSPProposalCollector(ICompilationUnit cu, JSPTranslation translation) {
		super(cu);
		if(cu != null) {
			// set some names for fixing up mangled name in proposals
			// set mangled (servlet) name
			String cuName = cu.getPath().lastSegment();
			setMangledName(cuName.substring(0, cuName.lastIndexOf('.')));
			// set name of jsp file
			String unmangled = JSP2ServletNameUtil.unmangle(cuName);
			setJspName(unmangled.substring(unmangled.lastIndexOf('/') + 1));
		}
		
		if(translation == null)
			throw new IllegalArgumentException("JSPTranslation cannot be null");
		
		fTranslation = translation;
	}

	/**
	 * Ensures that we only return JSPCompletionProposals.
	 * @return an array of JSPCompletionProposals
	 */
	public JSPCompletionProposal[] getJSPCompletionProposals() {
		List results = new ArrayList();
		IJavaCompletionProposal[] javaProposals = getJavaCompletionProposals();
		// need to filter out non JSPCompletionProposals
		// because their offsets haven't been translated
		for (int i = 0; i < javaProposals.length; i++) {
			if(javaProposals[i] instanceof JSPCompletionProposal)
				results.add(javaProposals[i]);
		}
		return (JSPCompletionProposal[])results.toArray(new JSPCompletionProposal[results.size()]);
	}
	
	/**
	 * Overridden to:
	 *  - translate Java -> JSP offsets
	 *  - fix cursor-position-after
	 *  - fix mangled servlet name in display string
	 *  - remove unwanted proposals (servlet constructor)
	 */
	protected IJavaCompletionProposal createJavaCompletionProposal(CompletionProposal proposal) {
		
		JSPCompletionProposal jspProposal = null;
		
		// from proposal
		String completion = String.valueOf(proposal.getCompletion());
		String mangledName = getMangledName();
		
		// ignore constructor proposals
		// (they will include mangled servlet name)
		if(mangledName != null && completion.indexOf(mangledName) == -1) {
			
			// java offset
			int offset = proposal.getReplaceStart();
			// replacement length
			int length = proposal.getReplaceEnd() - offset;
			// translate offset from Java > JSP
			offset = fTranslation.getJspOffset(offset);
			// cursor position after must be calculated
			int positionAfter = calculatePositionAfter(proposal, completion, offset);
			
			// from java proposal
			IJavaCompletionProposal javaProposal = super.createJavaCompletionProposal(proposal);
			Image image = javaProposal.getImage();
			String displayString = javaProposal.getDisplayString();
			displayString = fixupDisplayString(displayString);
			IContextInformation contextInformation = javaProposal.getContextInformation();
			String additionalInfo = javaProposal.getAdditionalProposalInfo();
			int relevance = javaProposal.getRelevance();
			
			boolean updateLengthOnValidate = true;
			
			jspProposal = new JSPCompletionProposal(completion, offset, length, positionAfter, image, displayString, contextInformation, additionalInfo, relevance, updateLengthOnValidate);
		}
		return jspProposal;
	}

	/**
	 * Cacluates the where the cursor should be after applying this proposal.
	 * eg. method(|) if the method proposal chosen had params.
	 * 
	 * @param proposal
	 * @param completion
	 * @param currentCursorOffset
	 * @return
	 */
	private int calculatePositionAfter(CompletionProposal proposal, String completion, int currentCursorOffset) {
		// calculate cursor position after
		int positionAfter = completion.length();
		int kind = proposal.getKind();
		
		// may need better logic here...
		// put cursor inside parenthesis if there's params
		// only checking for any kind of declaration
		if(kind == CompletionProposal.ANONYMOUS_CLASS_DECLARATION || kind == CompletionProposal.METHOD_DECLARATION || kind == CompletionProposal.POTENTIAL_METHOD_DECLARATION || kind == CompletionProposal.METHOD_REF) {
			String[] params = Signature.getParameterTypes(String.valueOf(proposal.getSignature()));
			if(completion.length() > 0 && params.length > 0)
				positionAfter--;
		}
		return positionAfter;
	}

	/**
	 * Replaces mangled (servlet) name with jsp file name.
	 * 
	 * @param displayString
	 * @return
	 */
	private String fixupDisplayString(String displayString) {
		StringBuffer fixedName = new StringBuffer(displayString);
		String mangledName = getMangledName();
		if(mangledName != null) {
			int index = displayString.indexOf(mangledName);
			if(index != -1) {
				fixedName = new StringBuffer();
				fixedName.append(displayString.substring(0, index));
				fixedName.append(getJspName());
				fixedName.append(displayString.substring(index + mangledName.length()));
			}
		}
		return fixedName.toString();
	}
	
	private String getMangledName() {
		return fMangledName;
	}

	private void setMangledName(String mangledName) {
		fMangledName = mangledName;
	}

	private String getJspName() {
		return fJspName;
	}

	private void setJspName(String jspName) {
		fJspName = jspName;
	}
	
	static char[] getTypeTriggers() {
		return TYPE_TRIGGERS;
	}
}