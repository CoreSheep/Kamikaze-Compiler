package generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import optimize.Optimizer;
import utility.ActiveRecord;
import utility.Quadruple;
import utility.Quadruple.Active;
import utility.Quadruple.Operation;

/**
 * Generating Code based on ASM
 * 
 * @author Marshall Lee
 *
 */
public class Generator {

	ActiveRecord RDL;
	private Optimizer optimizer;
	public List<String> obj;
	List<ActiveRecord> activeTable;

	/**
	 * Generator Constructor
	 */
	public Generator() {

		RDL = new ActiveRecord();
		optimizer = new Optimizer();
		obj = new ArrayList<String>();
		activeTable = new ArrayList<ActiveRecord>();

	}

	/**
	 * fill active-table
	 * 
	 * @author Marshall Lee
	 */
	private void fillActive() {
		Quadruple q = new Quadruple();
		// 1. add USERVAR into active table
		for (int i = 0; i < optimizer.parser.lex.symbolTable.symbolTable.size(); i++) {
			activeTable
					.add(new ActiveRecord(optimizer.parser.lex.symbolTable.symbolTable.get(i).name(), Active.IsActive));

		}

		// 2. add TEMPVAR into active table
		for (int i = 0; i < optimizer.tempVarTable.size(); i++) {
			activeTable.add(new ActiveRecord(optimizer.tempVarTable.get(i), Active.NoActive));
		}

		for (int i = optimizer.qt.Quad.size() - 1; i >= 0; i--) {
			q = optimizer.qt.Quad.get(i);
			if (!optimizer.isDigit(q.res)) {
				q.activeR = getActive(q.res);
				refillActive(q.res, Active.NoActive); // refill NoActive for result field
			}
			if (!optimizer.isDigit(q.op1)) {
				q.active1 = getActive(q.res);
				refillActive(q.op1, Active.IsActive); // refill Active for operand field
			}
			if (!optimizer.isDigit(q.op2)) {
				q.active2 = getActive(q.res);
				refillActive(q.op2, Active.IsActive);
			}

		}

	}

	private Active getActive(String res) {
		// TODO Auto-generated method stub
		Iterator<ActiveRecord> it = activeTable.iterator();
		ActiveRecord act = new ActiveRecord();
		while (it.hasNext()) {
			act = it.next();
			if (act.tag.equals(res))
				return act.active;
		}
		return Active.NoActive;
	}

	private void refillActive(String res, Active a) {
		// TODO Auto-generated method stub
		Iterator<ActiveRecord> it = activeTable.iterator();
		ActiveRecord act = new ActiveRecord();
		while (it.hasNext()) {
			act = it.next();
			if (act.tag.equals(res))
				act.active = a;
		}

	}

	/**
	 * Generating head of my object-code based on ASM.
	 * 
	 * @author Marshall Lee
	 * @time 2018.12.18
	 */
	private void genHead() {
		obj.add("SSEG\tSEGMENT\tSTACK");
		obj.add("STK\tDB\t20 DUP (0)");
		obj.add("SSEG\tENDS");
		obj.add("DSEG\tSEGMENT");
		genData();
		obj.add("DSEG\tENDS");
		obj.add("CSEG\tSEGMENT");
		obj.add("\tASSUME\tCS:CSEG, DS:DSEG");
		obj.add("\tASSUME\tSS:SSEG");
		obj.add("START:\tMOV\tAX, DSEG");
		obj.add("\tMOV\tDS, AX");
		obj.add("\tMOV\tAX, SSEG");
		obj.add("\tMOV\tSS, AX");
		obj.add("\tMOV\tSP, SIZE STK");

	}

	/**
	 * Generating Data definition areas.
	 * 
	 * @author Marshall Lee
	 */
	private void genData() {
		ActiveRecord act = new ActiveRecord();
		for (int i = 0; i < activeTable.size() - 1; i++) {
			act = activeTable.get(i);
			obj.add(act.tag + "\tDB\t0");
		}
	}

	private boolean isInteger(String tag) {
		for (int i = 0; i < tag.length(); i++) {
			if (!Character.isDigit(tag.charAt(i)))
				;
			return false;
		}
		return true;
	}

	private boolean isFloat(String tag) {
		for (int i = 0; i < tag.length(); i++) {
			if (!(Character.isDigit(tag.charAt(i)) || tag.charAt(i) == '.'))
				return false;
		}
		return true;
	}

	/**
	 * Get the address of the USERVAR or TEMPVAR
	 * 
	 * @param tag
	 * @return
	 */
	private int getAddress(String tag) {
		for (int i = 0; i < activeTable.size(); i++) {
			if (activeTable.get(i).tag.equals(tag))
				return i;
		}
		return 0;
	}

	/**
	 * Load the memory number into AX
	 * 
	 * @param tag
	 */
	private void genLD(String tag) {
		int address;
		if (isInteger(tag))
			obj.add("\tMOV\tAX, " + tag);
		if (isFloat(tag))
			obj.add("\tMOV\tAX, " + tag);
		else {
			address = getAddress(tag);
			obj.add("\tMOV\tAX, [" + String.format("%04X", address) + "]");
		}
	}

	/**
	 * Store AX into the object memory
	 * 
	 * @param tag
	 */
	private void genST(String tag) {
		int address = getAddress(tag);
		obj.add("\tMOV\t[" + String.format("%04X", address) + "], AX");
	}

	/**
	 * Using for MOV AX, [TAG] OR ADD AX, [TAG] OR MUL AX, [TAG]...
	 * 
	 * @param opt
	 */
	private void genOP(Operation opt, String tag) {
		int address;
		if (isInteger(tag))
			obj.add("\t" + opt + "\tAX, " + tag);
		if (isFloat(tag))
			obj.add("\t" + opt + "\tAX, " + tag);
		else {
			address = getAddress(tag);
			obj.add("\t" + opt + "\tAX, [" + String.format("%04X", address) + "]");
		}
	}

	/**
	 * Generate the body of code. Including the Assign-code and Expression-code for
	 * this compiler.
	 * 
	 * @author Marshall Lee
	 */
	private void genBody() {
		Quadruple q = new Quadruple();
		for (int i = 0; i < optimizer.qt.Quad.size(); i++) {
			q = optimizer.qt.Quad.get(i);
			if (q.opt != Operation.ASS) {
				if (RDL.tag == null) {
					genLD(q.op1);
					genOP(q.opt, q.op2);
				} else if (RDL.tag.equals(q.op1)) {
					if (q.active1 == Active.IsActive) {
						genST(q.op1);
						genOP(q.opt, q.op2);
					} else
						genOP(q.opt, q.op2);
				} else if (RDL.tag.equals(q.op2) && q.opt == Operation.ADD || q.opt == Operation.MUL) {
					if (q.active2 == Active.IsActive) {
						genST(q.op2);
						genOP(q.opt, q.op1);
					} else
						genOP(q.opt, q.op1);
				} else {
					if (RDL.active == Active.IsActive) {
						genST(RDL.tag);
						genLD(q.op1);
						genOP(q.opt, q.op2);
					}
				}
				RDL = new ActiveRecord(q.res, q.activeR);
			}

			else {
				if (RDL.tag == null)
					genLD(q.op1);
				else if (RDL.tag != null && RDL.tag.equals(q.op1)) {
					if (q.active1 == Active.IsActive)
						genST(q.op1);
				} else {
					if (RDL.active == Active.IsActive) {
						genST(RDL.tag);
						genLD(q.op1);
					} else
						genLD(q.op1);
				}

				RDL = new ActiveRecord(q.res, q.activeR);
			}

			if (RDL.active == Active.IsActive)
				genST(RDL.tag);

		}

	}

	private void genTail() {
		obj.add("\tMOV\tAX, 4C00H");
		obj.add("\tINT\t21H");
		obj.add("CSEG\tENDS");
		obj.add("\tEND\tSTART");
	}

	public void genCode() {
		genHead();
		genBody();
		genTail();
	}

	public void showCode() {
		System.out.println("Generating Code...");
		for (int i = 0; i < obj.size(); i++)
			System.out.println(obj.get(i));
	}

	public static void main(String[] args) {
		Generator generator = new Generator();
		generator.optimizer.build();
		generator.optimizer.getOptimizedQT();
		generator.fillActive();
		generator.genCode();
		generator.showCode();
		;

	}

}
