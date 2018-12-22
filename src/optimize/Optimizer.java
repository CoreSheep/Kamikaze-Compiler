package optimize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import parser.Parser;
import utility.CaculateQuadruple;
import utility.DAGNode;
import utility.DAGNode.DAGType;
import utility.Quadruple;
import utility.Quadruple.Operation;
import utility.QuadrupleTable;

/**
 * Optimizing in constructing a compiler is gonna focus on optimizing the
 * quadruple table. Using DAG method to solve this problem. The process of
 * optimization is to the process of building the DAG
 * 
 * @author Marshall Lee
 * @time 2018.12.18
 * @version 1.2
 *
 */
public class Optimizer {
	// using locally
	private List<DAGNode> DAGTable;
	private Quadruple q;
	private CaculateQuadruple cq;
	private int comExpID = -1;

	// using broadly
	public Parser parser;
	public List<String> tempVarTable;
	public QuadrupleTable qt; // optimized quadruple table

	// filename
	String filename = "H:\\Java Workplace Marshall\\Kamikaze\\test1.txt";

	// Optimizer Constructor
	public Optimizer() {
		parser = new Parser();
		parser.Compiler(filename);
		DAGTable = new ArrayList<DAGNode>();
		tempVarTable = new ArrayList<String>();
		qt = new QuadrupleTable();
		q = new Quadruple();
		cq = new CaculateQuadruple();
	}

	/**
	 * Build the DAG. For each quadruple, we traverse it and analyze it. And create
	 * DAGNode for need. I just analyze two kinds of statements, they are
	 * ASSIGN-STATEMENT and EXPRESSION-STATEMENT。
	 * 
	 * @author Marshall Lee
	 * @version 1.2
	 */
	public void build() {
		for (int i = 0; i < parser.Quad.Quad.size(); i++) {
			q = parser.Quad.Quad.get(i);
			Operation opt = q.opt;
			switch (opt) {
			case ADD:
			case SUB:
			case MUL:
			case DIV:
				insert(q);
				break;
			case ASS:
				insertASS(q);
				break;
			case NULL:
				break;
			default:
				break;

			}
		}
	}

	/**
	 * insert a DAGNode(Directed Acyclic Graph) based on this quadruple.
	 * 
	 * @param Quadruple quad
	 */
	private void insert(Quadruple quad) {
		// TODO Auto-generated method stub
		int resint = 0;
		float resfloat = 0;
		int idLeaf;
		/*
		 * 1. Check if the quadruple is constant expression, if so, calculate it first
		 * and create a DAGNode.
		 */
		if (isConstExpression(quad)) {
			cq.cacul(quad);
			if (cq.resflag) {
				resint = (int) cq.result;
				idLeaf = isDefined(resint + "");
				if (idLeaf == -1)
					idLeaf = insertLeafNode(resint + "");
			} else {

				resfloat = cq.result;
				idLeaf = isDefined(resfloat + "");
				if (idLeaf == -1)
					idLeaf = insertLeafNode(resfloat + "");
			}
			insertTag(idLeaf, quad.res);
		}

		/*
		 * 2. check if the quadruple is common sub-expression, if so, we don't need to
		 * create new node, and just insert the new tag on the mother DGANode.
		 */
		else if (isComSubExp(quad)) {
			insertTag(comExpID, quad.res);

		}

		/*
		 * 3. other cases: no common sun-expression, no constant-expression firstly,
		 * check if the operands are defined, if not, create new leafNode, if so, insert
		 * tag;
		 */
		else {

			int ID1, ID2;
			if ((ID1 = isDefined(quad.op1)) == -1)
				ID1 = insertLeafNode(quad.op1);
			if ((ID2 = isDefined(quad.op2)) == -1)
				ID2 = insertLeafNode(quad.op2);

			DAGNode newNode = new DAGNode(DAGTable.size(), quad.opt, quad.res, ID1, ID2);
			DAGTable.add(newNode);

		}

	}

	/**
	 * insert DGANode for assign quadruple
	 * 
	 * @author Marshall Lee
	 * @param quad
	 */
	private void insertASS(Quadruple quad) {
		// TODO Auto-generated method stub
		int oprID = isDefined(quad.op1);
		int resID = isDefined(quad.res);

		// if op1 isn't existed in DGATable, create a new DGANode.
		if (oprID == -1)
			oprID = insertLeafNode(quad.op1);
		if (resID != -1)
			deleteTag(resID, quad.res);
		insertTag(oprID, quad.res);

	}

	/**
	 * insert tag only for assignment quadruple Not creating the DAGNode for this
	 * quadruple
	 * 
	 * @param id  -- DAG ID
	 * @param tag
	 */
	private void insertTag(int id, String tag) {
		// TODO Auto-generated method stub
		if (isSuper(tag, DAGTable.get(id).mainTag))
			switchTag(id, tag);
		else {
			DAGTable.get(id).subTag.add(tag);
		}

	}

	/**
	 * delete tag on DAGNode if the tag is mainTag, do not delete it.
	 * 
	 * @param mainTag
	 * @return
	 */

	private boolean deleteTag(int id, String tag) {

		return DAGTable.get(id).mainTag.equals(tag) ? false : DAGTable.get(id).subTag.remove(tag);
	}

	private int insertLeafNode(String mainTag) {
		// TODO Auto-generated method stub
		int id = DAGTable.size();
		DAGNode leafNode = new DAGNode(DAGTable.size(), mainTag);
		DAGTable.add(leafNode);
		return id;
	}

	/**
	 * Switch mainTag and subTag
	 * 
	 * @param id
	 * @param mainTag
	 */
	private void switchTag(int id, String mainTag) {

		DAGNode node = new DAGNode();
		node = DAGTable.get(id);
		node.subTag.add(node.mainTag);
		node.mainTag = mainTag;

	}

	/**
	 * Whether the the quadruple is Common-Sub-Expression, if so, there is no need
	 * to insert left and right node.
	 * 
	 * @param q
	 * @return isComSubExp
	 * @author Marshall Lee
	 * @time 2018.12.19
	 * @version 1.2
	 */
	private boolean isComSubExp(Quadruple q) {

		DAGNode tmpNode = new DAGNode();
		// 逆序遍历DGA图，查看是否存在公共子表达式，如果存在，则返回对应母结点的ID,否则，返回-1
		for (int i = DAGTable.size() - 1; i >= 0; i--) {
			boolean flagLeft = false, flagRight = false;
			tmpNode = DAGTable.get(i);
			if (tmpNode.opt == q.opt) {
				if (tmpNode.leftNode != -1)
					flagLeft = (DAGTable.get(tmpNode.leftNode).mainTag.equals(q.op1)
							|| DAGTable.get(tmpNode.leftNode).subTag.contains(q.op1));
				if (tmpNode.rightNode != -1)
					flagRight = (DAGTable.get(tmpNode.rightNode).mainTag.equals(q.op2)
							|| DAGTable.get(tmpNode.leftNode).subTag.contains(q.op1));
				if (flagLeft && flagRight) {
					comExpID = tmpNode.id;
					return true;
				}
			} else
				continue;
		}

		return false;
	}

	/**
	 * Whether the tag is defined?
	 * 
	 * @param tag
	 * @return True for ID, False for -1.
	 */
	private int isDefined(String tag) {
		int id = -1;
		DAGNode node = new DAGNode();
		// return -1: no such tag
		// return id: id of the tag existed in DGATable

		for (int i = DAGTable.size() - 1; !DAGTable.isEmpty() && i >= 0; i--) {
			node = DAGTable.get(i);
			if (node.mainTag.equals(tag) || containString(node, tag)) {
				id = node.id;
				break;
			} else
				continue;

		}

		return id;
	}

	private boolean containString(DAGNode node, String str) {
		boolean flag = false;
		if (!node.subTag.isEmpty()) {
			Iterator<String> it = node.subTag.iterator();
			while (it.hasNext()) {
				if (it.next().equals(str)) {
					flag = true;
					break;
				}
			}

		}
		return flag;
	}

	/**
	 * Judge whether the new tag is superior than mainTag.
	 * 
	 * @param tag
	 * @param mainTag
	 * @return
	 */
	private boolean isSuper(String tag, String mainTag) {
		// TODO Auto-generated method stub
		DAGType t1, t2;
		t1 = getDAGType(tag);
		t2 = getDAGType(mainTag);
		// System.out.println("type op1:" + t1 + "\ttype op2:" + t2);

		// which is prior than the other?
		if (t1.compareTo(t2) > 0) {
			return true;
		}

		else
			return false;
	}

	/**
	 * Get the type for operand and result.
	 * 
	 * @param tag
	 * @return
	 */
	private DAGType getDAGType(String tag) {
		// TODO Auto-generated method stub

		if (parser.lex.symbolTable.hasName(tag))
			return DAGType.USERVAR;
		else if (isDigit(tag))
			return DAGType.CONSTANT;
		else
			return DAGType.TEMPVAR;
	}

	/**
	 * Is digit?
	 * 
	 * @param tag
	 * @return
	 */
	public boolean isDigit(String tag) {
		// TODO Auto-generated method stub
		for (int i = 0; i < tag.length(); i++)
			if (Character.isDigit(tag.charAt(i)) || tag.charAt(i) == '.')
				continue;
			else
				return false;

		return true;
	}

	/**
	 * constant expression -- calculate it.
	 * 
	 * @param quad
	 * @return
	 */
	private boolean isConstExpression(Quadruple quad) {
		// TODO Auto-generated method stub
		boolean flag = true;
		for (int i = 0; i < quad.op1.length(); i++)
			if (Character.isDigit(quad.op1.charAt(i)) || quad.op1.charAt(i) == '.')
				continue;
			else {
				flag = false;
				break;
			}
		if (flag) {
			for (int i = 0; i < quad.op2.length(); i++)
				if (Character.isDigit(quad.op2.charAt(i)) || quad.op2.charAt(i) == '.')
					continue;
				else {
					flag = false;
					break;
				}
		}

		return flag;
	}

	public void showDGATable() {
		System.out.println("ID:\tOPT:\tMainTag:\tLNode:\t\tRNode:\t\tSubTag:\t");
		Iterator<DAGNode> it = DAGTable.iterator();
		while (it.hasNext())
			System.out.println(it.next());

	}

	/**
	 * Whether the subTag has USERVAR?
	 * 
	 * @param node
	 * @return
	 */
	private String hasSubTagID(DAGNode node) {
		DAGType t;
		String tag = "";
		Iterator<String> it = node.subTag.iterator();
		while (it.hasNext()) {
			tag = it.next();
			t = getDAGType(tag);
			if (t == DAGType.USERVAR)
				return tag;
		}
		return null;
	}

	/**
	 * Traverse the DAG again to get optimized quadruple table. Store it into qt.
	 * 
	 * @time 2018.12.19
	 */
	public void getOptimizedQT() {
		Stack<Quadruple> stack = new Stack<Quadruple>();
		String isSubTagID = null;
		DAGNode node = new DAGNode();
		for (int i = DAGTable.size() - 1; i >= 0; i--) {
			node = DAGTable.get(i);
			writeTempVar(node);
			if (node.leftNode != -1 && node.rightNode != -1) {
				String op1 = "", op2 = "", res = "";
				op1 = DAGTable.get(node.leftNode).mainTag;
				op2 = DAGTable.get(node.rightNode).mainTag;
				res = node.mainTag;
				stack.push(new Quadruple(node.opt, op1, op2, res));

				isSubTagID = hasSubTagID(node);
				if (isSubTagID != null) {
					stack.push(new Quadruple(Operation.ASS, node.mainTag, "", isSubTagID));
				}

			}

		}
		while (!stack.isEmpty()) {
			qt.Quad.add(stack.pop());
		}
	}

	/**
	 * create a temporary variable table
	 * 
	 * @param node
	 */
	private void writeTempVar(DAGNode node) {
		// TODO Auto-generated method stub
		DAGType type = getDAGType(node.mainTag);
		if (type == DAGType.TEMPVAR && !tempVarTable.contains(node.mainTag))
			tempVarTable.add(node.mainTag);

		for (int i = 0; i < node.subTag.size(); i++) {
			type = getDAGType(node.subTag.get(i));
			if (type == DAGType.TEMPVAR && !tempVarTable.contains(node.subTag.get(i)))
				tempVarTable.add(node.subTag.get(i));
		}

	}

	/*
	 * public static void main(String[] args) { Optimizer optimizer = new
	 * Optimizer(); optimizer.parser.Quad.showQuadruple(); optimizer.build();
	 * optimizer.getOptimizedQT(); optimizer.qt.showQuadruple();
	 * optimizer.showDGATable(); System.out.println(optimizer.tempVarTable); }
	 * 
	 */

}
