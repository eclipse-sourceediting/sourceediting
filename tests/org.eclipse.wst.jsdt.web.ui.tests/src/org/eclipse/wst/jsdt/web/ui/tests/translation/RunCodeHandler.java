/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.web.ui.tests.translation;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.wst.jsdt.core.IBuffer;
import org.eclipse.wst.jsdt.core.IFunction;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ASTParser;
import org.eclipse.wst.jsdt.core.dom.ASTVisitor;
import org.eclipse.wst.jsdt.core.dom.Assignment;
import org.eclipse.wst.jsdt.core.dom.Block;
import org.eclipse.wst.jsdt.core.dom.ExpressionStatement;
import org.eclipse.wst.jsdt.core.dom.FunctionInvocation;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.ObjectLiteral;
import org.eclipse.wst.jsdt.core.dom.ObjectLiteralField;
import org.eclipse.wst.jsdt.core.dom.QualifiedName;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;
import org.eclipse.wst.jsdt.internal.core.DocumentAdapter;
import org.eclipse.wst.jsdt.internal.corext.ValidateEditException;
import org.eclipse.wst.jsdt.internal.corext.util.JavaModelUtil;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.JavaEditor;


/**
 * Connected handler for whatever we need it to be
 * 
 * Invoke with M1+M2+7
 * 
 * @author nitin
 */
public class RunCodeHandler extends AbstractHandler {

	/**
	 * 
	 */
	public RunCodeHandler() {
	}


	static final class MyNodeVisitor extends ASTVisitor{
		IJavaScriptUnit jsUnit;
		String className;
		private JavaScriptUnit cu;
		
		public MyNodeVisitor(IJavaScriptUnit jsUnit, String className, ASTNode ast) {
			super();
			this.jsUnit = jsUnit;
			this.className = className;
			this.cu = (JavaScriptUnit) ast;
		}

		public boolean visit(org.eclipse.wst.jsdt.core.dom.FunctionDeclaration node) {
//			this.jsUnit = (IJavaScriptUnit) node.getRoot();
			Block body = node.getBody();
			try {
				IJavaScriptElement element = jsUnit.getElementAt(body.getStartPosition());
				if (element instanceof IFunction) {
					IFunction function = (IFunction) element;
					if (!function.isConstructor() && function.getDisplayName().equals("sayHello")) {
						IType type = function.getDeclaringType();
						if (type != null /*&& type.getDisplayName().equals(className)*/) {
							cu.recordModifications();
							AST ast = node.getAST();
							
//							String newField = "dojo.declare(\"temp\", null,{handler : function(){ }});";
							String functionLiteral = "handler : function(args){alert(\"Hello\");}";
							ObjectLiteralField field = getObjectLiteralField(functionLiteral);
							
							//creating the function
//							AST parentAST = node.getParent().getAST();
//							FunctionDeclaration decl = parentAST.newFunctionDeclaration();
//							decl.setName(parentAST.newSimpleName("handler"));
//							Block block = parentAST.newBlock();
////							block.statements().add(parentAST.newExpressionStatement(parentAST.newBlockComment()));
//							decl.setBody(block);
							List fields = ((ObjectLiteral)(node.getParent().getParent().getParent())).fields();
							
							fields.add(field);
//							node.getParent().getBody().statements().add(decl);
							
//							SourceType sty ;
//							sty.createMethod(contents, sibling, force, monitor)
//							ExpressionStatement st = parentAST.newExpressionStatement(decl);
							
							

							FunctionInvocation methodInvocation = ast.newFunctionInvocation();
							QualifiedName name = ast.newQualifiedName(ast.newSimpleName("dojo"),ast.newSimpleName("connect"));//$NON-NLS-1$
							methodInvocation.setExpression(name);
							methodInvocation.arguments().add(ast.newSimpleName("a"));
							StringLiteral lit = ast.newStringLiteral();
							lit.setLiteralValue("a");
							methodInvocation.arguments().add(lit);
							ExpressionStatement expressionStatement = ast.newExpressionStatement(methodInvocation);
							body.statements().add(expressionStatement);
							
							TextEdit edits= cu.rewrite(getDocument(jsUnit), JavaScriptCore.getDefaultOptions());							
							try {
								JavaModelUtil.applyEdit(jsUnit, edits, true, new NullProgressMonitor());
							} catch (ValidateEditException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
//							jsUnit.becomeWorkingCopy(null);

							// get source and make changes to the body directly in the buffer
//							String source = jsUnit.getBuffer().getText(body.getStartPosition(), body.getLength());
//							String toInsert = "\tdojo.connect(a ,b , c, d);\n";
//							int offset = body.getStartPosition()+ body.getLength()-1;
//							IBuffer buffer = jsUnit.getBuffer();
//							buffer.replace(offset, 0, toInsert);
									
							// OR build the syntax up using AST manipulation
							
//							
							
//							ImportDeclaration id = nodeAst.newImportDeclaration();
//							 id.setName(nodeAst.newName(new String[] {"java", "util", "Set"}));
//							 
//									FunctionInvocation replacement= nodeAst.newFunctionInvocation();
//									
//							 
//							 
//							 ASTRewrite rewriter = ASTRewrite.create(nodeAst);
//							 
//							 replacement.setName((SimpleName) rewriter.createCopyTarget(function.getName()));
//								replacement.arguments().add(nodeAst.newThisExpression());
//								replacement.setExpression((Expression) rewriter.createCopyTarget(right));
//								rewriter.replace(method, replacement, null);
//							 
//							 
//							 TypeDeclaration td = (TypeDeclaration) cu.types().get(0);
//							 ITrackedNodePosition tdLocation = rewriter.track(td);
//							 ListRewrite lrw = rewriter.getListRewrite(cu, JavaScriptUnit.TYPES_PROPERTY);
//							 lrw.insertLast(id, null);
//							 IDocument doc = getDocument(jsUnit);
//							 TextEdit edits = rewriter.rewriteAST(doc, null);
//							 try {
//								UndoEdit undo = edits.apply(doc);
//							} catch (MalformedTreeException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							} catch (BadLocationException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//							

//							jsUnit.commitWorkingCopy(true, new NullProgressMonitor());
//							jsUnit.discardWorkingCopy();
						}
					}
				}
			}
			catch (JavaScriptModelException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected void doExecute(IFile jsFile) {
		IProject project = jsFile.getProject();
//		String namespace = CodeGenUtil.getNameSpace(project);
//		final String className = IPCCommandUtil.getPortletHelperClassName(jsFile, namespace);
		final IJavaScriptUnit jsUnit = (IJavaScriptUnit) JavaScriptCore.create(jsFile);
		
		if (jsUnit == null)
			return;
		IProgressMonitor monitor = null;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		
		////////////////////////////////////////////////////////////////////////////////
		
		// this part may not be of interest for what you're trying to do
//		IType t = null;
//		try{
//			for (org.eclipse.wst.jsdt.core.IFunction function : jsUnit.getFunctions()) {
//				// functions declared in the file outside of any types
//				org.eclipse.wst.jsdt.core.ISourceRange sourceRange = function.getSourceRange();
//				String source = jsUnit.getBuffer().getText(sourceRange.getOffset(), sourceRange.getLength());
//			}
//	
//			// start with the types in the file
//			IType[] types = jsUnit.getTypes();
//			
//			for (IType type: types) {
////				if (type.getDisplayName().equals(className)) {
//					t = type;
//					for (IFunction function : type.getFunctions()) {
//						// functions declared in a type
//						if(function.getDisplayName().equals("sayHello") && !function.isConstructor()) {
//							ISourceRange sourceRange = function.getSourceRange();
//							String source = jsUnit.getBuffer().getText(sourceRange.getOffset(), sourceRange.getLength());
//						}
//					}
////				}
//			}
//		}catch (JavaScriptModelException e) {
//			// TODO: handle exception
//			//;
//		}

		//It would be more accurate to walk the AST of the file instead of searching just the top-level children:
//		SourceType st = null;
//		if(t instanceof SourceType)
//			st = (SourceType) t;
//		try {
//			if(st!=null)
//				st.createMethod("function hello(args){}", null, false, null);
//			
//		} catch (JavaScriptModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		ASTParser parser = org.eclipse.wst.jsdt.core.dom.ASTParser.newParser(AST.JLS3);
		parser.setSource(jsUnit);
		ASTNode ast = parser.createAST(null);
		ASTVisitor visitor = new MyNodeVisitor(jsUnit,/*className*/null , ast);			
		ast.accept(visitor);
		
		
	}
		
		
		
		
		
		
		
		
		
		
		
		////////////////////////////////////////////////////////////////////////////////
		
		
		
		
		
		
		
		
		
		
		
		

//		
//		try {
//			// TODO: check for dirty state first, and save all the opened documents
//			//then do the operation
//			
//			jsUnit.becomeWorkingCopy(monitor);
//			IJavaScriptElement[] children = jsUnit.getChildren();
//			for (IJavaScriptElement javaScriptElement : children) {
//				if(javaScriptElement instanceof SourceType){
//					SourceType st = (SourceType) javaScriptElement;
//					if(st.isClass()){
//						if(st.getDisplayName().equals(className)){
//							SourceTypeElementInfo info = (SourceTypeElementInfo) st.getElementInfo();
//							IJavaScriptElement[] infoChildren =info.getChildren();
//							boolean found = false;
//							SourceMethod method = null;
//							for (IJavaScriptElement javaScriptElement2 : infoChildren) {
//								if(javaScriptElement2 instanceof SourceMethod){
//									method = (SourceMethod) javaScriptElement2;
//									if(method.getDisplayName().equals("sayHello")){
//										found = true;
//										break;
//									}
//								}
//							}
//							if(found){
//								
//								IPath filePath = jsFile.getFullPath();
//								String toInsert = "dojo.connect(a ,b , c, d)";
//								int offset = method.getSourceRange().getOffset();
//								IBuffer source = jsUnit.getBuffer();
////								source.replace(offset+1, 0, toInsert);
////								st.createMethod("hello: function(){ var x=5 }", null, true, monitor);
//								//method.
//								IDocument document = getDocument(jsUnit);
//								CompilationUnitChange result = new CompilationUnitChange("Insert dojo connect", jsUnit);
//								AST cu;
//								if(jsUnit instanceof JavaScriptUnit){
//									cu =((JavaScriptUnit)jsUnit).getAST();
//									ASTRewrite rewriter = ASTRewrite.create(cu);
//									
//								}else {
//									ASTParser parser = ASTParser.newParser(AST.JLS3);
//									 parser.setSource(document.get().toCharArray());
//									 JavaScriptUnit cu = (JavaScriptUnit) parser.createAST(null);
//									 cu = cu.getAST();
//									 cu.getBodyChild();
//									 List<Object> stmts = cu.statements();
//									 ExpressionStatement stmt = (ExpressionStatement) stmts.get(2);
////									 stmt.get
//									 for (Object object : stmts) {
//										object = object;
//										((ExpressionStatement)object).getAST();
//										//ASTNodes.getBodyDeclarations((ExpressionStatement)object);
//										FunctionInvocation fi = (FunctionInvocation) (ASTNodes.getChildren((ExpressionStatement)object)).get(0);
//										fi.getBodyChild();
////										fi.
//									}
//									 
//									 FunctionInvocation invocation = cu.newFunctionInvocation();
//								}
//									
//								
//								
//								//testing only...
////								IJavaScriptElement[] elements = method.getChildren();
////								String source = method.getSource();
////								Object info1 = method.getElementInfo();
////								IJavaScriptModel model = method.getJavaScriptModel();
//////								model = model;
////								st.createMethod("myfun : function(){}", method, true, monitor);
//							}
////							info = info;
//						}
//						
//					}
//				}
//			}
////			String toInsert = "dojo.connect(a ,b , c, d)";
////			int offset = 50;
////			IBuffer source = jsUnit.getBuffer();
////			source.replace(offset+1, 0, toInsert);
////			IBuffer source = jsUnit.getBuffer();
////			jsUnit.getBuffer().append("\n" + "var x;");
//			jsUnit.commitWorkingCopy(true, monitor);
//			jsUnit.discardWorkingCopy();
//		} catch (JavaScriptModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	
	
	protected static IDocument getDocument(IJavaScriptUnit cu) throws JavaScriptModelException {
		IBuffer buffer = cu.getBuffer();
		if (buffer instanceof IDocument)
			return (IDocument) buffer;
		return new DocumentAdapter(buffer);
	}
	
	static ObjectLiteralField getObjectLiteralField(String source){
		String js = "x.prototype = {" + source + "}";
		JavaScriptUnit jsu = getAST(js);
		ExpressionStatement stmt = (ExpressionStatement)jsu.statements().get(0);
		ObjectLiteral lit = (ObjectLiteral)((Assignment) stmt.getExpression()).getRightHandSide();		
		ObjectLiteralField field = (ObjectLiteralField) lit.fields().get(0);
		ASTNode node = ASTNode.copySubtree(jsu.getAST(), field);
		return (ObjectLiteralField) node;
	}
	
	static JavaScriptUnit getAST(String source){
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		ASTNode ast = parser.createAST(null);
		return (JavaScriptUnit) ast;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		if (!(activeEditor instanceof JavaEditor)) {
			return null;
		}
		doExecute((IFile) HandlerUtil.getActiveEditor(event).getEditorInput().getAdapter(IFile.class));
		return null;
	}
}
