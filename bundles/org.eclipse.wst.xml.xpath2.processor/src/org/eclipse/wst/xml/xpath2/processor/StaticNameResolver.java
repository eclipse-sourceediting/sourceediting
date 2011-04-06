/*******************************************************************************
 * Copyright (c) 2005, 2011 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Steen Moller  - bug 340933 - Migrate to new XPath2 API
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import org.eclipse.wst.xml.xpath2.api.CollationProvider;
import org.eclipse.wst.xml.xpath2.api.EvaluationContext;
import org.eclipse.wst.xml.xpath2.api.Function;
import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.api.StaticContext;
import org.eclipse.wst.xml.xpath2.api.StaticVariableResolver;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticAttrNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticElemNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticFunctNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticNsNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticTypeNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticVarNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

import java.net.URI;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * This class resolves static names.
 */
public class StaticNameResolver implements XPathVisitor, StaticChecker {
	static class DummyError extends Error {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3898564402981741950L;
	}

	private org.eclipse.wst.xml.xpath2.api.StaticContext _sc;
	private StaticNameError _err;

	/**
	 * Constructor for static name resolver
	 * 
	 * @param sc
	 *            is the static context.
	 * @since 2.0
	 */
	public StaticNameResolver(final org.eclipse.wst.xml.xpath2.processor.StaticContext sc) {
		_sc = new org.eclipse.wst.xml.xpath2.api.StaticContext() {

			public boolean isXPath1Compatible() {
				return sc.xpath1_compatible();
			}

			public StaticVariableResolver getInScopeVariables() {
				return new StaticVariableResolver() {
					
					public boolean isVariablePresent(javax.xml.namespace.QName name) {
						return sc.variable_exists(qn(name));
					}

					public TypeDefinition getVariableType(javax.xml.namespace.QName name) {
						return BuiltinTypeLibrary.XS_ANYTYPE;
					}
					
					public short getVariableOccurrence(javax.xml.namespace.QName name) {
						return StaticVariableResolver.ONE;
					}
				};
			}

			private QName qn(javax.xml.namespace.QName name) {
				return new QName(name);
			}

			public TypeDefinition getInitialContextType() {
				return BuiltinTypeLibrary.XS_UNTYPED;
			}

			public Collection getFunctionLibraries() {
				return Collections.emptyList();
			}

			public CollationProvider getCollationProvider() {
				return new CollationProvider() {
					
					public String getDefaultCollation() {
						return null;
					}
					
					public Comparator getCollation(String name) {
						return null;
					}
				};
			}

			public URI getBaseUri() {
				// TODO Auto-generated method stub
				return null;
			}

			public NamespaceContext getNamespaceContext() {
				return new NamespaceContext() {
					
					public Iterator getPrefixes(String arg0) {
						return Collections.emptyList().iterator();
					}
					
					public String getPrefix(String arg0) {
						return "x";
					}
					
					public String getNamespaceURI(String prefix) {
						String ns = sc.resolve_prefix(prefix);
						return ns != null ? ns : XMLConstants.NULL_NS_URI;
					}
				};
			}

			public String getDefaultNamespace() {
				return sc.default_namespace();
			}

			public String getDefaultFunctionNamespace() {
				return sc.default_function_namespace();
			}

			public TypeModel getTypeModel() {
				return sc.getTypeModel(null);
			}

			public Function resolveFunction(javax.xml.namespace.QName name, int arity) {
				// This is fake
				if (sc.function_exists(new QName(name), arity)) {
					return new Function() {
						public String getName() {
							return null;
						}
						public int getMinArity() {
							return 0;
						}
						public int getMaxArity() {
							return 0;
						}
						public boolean isVariableArgument() {
							return false;
						}
						public boolean canMatchArity(int actualArity) {
							return false;
						}
						public TypeDefinition getResultType() {
							return null;
						}
						public TypeDefinition getArgumentType(int index) {
							return null;
						}
						public String getArgumentNameHint(int index) {
							return null;
						}
						public ResultSequence evaluate(Collection args, EvaluationContext evaluationContext) {
							return null;
						}
						public TypeDefinition computeReturnType(Collection args, StaticContext sc) {
							return null;
						} 
					};
				} else {
					return null;
				}
			}

			public TypeDefinition getCollectionType(String collectionName) {
				return BuiltinTypeLibrary.XS_UNTYPED;
			}

			public TypeDefinition getDefaultCollectionType() {
				return BuiltinTypeLibrary.XS_UNTYPED;

			}
			
		};
		_err = null;
	}

	/**
	 * @since 2.0
	 */
	public StaticNameResolver(org.eclipse.wst.xml.xpath2.api.StaticContext context) {
		_sc = context;
		_err = null;
	}

	class VariableScope {
		public VariableScope(QName name, TypeDefinition typeDef, VariableScope nextScope) {
			this.name = name;
			this.typeDef = typeDef;
			this.nextScope = nextScope;
		}
		final public QName name;
		final public TypeDefinition typeDef;
		final public VariableScope nextScope; 
	}	
	
	private VariableScope _innerScope = null;

	private TypeDefinition getVariableType(QName name) {
		VariableScope scope = _innerScope;
		while (scope != null) {
			if (name.equals(scope.name)) return scope.typeDef;
			scope = scope.nextScope;
		}
		return _sc.getInScopeVariables().getVariableType(name.asQName());
	}

	private boolean isVariableInScope(QName name) {
		VariableScope scope = _innerScope;
		while (scope != null) {
			if (name.equals(scope.name)) return true;
			scope = scope.nextScope;
		}
		return _sc.getInScopeVariables().isVariablePresent(name.asQName());
	}
	
	private boolean canResolve(QName var) {
		if (var.prefix() == null) return true;
		return ! XMLConstants.NULL_NS_URI.equals(_sc.getNamespaceContext().getNamespaceURI(var.prefix()));
	}

	private QName resolve(QName var) {
		String ns = _sc.getNamespaceContext().getNamespaceURI(var.prefix());
		QName qName = new QName(var.prefix(), var.local());
		qName.set_namespace(XMLConstants.NULL_NS_URI.equals(ns) ? null : ns);
		return qName;
	}

	private void popScope() {
		if (_innerScope == null) throw new IllegalStateException("Unmatched scope pop");
		_innerScope = _innerScope.nextScope;
	}

	private void pushScope(QName var, BuiltinTypeDefinition xsAnytype) {
		_innerScope = new VariableScope(resolve(var), xsAnytype, _innerScope);		
	}
 
	private boolean expand_qname(QName name, String def) {
		String prefix = name.prefix();

		if ("*".equals(prefix)) {
			name.set_namespace("*");
			return true;
		}

		if (prefix == null) {
			name.set_namespace(def);
			return true;
		}

		String namespaceURI = _sc.getNamespaceContext().getNamespaceURI(prefix);
		if (XMLConstants.NULL_NS_URI.equals(namespaceURI))
			return false;

		name.set_namespace(namespaceURI);
		return true;

	}

	/**
	 * Expands the qname's prefix into a namespace.
	 * 
	 * @param name
	 *            qname to expand.
	 * @return true on success.
	 */
	private boolean expand_qname(QName name) {
		return expand_qname(name, null);
	}

	/**
	 * Expands a qname and uses the default function namespace if unprefixed.
	 * 
	 * @param name
	 *            qname to expand.
	 * @return true on success.
	 */
	private boolean expand_function_qname(QName name) {
		return expand_qname(name, _sc.getDefaultFunctionNamespace());
	}

	/**
	 * Expands a qname and uses the default type/element namespace if
	 * unprefixed.
	 * 
	 * @param name
	 *            qname to expand.
	 * @return true on success.
	 */
	private boolean expand_elem_type_qname(QName name) {
		return expand_qname(name, _sc.getDefaultNamespace());
	}

	
	
	// the problem is that visistor interface does not throw exceptions...
	// so we get around it ;D
	private void report_error(StaticNameError err) {
		_err = err;
		throw new DummyError();
	}

	private void report_bad_prefix(String prefix) {
		report_error(StaticNsNameError.unknown_prefix(prefix));
	}

	/**
	 * Check the XPath node.
	 * 
	 * @param node
	 *            is the XPath node to check.
	 * @throws StaticError
	 *             static error.
	 */
	public void check(XPathNode node) throws StaticError {
		try {
			node.accept(this);
		} catch (DummyError e) {
			throw _err;
		}
	}

	/**
	 * Validate an XPath by visiting all the nodes.
	 * 
	 * @param xp
	 *            is the XPath.
	 * @return null.
	 */
	public Object visit(XPath xp) {
		for (Iterator i = xp.iterator(); i.hasNext();) {
			Expr e = (Expr) i.next();

			e.accept(this);
		}

		return null;
	}

	// does a for and a quantified expression
	// takes the iterator for var expr paris
	private void doForExpr(Iterator iter, Expr expr) {
		int scopes = 0;

		// add variables to scope and check the binding sequence
		while (iter.hasNext()) {
			VarExprPair pair = (VarExprPair) iter.next();

			QName var = pair.varname();
			if (!expand_qname(var))
				report_bad_prefix(var.prefix());

			Expr e = pair.expr();

			e.accept(this);

			pushScope(var, BuiltinTypeLibrary.XS_ANYTYPE);
			scopes++;
		}

		expr.accept(this);

		// kill the scopes
		for (int i = 0; i < scopes; i++)
			popScope();
	}

	/**
	 * Validate a for expression.
	 * 
	 * @param fex
	 *            is the for expression.
	 * @return null.
	 */
	public Object visit(ForExpr fex) {

		doForExpr(fex.iterator(), fex.expr());

		return null;
	}

	/**
	 * Validate a quantified expression.
	 * 
	 * @param qex
	 *            is the quantified expression.
	 * @return null.
	 */
	public Object visit(QuantifiedExpr qex) {
		// lets cheat
		doForExpr(qex.iterator(), qex.expr());

		return null;
	}

	private void printExprs(Iterator i) {
		while (i.hasNext()) {
			Expr e = (Expr) i.next();

			e.accept(this);
		}
	}

	/**
	 * Validate an if expression.
	 * 
	 * @param ifex
	 *            is the if expression.
	 * @return null.
	 */
	public Object visit(IfExpr ifex) {

		printExprs(ifex.iterator());

		ifex.then_clause().accept(this);

		ifex.else_clause().accept(this);

		return null;
	}

	/**
	 * Validate a binary expression by checking its left and right children.
	 * 
	 * @param name
	 *            is the name of the binary expression.
	 * @param e
	 *            is the expression itself.
	 */
	public void printBinExpr(String name, BinExpr e) {
		e.left().accept(this);
		e.right().accept(this);
	}

	/**
	 * Validate an OR expression.
	 * 
	 * @param orex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(OrExpr orex) {
		printBinExpr("OR", orex);
		return null;
	}

	/**
	 * Validate an AND expression.
	 * 
	 * @param andex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(AndExpr andex) {
		printBinExpr("AND", andex);
		return null;
	}

	/**
	 * Validate a comparison expression.
	 * 
	 * @param cmpex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(CmpExpr cmpex) {
		printBinExpr("CMP" + cmpex.type(), cmpex);
		return null;
	}

	/**
	 * Validate a range expression.
	 * 
	 * @param rex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(RangeExpr rex) {
		printBinExpr("RANGE", rex);
		return null;
	}

	/**
	 * Validate an additon expression.
	 * 
	 * @param addex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(AddExpr addex) {
		printBinExpr("ADD", addex);
		return null;
	}

	/**
	 * Validate a subtraction expression.
	 * 
	 * @param subex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(SubExpr subex) {
		printBinExpr("SUB", subex);
		return null;
	}

	/**
	 * Validate a multiplication expression.
	 * 
	 * @param mulex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(MulExpr mulex) {
		printBinExpr("MUL", mulex);
		return null;
	}

	/**
	 * Validate a division expression.
	 * 
	 * @param mulex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(DivExpr mulex) {
		printBinExpr("DIV", mulex);
		return null;
	}

	/**
	 * Validate an integer divison expression.
	 * 
	 * @param mulex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(IDivExpr mulex) {
		printBinExpr("IDIV", mulex);
		return null;
	}

	/**
	 * Validate a mod expression.
	 * 
	 * @param mulex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(ModExpr mulex) {
		printBinExpr("MOD", mulex);
		return null;
	}

	/**
	 * Validate a union expression.
	 * 
	 * @param unex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(UnionExpr unex) {
		printBinExpr("UNION", unex);
		return null;
	}

	/**
	 * Validate a piped expression.
	 * 
	 * @param pipex
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(PipeExpr pipex) {
		printBinExpr("PIPE", pipex);
		return null;
	}

	/**
	 * Validate an intersection expression.
	 * 
	 * @param iexpr
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(IntersectExpr iexpr) {
		printBinExpr("INTERSECT", iexpr);
		return null;
	}

	/**
	 * Validate an except expression.
	 * 
	 * @param eexpr
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(ExceptExpr eexpr) {
		printBinExpr("INT_EXCEPT", eexpr);
		return null;
	}

	/**
	 * Validate an 'instance of' expression.
	 * 
	 * @param ioexp
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(InstOfExpr ioexp) {
		printBinExpr("INSTANCEOF", ioexp);
		return null;
	}

	/**
	 * Validate a 'treat as' expression.
	 * 
	 * @param taexp
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(TreatAsExpr taexp) {
		printBinExpr("TREATAS", taexp);
		return null;
	}

	/**
	 * Validate a castable expression.
	 * 
	 * @param cexp
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(CastableExpr cexp) {
		printBinExpr("CASTABLE", cexp);
		return null;
	}

	/**
	 * Validate a cast expression.
	 * 
	 * @param cexp
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(CastExpr cexp) {
		printBinExpr("CAST", cexp);
		return null;
	}

	/**
	 * Validate a unary expression by checking its one child.
	 * 
	 * @param name
	 *            is the name of the expression.
	 * @param e
	 *            is the expression itself.
	 */
	public void printUnExpr(String name, UnExpr e) {
		e.arg().accept(this);

	}

	/**
	 * Validate a minus expression.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(MinusExpr e) {
		printUnExpr("MINUS", e);
		return null;
	}

	/**
	 * Validate a plus expression.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(PlusExpr e) {
		printUnExpr("PLUS", e);
		return null;
	}

	/**
	 * Validate an xpath expression.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(XPathExpr e) {
		XPathExpr xp = e;

		while (xp != null) {
			StepExpr se = xp.expr();

			if (se != null)
				se.accept(this);

			xp = xp.next();
		}
		return null;
	}

	/**
	 * Validate a forward step.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(ForwardStep e) {
		e.node_test().accept(this);

		return null;
	}

	/**
	 * Validate a reverse step.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(ReverseStep e) {

		NodeTest nt = e.node_test();
		if (nt != null)
			nt.accept(this);

		return null;
	}

	/**
	 * Validate a name test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(NameTest e) {
		QName name = e.name();

		if (!expand_qname(name))
			report_bad_prefix(name.prefix());

		return null;
	}

	/**
	 * Validate a variable reference.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(VarRef e) {
		QName var = e.name();
		
		if (! isVariableInScope(var))
			report_error(new StaticNameError(StaticNameError.NAME_NOT_FOUND));
		
		if (!expand_qname(var))
			report_bad_prefix(var.prefix());

		if (!canResolve(var))
			report_error(new StaticVarNameError("Variable not in scope: "
					+ var.string()));
		return null;
	}

	/**
	 * Validate a string literal.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(StringLiteral e) {
		return null;
	}

	/**
	 * Validate an integer literal.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(IntegerLiteral e) {
		return null;
	}

	/**
	 * Validate a double literal.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(DoubleLiteral e) {
		return null;
	}

	/**
	 * Validate a decimal literal.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(DecimalLiteral e) {
		return null;
	}

	/**
	 * Validate a parenthesized expression.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(ParExpr e) {
		printExprs(e.iterator());
		return null;
	}

	/**
	 * Validate a context item expression.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(CntxItemExpr e) {
		return null;
	}

	/**
	 * Validate a function call.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(FunctionCall e) {
		QName name = e.name();

		if (!expand_function_qname(name))
			report_bad_prefix(name.prefix());

		if (_sc.resolveFunction(name.asQName(), e.arity()) == null)
			report_error(new StaticFunctNameError("Function does not exist: "
					+ name.string() + " arity: " + e.arity()));

		printExprs(e.iterator());
		return null;
	}

	/**
	 * Validate a single type.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(SingleType e) {
		QName type = e.type();
		if (!expand_elem_type_qname(type))
			report_bad_prefix(type.prefix());

		return null;
	}

	/**
	 * Validate a sequence type.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(SequenceType e) {
		ItemType it = e.item_type();

		if (it != null)
			it.accept(this);

		return null;
	}

	/**
	 * Validate an item type.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(ItemType e) {

		switch (e.type()) {
		case ItemType.ITEM:
			break;
		case ItemType.QNAME:
			QName type = e.qname();
			if (!expand_elem_type_qname(type))
				report_bad_prefix(type.prefix());

			if (BuiltinTypeLibrary.BUILTIN_TYPES.lookupType(e.qname().namespace(), e.qname().local()) == null) {
				if (_sc.getTypeModel() == null || _sc.getTypeModel().lookupType(e.qname().namespace(), e.qname().local()) == null)
					report_error(new StaticTypeNameError("Type not defined: "
							+ e.qname().string()));
			}
			break;

		case ItemType.KINDTEST:
			e.kind_test().accept(this);
			break;
		}

		return null;
	}

	/**
	 * Validate an any kind test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(AnyKindTest e) {
		return null;
	}

	/**
	 * Validate a document test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(DocumentTest e) {

		switch (e.type()) {
		case DocumentTest.ELEMENT:
			e.elem_test().accept(this);
			break;

		case DocumentTest.SCHEMA_ELEMENT:
			e.schema_elem_test().accept(this);
			break;
		}
		return null;
	}

	/**
	 * Validate a text test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(TextTest e) {
		return null;
	}

	/**
	 * Validate a comment test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(CommentTest e) {
		return null;
	}

	/**
	 * Validate a processing instructing test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(PITest e) {
		String arg = e.arg();
		if (arg == null)
			arg = "";

		return null;
	}

	/**
	 * Validate an attribute test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	// XXX NO CHECK ?
	public Object visit(AttributeTest e) {
		QName name = e.name();

		if (name == null)
			return null;

		if (!expand_qname(name))
			report_bad_prefix(name.prefix());

		name = e.type();
		if (name == null)
			return null;

		if (!expand_elem_type_qname(name))
			report_bad_prefix(name.prefix());

		return null;
	}

	/**
	 * Validate a schema attribute test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(SchemaAttrTest e) {
		QName name = e.arg();

		if (!expand_qname(name))
			report_bad_prefix(name.prefix());

		if (_sc.getTypeModel().lookupAttributeDeclaration(name.namespace(), name.local()) == null)
			report_error(new StaticAttrNameError("Attribute not decleared: "
					+ name.string()));

		return null;
	}

	/**
	 * Validate an element test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	// XXX NO SEMANTIC CHECK?!
	public Object visit(ElementTest e) {
		QName name = e.name();

		if (name == null)
			return null;

		if (!expand_elem_type_qname(name))
			report_bad_prefix(name.prefix());

		name = e.type();
		if (name == null)
			return null;

		if (!expand_elem_type_qname(name))
			report_bad_prefix(name.prefix());

		return null;
	}

	/**
	 * Validate a schema element test.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(SchemaElemTest e) {
		QName elem = e.name();

		if (!expand_elem_type_qname(elem))
			report_bad_prefix(elem.prefix());

		if (_sc.getTypeModel().lookupElementDeclaration(elem.namespace(), elem.local()) == null)
			report_error(new StaticElemNameError("Element not declared: "
					+ elem.string()));
		return null;
	}

	private void printCollExprs(Iterator i) {
		while (i.hasNext()) {
			Collection exprs = (Collection) i.next();

			printExprs(exprs.iterator());
		}
	}

	/**
	 * Validate an axis step.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(AxisStep e) {

		e.step().accept(this);

		printCollExprs(e.iterator());
		return null;
	}

	/**
	 * Validate a filter expression.
	 * 
	 * @param e
	 *            is the expression.
	 * @return null.
	 */
	public Object visit(FilterExpr e) {
		e.primary().accept(this);

		printCollExprs(e.iterator());
		return null;
	}
}
