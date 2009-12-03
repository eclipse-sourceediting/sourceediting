/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 274805 - improvements to xs:integer data type 
 *     Jesper Steen Moeller - bug 285145 - check arguments to op:to
 *     Jesper Steen Moeller - bug 262765 - fixed node state iteration
 *     Jesper Steen Moller  - bug 275610 - Avoid big time and memory overhead for externals
 *     Jesper Steen Moller  - bug 280555 - Add pluggable collation support
 *     Jesper Steen Moller  - bug 281938 - undefined context should raise error
 *     Jesper Steen Moller  - bug 262765 - use correct 'effective boolean value'
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import java.math.BigInteger;
import java.util.*;
import org.w3c.dom.*;
import org.apache.xerces.xs.*;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.Axis;
import org.eclipse.wst.xml.xpath2.processor.internal.DescendantOrSelfAxis;
import org.eclipse.wst.xml.xpath2.processor.internal.Focus;
import org.eclipse.wst.xml.xpath2.processor.internal.ForwardAxis;
import org.eclipse.wst.xml.xpath2.processor.internal.ParentAxis;
import org.eclipse.wst.xml.xpath2.processor.internal.ReverseAxis;
import org.eclipse.wst.xml.xpath2.processor.internal.SelfAxis;
import org.eclipse.wst.xml.xpath2.processor.internal.SeqType;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.StaticTypeNameError;
import org.eclipse.wst.xml.xpath2.processor.internal.TypeError;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.*;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

/**
 * Default evaluator interface
 */
public class DefaultEvaluator implements XPathVisitor, Evaluator {

	private class DummyError extends Error {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3161644790881405403L;
		// 0: dynamic error
		// 1: type error
		private int _type;

		public DummyError(int type) {
			_type = type;
		}

		public int type() {
			return _type;
		}
	}

	private DynamicContext _dc;
	private XPathException _err;

	// stuff anyone may use
	private Collection _g_coll;
	private XSInteger _g_xsint;

	// this is a parameter that may be set on a call...
	// the parameter may become invalid on the next call... i.e. the
	// previous parameter is not saved... so use with care! [remember...
	// this thing is highly recursive]
	private Object _param;

	private class Pair {
		public Object _one;
		public Object _two;

		public Pair(Object o, Object t) {
			_one = o;
			_two = t;
		}
	}

	/**
	 * set parameters
	 * 
	 * @param dc
	 *            is the dynamic context.
	 * @param doc
	 *            is the document.
	 */
	public DefaultEvaluator(DynamicContext dc, Document doc) {
		_dc = dc;
		_err = null;

		// initialize context item with root of document
		ResultSequence rs = ResultSequenceFactory.create_new();
		if (doc != null) rs.add(new DocType(doc));

		_dc.set_focus(new Focus(rs));

		_param = null;

		_g_coll = new ArrayList();
		_g_xsint = new XSInteger();
	}

	// XXX this kinda sux
	// the problem is that visistor interface does not throw exceptions...
	// so we get around it ;D
	private void report_error(DynamicError err) {
		_err = err;
		throw new DummyError(0);
	}

	private void report_error(TypeError err) {
		_err = err;
		throw new DummyError(1);
	}

	private void report_error(StaticNameError err) {
		throw new DummyError(666);
	}

	/**
	 * evaluate the xpath node
	 * 
	 * @param node
	 *            is the xpath node.
	 * @throws dynamic
	 *             error.
	 * @return result sequence.
	 */
	public ResultSequence evaluate(XPathNode node) throws DynamicError {
		try {
			return (ResultSequence) node.accept(this);
		} catch (DummyError e) {
			switch (e.type()) {
			case 0:
				throw (DynamicError) _err;
			case 1:
				throw new DynamicError((TypeError) _err);

			default:
				assert false;
			}
		}

		return null; // unreach

	}

	// basically the comma operator...
	private ResultSequence do_expr(Iterator i) {

		ResultSequence rs = null;

		while (i.hasNext()) {
			Expr e = (Expr) i.next();

			ResultSequence result = (ResultSequence) e.accept(this);

			if (rs == null)
				rs = result;
			else
				rs.concat(result);
		}

		if (rs == null)
			rs = ResultSequenceFactory.create_new();
		return rs;
	}

	/**
	 * iterate through xpath expression
	 * 
	 * @param xp
	 *            is the xpath.
	 * @return result sequence.
	 */
	public Object visit(XPath xp) {
		ResultSequence rs = do_expr(xp.iterator());

		return rs;
	}

	// XXX ugly
	// type: 0 = for [return == "correct"]
	// 1 = for all [return false, return empty on true]
	// 2 = there exists [return true, return empty on false]
	private ResultSequence do_for_quantified_expr(ListIterator iter,
			Expr finalexpr, int type) {

		// we have more vars to bind...
		if (iter.hasNext()) {
			boolean allocated_var = false;
			ResultSequence result = ResultSequenceFactory.create_new();
			VarExprPair ve = (VarExprPair) iter.next();

			// evaluate binding sequence
			ResultSequence rs = (ResultSequence) ve.expr().accept(this);

			// XXX
			if (rs.empty()) {
				iter.previous();
				return result;
			}

			QName varname = ve.varname();

			// for each item of binding sequence, bind the range
			// variable and do the expression, concatenating the
			// result

			for (Iterator i = rs.iterator(); i.hasNext();) {
				AnyType item = (AnyType) i.next();

				_dc.set_variable(varname, item);
				allocated_var = true;

				_dc.new_scope();
				ResultSequence res = do_for_quantified_expr(iter, finalexpr,
						type);
				_dc.destroy_scope();
				assert res != null;

				// ok here we got a "real" result, now figure
				// out what to do with it
				XSBoolean effbool = null;
				switch (type) {
				// for expression
				case 0:
					result.concat(res);
					break;

				// we need the effective boolean value
				// of the expression
				case 1:
				case 2:
					effbool = effective_boolean_value(res);
					break;

				default:
					assert false;
				}

				// we got a quantified expression
				if (effbool != null) {
					// for all
					if (type == 1) {
						result = ResultSequenceFactory.create_new(effbool);
						if (!effbool.value())
							break;
					}
					// there exists
					else if (type == 2) {
						result = ResultSequenceFactory.create_new(effbool);
						if (effbool.value())
							break;
					} else
						assert false;
				}
			}

			if (allocated_var) {
				boolean del = _dc.del_variable(varname);
				assert del == true;
			}

			iter.previous();
			return result;
		}
		// we finally got to do the "last expression"
		else {
			return (ResultSequence) finalexpr.accept(this);
		}

	}

	/**
	 * visit for expression
	 * 
	 * @param fex
	 *            is the for expression.
	 * @return a new function.
	 */
	public Object visit(ForExpr fex) {
		// XXX
		List pairs = new ArrayList(fex.ve_pairs());
		return do_for_quantified_expr(pairs.listIterator(), fex.expr(), 0);
	}

	/**
	 * visit quantified expression
	 * 
	 * @param qex
	 *            is the quantified expression.
	 * @return a new function or null.
	 */
	public Object visit(QuantifiedExpr qex) {
		// XXX
		List pairs = new ArrayList(qex.ve_pairs());

		int hack = 0;

		switch (qex.type()) {
		case QuantifiedExpr.SOME:
			hack = 2;
			break;
		case QuantifiedExpr.ALL:
			hack = 1;
			break;

		default:
			assert false;
			return null; // unreach
		}
		ResultSequence rs = do_for_quantified_expr(pairs.listIterator(), qex
				.expr(), hack);

		// make sure we found answer
		if (!rs.empty())
			return rs;

		// ok because all of this is a hack... here we go
		switch (qex.type()) {
		case QuantifiedExpr.SOME:
			return ResultSequenceFactory.create_new(new XSBoolean(false));
		case QuantifiedExpr.ALL:
			return ResultSequenceFactory.create_new(new XSBoolean(true));
		default:
			assert false;
			return null; // unreach
		}
	}

	/**
	 * visit if expression
	 * 
	 * @param ifex
	 *            is the if expression.
	 * @return a ifex.then_clause().accept(this).
	 */
	public Object visit(IfExpr ifex) {
		ResultSequence test_res = do_expr(ifex.iterator());

		XSBoolean res = effective_boolean_value(test_res);

		if (res.value())
			return ifex.then_clause().accept(this);
		else
			return ifex.else_clause().accept(this);
	}

	private boolean[] do_logic_exp(BinExpr e) {
		Collection args = do_bin_args(e);

		Iterator argiter = args.iterator();

		ResultSequence one = (ResultSequence) argiter.next();
		ResultSequence two = (ResultSequence) argiter.next();

		boolean oneb = effective_boolean_value(one).value();
		boolean twob = effective_boolean_value(two).value();

		boolean res[] = { oneb, twob };
		return res;
	}

	/**
	 * visit or expression
	 * 
	 * @param orex
	 *            is the or expression.
	 * @return a new function
	 */
	public Object visit(OrExpr orex) {
		boolean res[] = do_logic_exp(orex);

		return ResultSequenceFactory
				.create_new(new XSBoolean(res[0] || res[1]));
	}

	/**
	 * visit and expression
	 * 
	 * @param andex
	 *            is the and expression.
	 * @return a new function
	 */
	public Object visit(AndExpr andex) {
		boolean res[] = do_logic_exp(andex);

		return ResultSequenceFactory
				.create_new(new XSBoolean(res[0] && res[1]));
	}

	private ResultSequence node_cmp(int type, Collection args) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		assert args.size() == 2;

		Iterator argsiter = args.iterator();

		ResultSequence one = (ResultSequence) argsiter.next();
		ResultSequence two = (ResultSequence) argsiter.next();

		int size_one = one.size();
		int size_two = two.size();

		if (size_one > 1 || size_two > 1)
			report_error(TypeError.invalid_type(null));

		if (size_one == 0 || size_two == 0)
			return rs;

		AnyType at_one = one.first();
		AnyType at_two = two.first();

		if (!(at_one instanceof NodeType) || !(at_two instanceof NodeType))
			report_error(TypeError.invalid_type(null));

		// ok we got the args finally
		NodeType nt_one = (NodeType) at_one;
		NodeType nt_two = (NodeType) at_two;

		boolean answer = false; // we are pessimistic as usual

		// do comparison
		switch (type) {
		case CmpExpr.IS:
			answer = nt_one.node_value() == nt_two.node_value();
			break;

		case CmpExpr.LESS_LESS:
			answer = nt_one.before(nt_two);
			break;

		case CmpExpr.GREATER_GREATER:
			answer = nt_one.after(nt_two);
			break;

		default:
			assert false;
		}

		rs.add(new XSBoolean(answer));
		return rs;
	}

	/**
	 * visit compare expression
	 * 
	 * @param cmpex
	 *            is the compare expression.
	 * @return a new function or null
	 */
	public Object visit(CmpExpr cmpex) {
		try {
			Collection args = do_bin_args(cmpex);

			switch (cmpex.type()) {
			case CmpExpr.EQ:
				return FsEq.fs_eq_value(args, _dc);

			case CmpExpr.NE:
				return FsNe.fs_ne_value(args, _dc);

			case CmpExpr.GT:
				return FsGt.fs_gt_value(args, _dc);

			case CmpExpr.LT:
				return FsLt.fs_lt_value(args, _dc);

			case CmpExpr.GE:
				return FsGe.fs_ge_value(args, _dc);

			case CmpExpr.LE:
				return FsLe.fs_le_value(args, _dc);

			case CmpExpr.EQUALS:
				return FsEq.fs_eq_general(args, _dc);

			case CmpExpr.NOTEQUALS:
				return FsNe.fs_ne_general(args, _dc);

			case CmpExpr.GREATER:
				return FsGt.fs_gt_general(args, _dc);

			case CmpExpr.LESSTHAN:
				return FsLt.fs_lt_general(args, _dc);

			case CmpExpr.GREATEREQUAL:
				return FsGe.fs_ge_general(args, _dc);

			case CmpExpr.LESSEQUAL:
				return FsLe.fs_le_general(args, _dc);

			case CmpExpr.IS:
			case CmpExpr.LESS_LESS:
			case CmpExpr.GREATER_GREATER:
				return node_cmp(cmpex.type(), args);

			default:
				assert false;
			}
		} catch (DynamicError err) {
			report_error(err);
		}
		return null; // unreach
	}

	/**
	 * visit range expression
	 * 
	 * @param rex
	 *            is the range expression.
	 * @return a new function
	 */
	public Object visit(RangeExpr rex) {
		ResultSequence one = (ResultSequence) rex.left().accept(this);
		ResultSequence two = (ResultSequence) rex.right().accept(this);
		if (one.empty() || two.empty()) return ResultSequenceFactory.create_new(); 
		Collection args = new ArrayList();
		args.add(one);
		args.add(two);

		try {
			return OpTo.op_to(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	private XSBoolean effective_boolean_value(ResultSequence rs) {
		try {
			return FnBoolean.fn_boolean(rs);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	
	/**
	 * visit and expression
	 * 
	 * @param addex
	 *            is the and expression.
	 * @return a new function
	 */
	public Object visit(AddExpr addex) {
		try {
			Collection args = do_bin_args(addex);
			return FsPlus.fs_plus(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit sub expression
	 * 
	 * @param subex
	 *            is the sub expression.
	 * @return a new function
	 */
	public Object visit(SubExpr subex) {
		try {
			Collection args = do_bin_args(subex);
			return FsMinus.fs_minus(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit multiply expression
	 * 
	 * @param mulex
	 *            is the mul expression.
	 * @return a new function
	 */
	public Object visit(MulExpr mulex) {
		try {
			Collection args = do_bin_args(mulex);
			return FsTimes.fs_times(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit division expression
	 * 
	 * @param mulex
	 *            is the division expression.
	 * @return a new function
	 */
	public Object visit(DivExpr mulex) {
		try {
			Collection args = do_bin_args(mulex);
			return FsDiv.fs_div(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit integer division expression
	 * 
	 * @param mulex
	 *            is the integer division expression.
	 * @return a new function
	 */
	public Object visit(IDivExpr mulex) {
		try {
			Collection args = do_bin_args(mulex);
			return FsIDiv.fs_idiv(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit modular expression
	 * 
	 * @param mulex
	 *            is the modular expression.
	 * @return a new function
	 */
	public Object visit(ModExpr mulex) {
		try {
			Collection args = do_bin_args(mulex);
			return FsMod.fs_mod(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	private Collection do_bin_args(BinExpr e) {
		ResultSequence one = (ResultSequence) e.left().accept(this);
		ResultSequence two = (ResultSequence) e.right().accept(this);

		Collection args = new ArrayList();
		args.add(one);
		args.add(two);

		return args;
	}

	/**
	 * visit union expression
	 * 
	 * @param unex
	 *            is the union expression.
	 * @return a new function
	 */
	public Object visit(UnionExpr unex) {
		try {
			Collection args = do_bin_args(unex);
			return OpUnion.op_union(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit pipe expression
	 * 
	 * @param pipex
	 *            is the pipe expression.
	 * @return a new function
	 */
	// XXX same as above
	public Object visit(PipeExpr pipex) {
		try {
			Collection args = do_bin_args(pipex);
			return OpUnion.op_union(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit intersect expression
	 * 
	 * @param iexpr
	 *            is the intersect expression.
	 * @return a new function
	 */
	public Object visit(IntersectExpr iexpr) {
		try {
			Collection args = do_bin_args(iexpr);
			return OpIntersect.op_intersect(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit except expression
	 * 
	 * @param eexpr
	 *            is the except expression.
	 * @return a new function
	 */
	public Object visit(ExceptExpr eexpr) {
		try {
			Collection args = do_bin_args(eexpr);
			return OpExcept.op_except(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit instance of expression
	 * 
	 * @param ioexp
	 *            is the instance of expression.
	 * @return a new function
	 */
	public Object visit(InstOfExpr ioexp) {
		// XXX hack

		// get the value
		ResultSequence rs = (ResultSequence) ioexp.left().accept(this);

		// get the sequence type
		SequenceType seqt = (SequenceType) ioexp.right();
		// lamely "convert" it
		SeqType st = new SeqType(seqt, _dc);

		// see if they match
		try {
			st.match(rs);
		} catch (DynamicError err) {
			return ResultSequenceFactory.create_new(new XSBoolean(false));
		}

		return ResultSequenceFactory.create_new(new XSBoolean(true));
	}

	/**
	 * visit treat-as expression
	 * 
	 * @param taexp
	 *            is the treat-as expression.
	 * @return a new function
	 */
	public Object visit(TreatAsExpr taexp) {

		ResultSequence rs = (ResultSequence) taexp.left().accept(this);

		SequenceType seqt = (SequenceType) taexp.right();
		SeqType st = new SeqType(seqt, _dc);

		try {
			st.match(rs);
		} catch (DynamicError err) {
			report_error(err);
		}

		return rs;
	}

	/**
	 * visit castable expression
	 * 
	 * @param cexp
	 *            is the castable expression.
	 * @return a new function
	 */
	public Object visit(CastableExpr cexp) {

		// ResultSequence rs = (ResultSequence) cexp.left().accept(this);

		// XXX lame
		boolean castable = false;
		try {
			CastExpr ce = new CastExpr((Expr) cexp.left(), (SingleType) cexp
					.right());

			visit(ce);
			castable = true;
		} catch (Throwable t) {
			castable = false;
		}

		return ResultSequenceFactory.create_new(new XSBoolean(castable));
	}

	/**
	 * visit cast expression
	 * 
	 * @param cexp
	 *            is the cast expression.
	 * @return a new function
	 */
	public Object visit(CastExpr cexp) {

		ResultSequence rs = (ResultSequence) cexp.left().accept(this);
		SingleType st = (SingleType) cexp.right();

		rs = FnData.atomize(rs);

		if (rs.size() > 1)
			report_error(TypeError.invalid_type(null));

		if (rs.empty()) {
			if (st.qmark())
				return rs;
			else
				report_error(TypeError.invalid_type(null));
		}

		AnyType at = rs.first();

		if (!(at instanceof AnyAtomicType))
			report_error(TypeError.invalid_type(null));

		AnyAtomicType aat = (AnyAtomicType) at;

		QName type = st.type();

		// check if constructor exists
		// try {
		if (!_dc.function_exists(type, 1))
			report_error(TypeError.invalid_type(null));
		/*
		 * } catch(StaticNsNameError err) {
		 * report_error(TypeError.invalid_type(null)); }
		 */
		// prepare args from function
		Collection args = new ArrayList();
		args.add(ResultSequenceFactory.create_new(aat));

		try {
			return _dc.evaluate_function(type, args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit minus expression
	 * 
	 * @param e
	 *            is the minus expression.
	 * @return a new function
	 */
	public Object visit(MinusExpr e) {
		ResultSequence rs = (ResultSequence) e.arg().accept(this);

		Collection args = new ArrayList();
		args.add(rs);

		try {
			return FsMinus.fs_minus_unary(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit plus expression
	 * 
	 * @param e
	 *            is the plus expression.
	 * @return a new function
	 */
	public Object visit(PlusExpr e) {
		ResultSequence rs = (ResultSequence) e.arg().accept(this);

		Collection args = new ArrayList();
		args.add(rs);

		try {
			return FsPlus.fs_plus_unary(args);
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	// this will evaluate the step expression for the whole focus and return
	// the result.
	//
	// i.e. It will execute the step expression for each item in the focus
	// [each time changing the context item].
	private ResultSequence do_step(StepExpr se) {

		ResultSequence rs = ResultSequenceFactory.create_new();
		ArrayList results = new ArrayList();
		int type = 0; // 0: don't know yet
		// 1: atomic
		// 2: node

		Focus focus = _dc.focus();
		int original_pos = focus.position();

		// execute step for all items in focus
		while (true) {
			results.add(se.accept(this));

			// go to next
			if (!focus.advance_cp())
				break;
		}

		// make sure we didn't change focus
		focus.set_position(original_pos);

		boolean node_types = false;

		// check the results
		for (Iterator i = results.iterator(); i.hasNext();) {
			ResultSequence result = (ResultSequence) i.next();

			// make sure results are of same type, and add them in
			for (Iterator j = result.iterator(); j.hasNext();) {
				AnyType item = (AnyType) j.next();

				// first item
				if (type == 0) {
					if (item instanceof AnyAtomicType)
						type = 1;
					else if (item instanceof NodeType)
						type = 2;
					else
						assert false;

				}

				// make sure we got coherent types
				switch (type) {
				// atomic... just concat
				case 1:
					if (!(item instanceof AnyAtomicType))
						report_error(TypeError.mixed_vals(null));
					rs.add(item);
					break;

				case 2:
					node_types = true;
					if (!(item instanceof NodeType))
						report_error(TypeError.mixed_vals(null));
					rs.add(item);
					break;

				default:
					assert false;
				}
			}
		}

		// XXX lame
		if (node_types) {
			rs = NodeType.eliminate_dups(rs);
			rs = NodeType.sort_document_order(rs);
		}

		return rs;
	}

	private ResultSequence root_self_node() {
		Axis axis = new SelfAxis();
		ResultSequence rs;

		// XXX the cast!!!
		rs = axis.iterate((NodeType) _dc.context_item(), _dc);

		rs = kind_test(rs, NodeType.class);

		try {
			List records = new ArrayList();
			records.add(rs);
			rs = FnRoot.fn_root(records, _dc);
		} catch (DynamicError err) {
			report_error(err);
		}
		return rs;
	}

	private ResultSequence descendant_or_self_node(ResultSequence rs) {
		ResultSequence res = ResultSequenceFactory.create_new();
		Axis axis = new DescendantOrSelfAxis();

		// for all nodes, get descendant or self nodes
		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType item = (NodeType) i.next();

			ResultSequence nodes = axis.iterate(item, _dc);
			nodes = kind_test(nodes, NodeType.class);

			res.concat(nodes);
		}

		return res;
	}

	/**
	 * visit XPath expression
	 * 
	 * @param e
	 *            is the XPath expression.
	 * @return a new function
	 */
	public Object visit(XPathExpr e) {
		XPathExpr xp = e;

		ResultSequence rs = null;
		Focus original_focus = _dc.focus();

		// do all the steps
		while (xp != null) {
			StepExpr se = xp.expr();

			if (se != null) {
				// this is not the first step
				if (rs != null) {
					// XXX ?
					// the expression didn't return any
					// results...
					if (rs.size() == 0)
						break;

					// make sure result of previous step are
					// nodes!
					for (Iterator i = rs.iterator(); i.hasNext();) {
						AnyType item = (AnyType) i.next();

						if (!(item instanceof NodeType)) {
							report_error(TypeError.step_conatins_atoms(null));
							return null; // unreach
						}
					}

					// check if we got a //
					if (xp.slashes() == 2) {
						rs = descendant_or_self_node(rs);

						if (rs.size() == 0)
							break;
					}

					// make result of previous step the new
					// focus
					_dc.set_focus(new Focus(rs));

					// do the step for all item in context
					rs = do_step(se);
				}
				// this is first step...
				// note... we may be called from upstream...
				// like in the expression sorbo/*[2] ... we may
				// be called to evaluate the 2... the caller
				// will iterate through the whole outer focus
				// for us
				else {
					// XXX ???
					if (xp.slashes() == 1) {
						rs = root_self_node();
						_dc.set_focus(new Focus(rs));

						rs = do_step(se);
					} else if (xp.slashes() == 2) {
						ResultSequence res = ResultSequenceFactory.create_new();
						rs = root_self_node();

						rs = descendant_or_self_node(rs);

						_dc.set_focus(new Focus(rs));

						rs = do_step(se);
					} else
						rs = (ResultSequence) se.accept(this);
				}
			}
			// the expression is "/"
			else {
				assert xp.slashes() == 1;

				rs = root_self_node();
			}

			xp = xp.next();
		}

		// restore focus
		_dc.set_focus(original_focus);

		return rs;
	}

	/**
	 * visit a forward step expression
	 * 
	 * @param e
	 *            is the forward step.
	 * @return a new function
	 */
	public Object visit(ForwardStep e) {

		// get context node
		AnyType ci = _dc.context_item();
		
		if (ci == null) 
			report_error(DynamicError.contextUndefined());

		if (!(ci instanceof NodeType))
			report_error(TypeError.ci_not_node(ci.string_type()));

		NodeType cn = (NodeType) ci;

		// get the nodes on the axis
		ForwardAxis axis = e.iterator();
		ResultSequence nodes = axis.iterate(cn, _dc);
		// get all nodes in the axis, and principal node
		Pair arg = new Pair(axis.principal_node_kind().string_type(), nodes);

		// do the name test
		_param = arg;
		ResultSequence rs = (ResultSequence) e.node_test().accept(this);

		return rs;
	}

	/**
	 * visit a reverse step expression
	 * 
	 * @param e
	 *            is the reverse step.
	 * @return a new function
	 */
	// XXX unify with top
	public Object visit(ReverseStep e) {
		// get context node
		AnyType ci = _dc.context_item();

		if (!(ci instanceof NodeType))
			report_error(TypeError.ci_not_node(ci.string_type()));

		NodeType cn = (NodeType) ci;

		// get the nodes on the axis
		ReverseAxis axis = e.iterator();

		// short for "gimme da parent"
		if (e.axis() == ReverseStep.DOTDOT) {
			axis = new ParentAxis();

			return kind_test(axis.iterate(cn, _dc), NodeType.class);
		}

		assert axis != null;

		ResultSequence nodes = axis.iterate(cn, _dc);
		// get all nodes in the axis, and principal node
		Pair arg = new Pair(axis.principal_node_kind().string_type(), nodes);

		// do the name test
		_param = arg;
		ResultSequence rs = (ResultSequence) e.node_test().accept(this);

		return rs;
	}

	// XXX this routine sux
	private boolean name_test(NodeType node, QName name, String type) {
		// make sure principal node kind is the same
		if (node == null) {
			return false;
		}
		if (!type.equals(node.string_type())) {
			return false;
		}

		String test_prefix = name.prefix();

		// if unprefixed and principal node kind is element, set default
		// element namespace
		if (test_prefix == null && type.equals("element")) {
			// XXX make a new copy
			name = new QName(null, name.local());
			name.set_namespace(_dc.default_namespace());

			// if we actually have a namespace, pretend we do =D
			if (name.namespace() != null)
				test_prefix = "";
		}

		QName node_name = node.node_name();

		assert node_name != null;

		// make sure namespace matches
		String node_namespace = node_name.namespace();

		String test_namespace = null;
		if (name.expanded())
			test_namespace = name.namespace();

		// name test has no prefix
		if (test_prefix == null) {
			// ok no namespace... match
			if (node_namespace == null) {
			} else {
				return false;
			}
		}
		// name test has a prefix and is not wildcard
		// XXX AT THIS POINT ALL PREFIXES NEED TO BE RESOLVED!
		else if (!test_namespace.equals("*")) {
			// the node doesn't have a namespace... no match
			if (node_namespace == null) {
				return false;
			}
			// check namespaces
			else {
				if (node_namespace.equals(test_namespace)) {
					// namespace matches
				} else {
					return false;
				}
			}
		}

		// make sure local part matches
		// check for wildcard in localpart
		if (name.local().equals("*"))
			return true;

		// check if local part matches
		if (!name.local().equals(node_name.local())) {
			return false;
		}

		return true;
	}

	/**
	 * visit a name test expression
	 * 
	 * @param e
	 *            is thename test.
	 * @return a result sequence
	 */
	public Object visit(NameTest e) {
		QName name = e.name();

		// get the arguments
		Pair arg = (Pair) _param;
		String type = (String) arg._one;
		ResultSequence rs = (ResultSequence) arg._two;

		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType nt = (NodeType) i.next();

			// check if node passes name test
			if (!name_test(nt, name, type))
				i.remove();

		}

		return rs;
	}

	/**
	 * visit variable reference
	 * 
	 * @param e
	 *            is the variable reference.
	 * @return a result sequence
	 */
	public Object visit(VarRef e) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		AnyType var = _dc.get_variable(e.name());

		assert var != null;

		rs.add(var);

		return rs;
	}

	/**
	 * visit string literal.
	 * 
	 * @param e
	 *            is the string literal.
	 * @return a result sequence
	 */
	public Object visit(StringLiteral e) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		rs.add(e.value());
		return rs;
	}

	/**
	 * visit integer literal.
	 * 
	 * @param e
	 *            is the integer literal.
	 * @return a result sequence
	 */
	public Object visit(IntegerLiteral e) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		rs.add(e.value());
		return rs;
	}

	/**
	 * visit double literal.
	 * 
	 * @param e
	 *            is the double literal.
	 * @return a result sequence
	 */
	public Object visit(DoubleLiteral e) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		rs.add(e.value());
		return rs;
	}

	/**
	 * visit decimal literal.
	 * 
	 * @param e
	 *            is the decimal literal.
	 * @return a result sequence
	 */
	public Object visit(DecimalLiteral e) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		rs.add(e.value());
		return rs;
	}

	/**
	 * visit parent expression.
	 * 
	 * @param e
	 *            is the parent expression.
	 * @return a new function
	 */
	public Object visit(ParExpr e) {
		return do_expr(e.iterator());
	}

	/**
	 * visit context item expression.
	 * 
	 * @param e
	 *            is the context item expression.
	 * @return a result sequence
	 */
	public Object visit(CntxItemExpr e) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		AnyType contextItem = _dc.context_item();
		if (contextItem == null) {
			report_error(DynamicError.contextUndefined());
		}
		rs.add(contextItem);
		return rs;
	}

	/**
	 * visit function call.
	 * 
	 * @param e
	 *            is the function call.
	 * @return a new function or null
	 */
	public Object visit(FunctionCall e) {
		ArrayList args = new ArrayList();

		for (Iterator i = e.iterator(); i.hasNext();) {
			Expr arg = (Expr) i.next();
			// each argument will produce a result sequence
			args.add(arg.accept(this));
		}

		try {
			ResultSequence rs = _dc.evaluate_function(e.name(), args);
			return rs;
		} catch (DynamicError err) {
			report_error(err);
			return null; // unreach
		}
	}

	/**
	 * visit single type.
	 * 
	 * @param e
	 *            is the single type.
	 * @return null
	 */
	public Object visit(SingleType e) {
		return null;
	}

	/**
	 * visit sequence type.
	 * 
	 * @param e
	 *            is the sequence type.
	 * @return null
	 */
	public Object visit(SequenceType e) {
		ItemType it = e.item_type();

		if (it != null)
			it.accept(this);

		return null;
	}

	/**
	 * visit item type.
	 * 
	 * @param e
	 *            is the item type.
	 * @return null
	 */
	public Object visit(ItemType e) {

		switch (e.type()) {
		case ItemType.ITEM:
			break;
		case ItemType.QNAME:

			// try {
			if (!_dc.type_defined(e.qname()))
				report_error(new StaticTypeNameError("Type not defined: "
						+ e.qname().string()));
			/*
			 * } catch(StaticNsNameError ns) { report_error(ns); }
			 */
			break;

		case ItemType.KINDTEST:
			e.kind_test().accept(this);
			break;
		}

		return null;
	}

	private ResultSequence kind_test(ResultSequence rs, Class kind) {
		for (Iterator i = rs.iterator(); i.hasNext();) {
			if (!kind.isInstance(i.next()))
				i.remove();
		}
		return rs;
	}

	/**
	 * visit any kind test.
	 * 
	 * @param e
	 *            is the any kind test.
	 * @return a new function
	 */
	public Object visit(AnyKindTest e) {
		ResultSequence arg = (ResultSequence) ((Pair) _param)._two;

		return kind_test(arg, NodeType.class);
	}

	/**
	 * visit document test.
	 * 
	 * @param e
	 *            is the document test.
	 * @return result sequence
	 */
	public Object visit(DocumentTest e) {
		ResultSequence arg = (ResultSequence) ((Pair) _param)._two;
		int type = e.type();

		// filter doc nodes
		ResultSequence rs = kind_test(arg, DocType.class);

		if (type == DocumentTest.NONE)
			return rs;

		// for all docs, find the ones with exactly one element, and do
		// the element test
		for (Iterator i = rs.iterator(); i.hasNext();) {
			DocType doc = (DocType) i.next();
			int elem_count = 0;
			ElementType elem = null;

			// make sure doc has only 1 element
			NodeList children = doc.node_value().getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);

				// bingo
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					elem_count++;

					if (elem_count > 1)
						break;

					elem = new ElementType((Element) child);
				}
			}

			// this doc is no good... send him to hell
			if (elem_count != 1) {
				i.remove();
				continue;
			}

			assert elem != null;

			// setup parameter for element test
			ResultSequence res = ResultSequenceFactory.create_new();
			res.add(elem);
			_param = new Pair("element", res);

			// do name test
			res = null;
			if (type == DocumentTest.ELEMENT)
				res = (ResultSequence) e.elem_test().accept(this);
			else if (type == DocumentTest.SCHEMA_ELEMENT)
				res = (ResultSequence) e.schema_elem_test().accept(this);
			else
				assert false;

			// check if element survived nametest
			if (res.size() != 1)
				i.remove();
		}

		return rs;
	}

	/**
	 * visit text test.
	 * 
	 * @param e
	 *            is the text test.
	 * @return a new function
	 */
	public Object visit(TextTest e) {
		ResultSequence arg = (ResultSequence) ((Pair) _param)._two;

		return kind_test(arg, TextType.class);
	}

	/**
	 * visit comment test.
	 * 
	 * @param e
	 *            is the text test.
	 * @return a new function
	 */
	public Object visit(CommentTest e) {
		ResultSequence arg = (ResultSequence) ((Pair) _param)._two;

		return kind_test(arg, CommentType.class);
	}

	/**
	 * visit PI test.
	 * 
	 * @param e
	 *            is the PI test.
	 * @return a argument
	 */
	public Object visit(PITest e) {
		ResultSequence arg = (ResultSequence) ((Pair) _param)._two;

		String pit_arg = e.arg();

		// match any pi
		if (pit_arg == null)
			return kind_test(arg, PIType.class);

		for (Iterator i = arg.iterator(); i.hasNext();) {
			AnyType item = (AnyType) i.next();

			// match PI
			if (item instanceof PIType) {
				PIType pi = (PIType) item;

				// match target
				if (!pit_arg.equals(pi.value().getTarget()))
					i.remove();
			} else
				i.remove();
		}

		return arg;
	}

	/**
	 * visit attribute test.
	 * 
	 * @param e
	 *            is the attribute test.
	 * @return a result sequence
	 */
	public Object visit(AttributeTest e) {
		// filter out all attrs
		ResultSequence rs = kind_test((ResultSequence) ((Pair) _param)._two,
				AttrType.class);

		// match the name if it's not a wild card
		QName name = e.name();
		if (name != null && !e.wild()) {
			for (Iterator i = rs.iterator(); i.hasNext();) {
				if (!name_test((NodeType) i.next(), name, "attribute"))

					i.remove();
			}
		}

		// match the type
		QName type = e.type();
		if (type != null) {
			for (Iterator i = rs.iterator(); i.hasNext();) {
				NodeType node = (NodeType) i.next();

				// check if element derives from
				if (!_dc.derives_from(node, type))
					i.remove();
			}
		}

		return rs;
	}

	/**
	 * visit schema attribute test.
	 * 
	 * @param e
	 *            is the schema attribute test.
	 * @return a result sequence
	 */
	public Object visit(SchemaAttrTest e) {
		// filter out all attrs
		ResultSequence rs = kind_test((ResultSequence) ((Pair) _param)._two,
				AttrType.class);

		// match the name
		QName name = e.arg();
		for (Iterator i = rs.iterator(); i.hasNext();) {
			if (!name_test((NodeType) i.next(), name, "attribute"))

				i.remove();
		}

		// check the type
		XSTypeDefinition et = _dc.attribute_type_definition(name);
		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType node = (NodeType) i.next();

			if (!_dc.derives_from(node, et))
				i.remove();

		}

		return rs;
	}

	/**
	 * visit element test.
	 * 
	 * @param e
	 *            is the element test.
	 * @return a result sequence
	 */
	public Object visit(ElementTest e) {

		// filter out all elements
		ResultSequence rs = kind_test((ResultSequence) ((Pair) _param)._two,
				ElementType.class);

		// match the name if it's not a wild card
		QName name = e.name();
		if (name != null && !e.wild()) {
			for (Iterator i = rs.iterator(); i.hasNext();) {
				if (!name_test((ElementType) i.next(), name, "element"))

					i.remove();
			}
		}

		// match the type
		QName type = e.type();
		if (type != null) {
			for (Iterator i = rs.iterator(); i.hasNext();) {
				NodeType node = (NodeType) i.next();

				// check if element derives from
				if (_dc.derives_from(node, type)) {
					// nilled may be true or false
					if (e.qmark()) {
					}
					// nilled has to be false
					else {
						XSBoolean nilled = (XSBoolean) node.nilled().first();
						if (nilled.value())
							i.remove();
					}

				} else
					i.remove();
			}
		}

		return rs;
	}

	/**
	 * visit schema element test.
	 * 
	 * @param e
	 *            is the schema element test.
	 * @return a result sequence
	 */
	public Object visit(SchemaElemTest e) {
		// filter out all elements
		ResultSequence rs = kind_test((ResultSequence) ((Pair) _param)._two,
				ElementType.class);

		// match the name
		// XXX substitution groups
		QName name = e.name();
		for (Iterator i = rs.iterator(); i.hasNext();) {
			if (!name_test((ElementType) i.next(), name, "element"))

				i.remove();
		}

		// check the type
		XSTypeDefinition et = _dc.element_type_definition(name);
		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType node = (NodeType) i.next();

			if (!_dc.derives_from(node, et)) {
				i.remove();
				continue;
			}

			XSBoolean nilled = (XSBoolean) node.nilled().first();
			// XXX or, in schema it is nillable
			if (nilled.value())
				i.remove();
		}

		return rs;
	}

	private boolean predicate_truth(ResultSequence rs) {
		// rule 1 of spec... if numeric type:
		// if num eq position then true else false
		if (rs.size() == 1) {
			AnyType at = rs.get(0);

			if (at instanceof NumericType) {
				try {
					_g_xsint.set_int(BigInteger.valueOf(_dc.context_position()));
					return FsEq.fs_eq_fast(at, _g_xsint, _dc);
				} catch (DynamicError err) {
					report_error(err);

					// unreach
					assert false;
					return false;
				}
			}
		}

		// rule 2
		XSBoolean ret = effective_boolean_value(rs);

		return ret.value();
	}

	// do the predicate for all items in focus
	private ResultSequence do_predicate(Collection exprs) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		Focus focus = _dc.focus();
		int original_cp = focus.position();

		// optimization
		// check if predicate is single numeric constant
		if (exprs.size() == 1) {
			Expr expr = (Expr) exprs.iterator().next();

			if (expr instanceof XPathExpr) {
				XPathExpr xpe = (XPathExpr) expr;
				if (xpe.next() == null && xpe.slashes() == 0
						&& xpe.expr() instanceof FilterExpr) {
					FilterExpr fex = (FilterExpr) xpe.expr();
					if (fex.primary() instanceof IntegerLiteral) {
						int pos = (((IntegerLiteral) fex.primary()).value()
								.int_value()).intValue();

						if (pos <= focus.last() && pos > 0) {
							focus.set_position(pos);
							rs.add(focus.context_item());
						}
						focus.set_position(original_cp);
						return rs;
					}
				}
			}
		}

		// go through all elements
		while (true) {
			// do the predicate
			// XXX saxon doesn't allow for predicates to have
			// commas... but XPath 2.0 spec seems to do
			ResultSequence res = do_expr(exprs.iterator());

			// if predicate is true, the context item is definitely
			// in the sequence
			if (predicate_truth(res))
				rs.add(_dc.context_item());

			res.release();
			if (!focus.advance_cp())
				break;

		}

		// restore
		focus.set_position(original_cp);

		return rs;
	}

	/**
	 * visit axis step.
	 * 
	 * @param e
	 *            is the axis step.
	 * @return a result sequence
	 */
	public Object visit(AxisStep e) {
		ResultSequence rs = (ResultSequence) e.step().accept(this);

		if (e.predicate_count() == 0)
			return rs;

		// I take it predicates are logical ANDS...
		Focus original_focus = _dc.focus();

		// go through all predicates
		for (Iterator i = e.iterator(); i.hasNext();) {
			// empty results... get out of here ? XXX
			if (rs.size() == 0)
				break;

			_dc.set_focus(new Focus(rs));
			rs = do_predicate((Collection) i.next());

		}

		// restore focus [context switching ;D ]
		_dc.set_focus(original_focus);
		return rs;
	}

	/**
	 * visit filter expression
	 * 
	 * @param e
	 *            is the filter expression.
	 * @return a result sequence
	 */
	// XXX unify with top ?
	public Object visit(FilterExpr e) {
		ResultSequence rs = (ResultSequence) e.primary().accept(this);

		// if no predicates are present, then the result is the same as
		// the primary expression
		if (e.predicate_count() == 0)
			return rs;

		Focus original_focus = _dc.focus();

		// go through all predicates
		for (Iterator i = e.iterator(); i.hasNext();) {
			if (rs.size() == 0)
				break;

			_dc.set_focus(new Focus(rs));
			rs = do_predicate((Collection) i.next());

		}

		// restore focus [context switching ;D ]
		_dc.set_focus(original_focus);
		return rs;
	}
}
