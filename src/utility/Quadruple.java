package utility;

public class Quadruple {
	public enum Operation {
		NULL, ADD, SUB, MUL, DIV, ASS
	};

	public enum Active {
		IsActive, NoActive
	}

	public Operation opt;
	public String op1;
	public String op2;
	public String res;
	public Active active1;
	public Active active2;
	public Active activeR;

	public Quadruple() {
		opt = Operation.NULL;
		op1 = "";
		op2 = "";
		res = "";
		active1 = Active.NoActive;
		active2 = Active.NoActive;
		activeR = Active.NoActive;

	}

	public Quadruple(Operation opt, String op1, String op2, String res) {
		this.opt = opt;
		this.op1 = op1;
		this.op2 = op2;
		this.res = res;
		active1 = Active.NoActive;
		active2 = Active.NoActive;
		activeR = Active.NoActive;
	}

	public String toString() {
		System.out.println("opt: " + opt);
		System.out.println("op1: " + op1);
		System.out.println("op2: " + op2);
		System.out.println("res: " + res);
		return "";

	}

}
