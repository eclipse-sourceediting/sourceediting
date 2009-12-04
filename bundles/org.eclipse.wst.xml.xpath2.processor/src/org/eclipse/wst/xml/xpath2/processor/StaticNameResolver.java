/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

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

import java.util.*;

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

	private StaticContext _sc;
	private StaticNameError _err;

	/**
	 * Constructor for static name resolver
	 * 
	 * @param sc
	 *            is the static context.
	 */
	public StaticNameResolver(StaticContext sc) {
		_sc = sc;
		_err = null;
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
			if (!_sc.expand_qname(var))
				report_bad_prefix(var.prefix());

			Expr e = pair.expr();

			e.accept(this);

			_sc.new_scope();
			scopes++;
			_sc.add_variable(var);
		}

		_sc.new_scope();
		scopes++;
		expr.accept(this);

		// kill the scopes
		for (int i = 0; i < scopes; i++)
			_sc.destroy_scope();
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

		if (!_sc.expand_qname(name))
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
		if (!_sc.expand_qname(var))
			report_bad_prefix(var.prefix());

		if (!_sc.variable_in_scope(var))
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

		if (!_sc.expand_function_qname(name))
			report_bad_prefix(name.prefix());

		if (!_sc.function_exists(name, e.arity()))
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
		if (!_sc.expand_elem_type_qname(type))
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
			if (!_sc.expand_elem_type_qname(type))
				report_bad_prefix(type.prefix());

			if (!_sc.type_defined(e.qname()))
				report_error(new StaticTypeNameError("Type not defined: "
						+ e.qname().string()));
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

		if (!_sc.expand_qname(name))
			report_bad_prefix(name.prefix());

		name = e.type();
		if (name == null)
			return null;

		if (!_sc.expand_elem_type_qname(name))
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

		if (!_sc.expand_qname(name))
			report_bad_prefix(name.prefix());

		if (!_sc.attribute_declared(name))
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

		if (!_sc.expand_elem_type_qname(name))
			report_bad_prefix(name.prefix());

		name = e.type();
		if (name == null)
			return null;

		if (!_sc.expand_elem_type_qname(name))
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

		if (!_sc.expand_elem_type_qname(elem))
			report_bad_prefix(elem.prefix());

		if (!_sc.element_declared(elem))
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
