/*******************************************************************************
 * Copyright (c) 2005, 2010 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *     Bug 154474 EL: 'and', 'or', ... operator
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=154474
 *     Bernhard Huemer <bernhard.huemer@gmail.com>
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java.jspel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.CMDocumentImpl;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDFunction;
import org.eclipse.jst.jsp.core.jspel.ELProblem;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;

public class ELGeneratorVisitor implements JSPELParserVisitor {
	
	private static final String ENDL = "\n"; //$NON-NLS-1$
	
	private static final String fExpressionHeader1 = "public String _elExpression"; //$NON-NLS-1$
	private static final String fExpressionHeader2 = "()" + ENDL + //$NON-NLS-1$
	"\t\tthrows java.io.IOException, javax.servlet.ServletException, javax.servlet.jsp.JspException {" + ENDL + //$NON-NLS-1$
	"javax.servlet.jsp.PageContext pageContext = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map param = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map paramValues = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map header = null;" + ENDL + //$NON-NLS-1$ 
	"java.util.Map headerValues = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map cookie = null;" + ENDL + //$NON-NLS-1$ 
	"java.util.Map initParam = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map pageScope = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map requestScope = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map sessionScope = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map applicationScope = null;" + ENDL + //$NON-NLS-1$
	"return \"\"+( "; //$NON-NLS-1$

	private static final String fExpressionHeader2_param = "()" + ENDL + //$NON-NLS-1$
	"\t\tthrows java.io.IOException, javax.servlet.ServletException, javax.servlet.jsp.JspException {" + ENDL + //$NON-NLS-1$
	"javax.servlet.jsp.PageContext pageContext = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, String> param = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, String[]> paramValues = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, String> header = null;" + ENDL + //$NON-NLS-1$ 
	"java.util.Map<String, String[]> headerValues = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, javax.servlet.http.Cookie> cookie = null;" + ENDL + //$NON-NLS-1$ 
	"java.util.Map<String, String> initParam = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, Object> pageScope = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, Object> requestScope = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, Object> sessionScope = null;" + ENDL + //$NON-NLS-1$
	"java.util.Map<String, Object> applicationScope = null;" + ENDL + //$NON-NLS-1$
	"return \"\"+( "; //$NON-NLS-1$
	
	private static final String fJspImplicitObjects[] = { "pageContext" }; //$NON-NLS-1$
	
	private static final String fJspImplicitMaps[] = { 	"param", "paramValues", "header", "headerValues", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
														"cookie", "initParam", "pageScope", "requestScope", "sessionScope",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
														"applicationScope" }; //$NON-NLS-1$
	
	private static final HashMap fJSPImplicitObjectMap = new HashMap(fJspImplicitObjects.length);
	static {
		for(int i = 0; i < fJspImplicitObjects.length; i++) {
			fJSPImplicitObjectMap.put(fJspImplicitObjects[i], new Boolean(true));
		}
		
		for(int i = 0; i < fJspImplicitMaps.length; i++) {
			fJSPImplicitObjectMap.put(fJspImplicitMaps[i], new Boolean(false));
		}
	}
	
	private static final String fFooter = " );" + ENDL + "}" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$

	private StringBuffer fResult;
	private Map fCodeMap;
	private int fOffsetInUserCode;
	private static int methodCounter = 0;
	private IStructuredDocument fDocument = null;
	private int fContentStart;
	private static Map fOperatorMap;
	// start of the generated function definition, if any:
	private int fGeneratedFunctionStart;

	// this flag lets us know if we were unable to generate for some reason.  One possible reason is that the expression 
	// contains a reference to a variable for which information is only available at runtime.
	private boolean fCanGenerate = true;

	private IStructuredDocumentRegion fCurrentNode;

	private boolean fUseParameterizedTypes;

	private List fELProblems;

	/**
	 * Tranlsation of XML-style operators to java
	 */
	static {
		fOperatorMap = new HashMap();
		fOperatorMap.put("gt", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("lt", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("ge", ">="); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("le", "<="); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("mod", "%"); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("eq", "=="); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("and", "&&"); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("or", "||"); //$NON-NLS-1$ //$NON-NLS-2$
		fOperatorMap.put("not", "!"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * The constructor squirrels away a few things we'll need later
	 * 
	 * @param result
	 * @param codeMap
	 * @param translator
	 * @param jspReferenceRegion
	 * @param contentStart
	 */
	public ELGeneratorVisitor(StringBuffer result, IStructuredDocumentRegion currentNode, Map codeMap, IStructuredDocument document, ITextRegionCollection jspReferenceRegion, int contentStart)
	{
		fResult = result;
		fCodeMap = codeMap;
		fOffsetInUserCode = result.length();
		fContentStart = contentStart;
		fDocument = document;
		fCurrentNode = currentNode;
		fGeneratedFunctionStart = -1; //set when generating function definition
		fUseParameterizedTypes = compilerSupportsParameterizedTypes();
		fELProblems = new ArrayList();
	}

	/**
	 * Append a token to the output stream.  Automatically calculating mapping.
	 * 
	 * @param token
	 */
	private void append(Token token)
	{
		append(token.image, token.beginColumn - 1, token.endColumn);
	}
	
	/**
	 * Append a translation for the corresponding input token.
	 * 
	 * @param translated
	 * @param token
	 */
	private void append(String translated, Token token)
	{
		append(translated, token.beginColumn - 1, token.endColumn);
	}

	/**
	 * Append a string explicitly giving the input mapping.
	 * 
	 * @param newText
	 * @param jspPositionStart
	 * @param jspPositionEnd
	 */
	private void append(String newText, int jspPositionStart, int jspPositionEnd)
	{
		fResult.append(newText);
		Position javaRange = new Position(fOffsetInUserCode, newText.length());
		Position jspRange = new Position(fContentStart + jspPositionStart, jspPositionEnd - jspPositionStart);

		fCodeMap.put(javaRange, jspRange);
		fOffsetInUserCode += newText.length();
	}
	
	/**
	 * Append text that will be unmapped and therefore will not be available for completion.
	 * 
	 * @param newText
	 */
	private void append(String newText)
	{
		fResult.append(newText);
		fOffsetInUserCode += newText.length();
	}
	
	/**
	 * Generate a function invocation.
	 * 
	 * @param fullFunctionName
	 * @return
	 */
	protected String genFunction(String fullFunctionName) {
		TLDCMDocumentManager docMgr = TaglibController.getTLDCMDocumentManager(fDocument);
		int colonIndex = fullFunctionName.indexOf(':');
		String prefix = fullFunctionName.substring(0, colonIndex);
		String functionName = fullFunctionName.substring(colonIndex + 1);
		if (docMgr == null)
			return null;
		
		Iterator taglibs = docMgr.getCMDocumentTrackers(fCurrentNode.getStartOffset()).iterator();
		while (taglibs.hasNext()) {
			TaglibTracker tracker = (TaglibTracker)taglibs.next();
			if(tracker.getPrefix().equals(prefix)) {
				CMDocumentImpl doc = (CMDocumentImpl)tracker.getDocument();
				
				List functions = doc.getFunctions();
				for(Iterator it = functions.iterator(); it.hasNext(); ) {
					TLDFunction function = (TLDFunction)it.next();
					if(function.getName().equals(functionName)) {
						String javaFuncName = getFunctionNameFromSignature(function.getSignature());
						if (javaFuncName == null)
							javaFuncName = functionName;
						return function.getClassName() + "." + javaFuncName; //$NON-NLS-1$
					}
				}
			}
		}
		return null;
	}

	/**
	 * Handle a simple node -- fallback
	 */
	public Object visit(SimpleNode node, Object data) {
		return(node.childrenAccept(this, data));
	}

	static synchronized int getMethodCounter() {
		return methodCounter++;
	}
	
	/**
	 * Handle top-level expression
	 */
	public Object visit(ASTExpression node, Object data) {
		return node.childrenAccept(this, data);
	}

	public void startFunctionDefinition(int start) {
		fGeneratedFunctionStart = fResult.length();
		append(fExpressionHeader1, start, start);
		append(Integer.toString(getMethodCounter()), start, start);
		if (fUseParameterizedTypes)
			append(fExpressionHeader2_param, start, start);
		else
			append(fExpressionHeader2, start, start);
	}

	public void endFunctionDefinition(int end) {
		if (fGeneratedFunctionStart < 0) {
			throw new IllegalStateException("Cannot end function definition because none has been started."); //$NON-NLS-1$
		}
		append(fFooter, end, end);

		// something is preventing good code generation so empty out the result
		// and the map.
		if (!fCanGenerate) {
			fResult.delete(fGeneratedFunctionStart, fResult.length());
			fOffsetInUserCode = fResult.length();
			// remove all fCodeMap entries for the removed code:
			for (Iterator it = fCodeMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Entry) it.next();
				if (entry.getKey() instanceof Position) {
					Position pos = (Position) entry.getKey();
					if (pos.getOffset() >= fGeneratedFunctionStart) {
						it.remove();
					}
				}
			}
		}
		fGeneratedFunctionStart = -1;
	}


	private boolean compilerSupportsParameterizedTypes() {
		if (fDocument != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IPath location = TaglibController.getLocation(fDocument);
			if (location != null && location.segmentCount() > 0) {
				IJavaProject project = JavaCore.create(root.getProject(location.segment(0)));
				String compliance = project.getOption(JavaCore.COMPILER_SOURCE, true);
				try {
					return Float.parseFloat(compliance) >= 1.5;
				}
				catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * Generically generate an operator node.
	 * 
	 * @param node
	 * @param data
	 */
	private void generateOperatorNode(ASTOperatorExpression node, Object data) {
		for(int i = 0; i < node.children.length; i++) {
			node.children[i].jjtAccept(this, data);
			if( node.children.length - i > 1) {
				appendOperator((Token)node.getOperatorTokens().get(i));
			}
		}
	}
	
	/**
	 * Append an operator to the output stream after translation (if any)
	 * 
	 * @param token
	 * @return
	 */
	private String appendOperator(Token token) {
		String tokenImage = token.image.trim();
		String translated = (String)fOperatorMap.get(tokenImage);
		if(null != translated) {
			append(translated, token);
		} else {
			append(token);
		}
		return(translated);
	}

	/**
	 * Handle or Expression
	 */
	public Object visit(ASTOrExpression node, Object data) {
		generateOperatorNode(node, data);
		return(null);
	}


	/**
	 * Handle and expression 
	 */
	public Object visit(ASTAndExpression node, Object data) {
		generateOperatorNode(node, data);
		return(null);
	}


	/**
	 * Handle equality
	 */
	public Object visit(ASTEqualityExpression node, Object data) {
		generateOperatorNode(node, data);
		return(null);
	}


	/**
	 * Handle Relational
	 */
	public Object visit(ASTRelationalExpression node, Object data) {
		generateOperatorNode(node, data);
		return(null);
	}


	/**
	 * Handle addition
	 */
	public Object visit(ASTAddExpression node, Object data) {
		generateOperatorNode(node, data);
		return(null);
	}


	/**
	 * Handle multiply
	 */
	public Object visit(ASTMultiplyExpression node, Object data) {
		generateOperatorNode(node, data);
		return(null);
	}


	/** 
	 * Choice Expression (ternary operator)
	 */
	public Object visit(ASTChoiceExpression node, Object data) {
		node.children[0].jjtAccept(this, data);
		append("?"); //$NON-NLS-1$
		node.children[1].jjtAccept(this, data);
		append(":"); //$NON-NLS-1$
		node.children[2].jjtAccept(this,data);
		return null;
	}


	/**
	 * Handle unary
	 */
	public Object visit(ASTUnaryExpression node, Object data) {
		if(JSPELParserConstants.EMPTY == node.firstToken.kind) {
			append("((null == "); //$NON-NLS-1$
			node.childrenAccept(this, data);
			append(") || ("); //$NON-NLS-1$
			node.childrenAccept(this, data);
			append(").isEmpty())"); //$NON-NLS-1$
		} else if(JSPELParserConstants.NOT1 == node.firstToken.kind || JSPELParserConstants.NOT2 == node.firstToken.kind) {
			append("(!"); //$NON-NLS-1$
			node.childrenAccept(this, data);
			append(")"); //$NON-NLS-1$
		} else if(JSPELParserConstants.MINUS == node.firstToken.kind) {
			append("(-"); //$NON-NLS-1$
			node.childrenAccept(this, data);
			append(")"); //$NON-NLS-1$
		} else {
			node.childrenAccept(this, data);
		}
		return null;
	}


	/**
	 * Value node
	 */
	public Object visit(ASTValue node, Object data) {
		if(node.jjtGetNumChildren() >= 2) {
			if(node.jjtGetChild(0) instanceof ASTValuePrefix && node.jjtGetChild(1) instanceof ASTValueSuffix) {
				ASTValuePrefix prefix = (ASTValuePrefix) node.jjtGetChild(0);
				ASTValueSuffix suffix = (ASTValueSuffix) node.jjtGetChild(1);
				//content assist can cause a null pointer here without the extra null check
				if(prefix.firstToken.image.equals("pageContext") && suffix.getPropertyNameToken() != null && suffix.getPropertyNameToken().image.equals("request")) {
					append("((HttpServletRequest)");
				}
			}
		}
		return node.childrenAccept(this, data);	
	}


	/**
	 * Value Prefix
	 */
	public Object visit(ASTValuePrefix node, Object data) {
		// this is a raw identifier.  May sure it's an implicit object.
		// This is the primary place where modification is needed to 
		// support JSF backing beans.
		if(null == node.children) {
			if(isCompletingObject(node.firstToken.image)) {
				append(node.firstToken);
			} else {
				fCanGenerate = false;
			}
			return(null);
		}
		return node.childrenAccept(this, data);
	}


	/**
	 * Function for testing implicit objects.
	 * 
	 * @param image
	 * @return
	 */
	private boolean isCompletingObject(String image) {
		Boolean value = (Boolean)fJSPImplicitObjectMap.get(image);
		return null == value ? false : value.booleanValue();
	}

	/**
	 * Value suffix
	 */
	public Object visit(ASTValueSuffix node, Object data) {
		if(JSPELParserConstants.LBRACKET == node.firstToken.kind) {
			fCanGenerate = false;
		} else if(null != node.getPropertyNameToken()) {
			Token suffix = node.getPropertyNameToken();
			String ucaseName = suffix.image.substring(0, 1).toUpperCase() + suffix.image.substring(1, suffix.image.length()); 

			// This is a special case.  Note that the type system, no matter how much type information
			// we would have wouldn't give us the correct result.  We're looking for "pageContext.request" 
			// here and will add a downcast to (HTTPServletRequest)
			
			append(node.firstToken);
			append("get" + ucaseName + "()", suffix); //$NON-NLS-1$ //$NON-NLS-2$
			
			SimpleNode parent = (SimpleNode) node.jjtGetParent();
			if(suffix.image.equals("request") && parent instanceof ASTValue && //$NON-NLS-1$
					parent.jjtGetParent() instanceof ASTUnaryExpression && parent.firstToken.image.equals("pageContext")) { //$NON-NLS-1$
				append(")");
			} 

		} else if(node.getLastToken().image.equals(".") && node.getLastToken().next.image.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			//this allows for content assist in the case of something along the lines of "pageContext." and then ctl-space
			append(node.firstToken);
			append("get()", node.getLastToken().beginColumn, node.getLastToken().beginColumn); //$NON-NLS-1$
		} else {
			append(node.firstToken);
		}
		return null;
	}


	/**
	 * Function invocation
	 */
	public Object visit(ASTFunctionInvocation node, Object data) {
		String functionTranslation = genFunction(node.getFullFunctionName());
		if(null != functionTranslation) {

			//find the token representing the function name
			Token jspFuncNameToken = getJSPFuncNameToken(node);
			
			/* if there is a dot in the function name then separate out the class path
			 * from the function name and append.
			 * else just append
			 * in both cases use the jsp function name token as the mapped token
			 */
			int indexOfDot = functionTranslation.lastIndexOf('.');
			if(indexOfDot != -1) {
				String funcClass = functionTranslation.substring(0,indexOfDot+1);
				String funcName = functionTranslation.substring(indexOfDot+1);
				append(funcClass, jspFuncNameToken);
				append(funcName, jspFuncNameToken);
			} else {
				append(functionTranslation, jspFuncNameToken);
			}
			
			//append any parameters
			append("(");
			if(node.children != null) {
				for(int i = 0; i < node.children.length; i++) {
					node.children[i].jjtAccept(this, data);
					if( node.children.length - i > 1){
						append(","); //$NON-NLS-1$
					}
				}
			}
			append(")"); //$NON-NLS-1$
		}
		else {
			//column offsets are 1 based not 0 based, thus subtract one
			final int problemOffset = fContentStart + node.getFirstToken().beginColumn - 1;
			final int problemLength = node.getLastToken().endColumn - 1;

			//could not find function translation so report error
			fELProblems.add(new ELProblem(new Position(problemOffset, problemLength), NLS.bind(JSPCoreMessages.JSPELTranslator_0, node.getFullFunctionName())));
			
			//error message to be injected into translation purely for debugging purposes
			String errorMsg = "\"Could not find function translation for: " + node.getFullFunctionName() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			append(errorMsg);
 		}
		return null;
	}

	/**
	 * @return the {@link ELProblem}s found by this visitor
	 */
	public List getELProblems() {
		return fELProblems;
	}

	/**
	 * Literal
	 */
	public Object visit(ASTLiteral node, Object data) {
		if (isSingleQuotedStringLiteral(node)) {
			//replace the single quotes with double quotes quotes
			//so java compiler will be happy
			//(see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=104943)
			String image = node.firstToken.image;
			image = "\"" + image.substring(1, image.length()-1) + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			node.firstToken.image = image;
		}
		
		append(node.firstToken);
		return null;
	}
	
	/**
	 * Indicates whether the given ASTLiteral is a single quoted string literal,
	 * As opposed to a double quoted ASTLiteral
	 * 
	 * @param node the ASTLiteral to check to see if it is single quoted
	 * 
	 * @return true, if the given token is a single quoted string literal,
	 * false otherwise
	 */
	private static boolean isSingleQuotedStringLiteral(ASTLiteral node) {
		String content = node.firstToken.image;
		return content.length() > 1 && content.startsWith("'") && content.endsWith("'"); //$NON-NLS-1$ // $NON-NLS-2$
	}

	/**
	 * <p>Given a method signature parse out the method name and return it.
	 * The method name in the signature is found by finding a word with
	 * whitespace before it and a '<code>(</code>' after it.</p>
	 * 
	 * @param methodSignature the signature of the method to get the method name out of.
	 * @return the method name from the given signature, or <code>null</code> if it
	 * can not be found.
	 */
	private static String getFunctionNameFromSignature (String methodSignature) {
		int length = methodSignature.length();
		char c = 0;
		int identifierStart = -1;
		int whitespaceStart = -1;
		// keep track of the index of the last identifier before the (
		for (int i = 0; i < length; i++) {
			c = methodSignature.charAt(i);
			if (Character.isJavaIdentifierPart(c) && whitespaceStart >= identifierStart)
				identifierStart = i;
			else if (Character.isWhitespace(c))
				whitespaceStart = i;
			else if (c == '(') {
				if (identifierStart >= 0) {
					return methodSignature.substring(identifierStart, i).trim();
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the {@link Token} the represents the function name in
	 * the {@link ASTFunctionInvocation}. This is designated as the
	 * first token after the {@link Token} whose image is ":".
	 * If such a token can not be found then the first token of the
	 * {@link ASTFunctionInvocation} is returned.
	 * 
	 * @param funcInvo the {@link ASTFunctionInvocation} to find the function name {@link Token} in
	 * @return the {@link Token} in the given {@link ASTFunctionInvocation} that represents the
	 * function name, or if that can't be found the first {@link Token} in the {@link ASTFunctionInvocation}.
	 */
	private Token getJSPFuncNameToken(ASTFunctionInvocation funcInvo) {
		Token funcNameToken = funcInvo.getFirstToken();
		
		Token temp = funcInvo.getFirstToken();
		do {
			if(temp.image.equals(":")) {
				funcNameToken = temp.next;
			}
		} while(temp.next != null && funcNameToken == null);
			
		
		return funcNameToken;
	}
}
