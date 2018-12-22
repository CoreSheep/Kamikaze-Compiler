package utility;

import utility.Quadruple.Operation;

public class CaculateQuadruple {

	private Error err;
	public boolean resflag;
	public float result;

	public CaculateQuadruple() {
		err = new Error();
	}

	private boolean isFloat(String floatDigitString) {

		for (int i = 0; i < floatDigitString.length(); i++)
			if (floatDigitString.charAt(i) == '.')
				return true;
			else
				continue;
		return false;

	}

	public float cacul(Quadruple quad) {

		float f1 = 0, f2 = 0, res;
		int i1 = 0, i2 = 0;
		boolean op1, op2; // true is int, false is float
		if (isFloat(quad.op1)) {
			try {
				f1 = Float.parseFloat(quad.op1);
			} catch (Exception e) {
				err.error("Not a float number.");
				f1 = 0;
			}
			op1 = false;
		} else {
			try {
				i1 = Integer.parseInt(quad.op1);
			} catch (Exception e) {
				err.error("Not an integer number.");
				i1 = 0;
			}
			op1 = true;
		}

		if (isFloat(quad.op2)) {
			try {
				f2 = Float.parseFloat(quad.op2);
			} catch (Exception e) {
				err.error("Not a float number.");
				f2 = 0;
			}
			op2 = false;
		} else {
			try {
				i2 = Integer.parseInt(quad.op2);
			} catch (Exception e) {
				err.error("Not an integer number.");
				i2 = 0;
			}
			op2 = true;
		}

		if (op1 && op2) {
			res = caculate(quad.opt, i1, i2);
			resflag = true;
		} else if (op1 && !op2) {
			resflag = false;
			res = caculate(quad.opt, i1, f2);
		} else if (!op1 && op2) {
			resflag = false;
			res = caculate(quad.opt, f1, i2);
		}

		else {
			resflag = false;
			res = caculate(quad.opt, f1, f2);
		}

		result = res;
		return res;

	}

	private float caculate(Operation opt, float f1, float f2) {
		// TODO Auto-generated method stub
		float res;
		switch (opt) {
		case ADD:
			res = f1 + f2;
			break;
		case SUB:
			res = f1 - f2;
			break;
		case MUL:
			res = f1 * f2;
			break;
		case DIV:
			res = f1 / f2;
			break;
		case NULL:
			res = 0;
			err.error("No operation in caculating.");
		default:
			res = 0;
			break;
		}
		return res;
		/*
		 * lex.floatHolder.add(res); return lex.floatHolder.lastIndexOf(res);
		 */
	}

	private float caculate(Operation opt, float f1, int t2) {
		// TODO Auto-generated method stub
		float res;
		float tt2 = (float) t2;
		switch (opt) {
		case ADD:
			res = f1 + tt2;
			break;
		case SUB:
			res = f1 - tt2;
			break;
		case MUL:
			res = f1 * tt2;
			break;
		case DIV:
			res = f1 / tt2;
			break;
		case NULL:
			err.error("No operation in caculating.");
			return 0;
		default:
			res = 0;
			break;
		}
		return res;

		/*
		 * lex.floatHolder.add(res); return lex.floatHolder.lastIndexOf(res);
		 */
	}

	private float caculate(Operation opt, int t1, float f2) {
		// TODO Auto-generated method stub
		float res;
		float tt1 = (float) t1;
		switch (opt) {
		case ADD:
			res = tt1 + f2;
			break;
		case SUB:
			res = tt1 - f2;
			break;
		case MUL:
			res = tt1 * f2;
			break;
		case DIV:
			res = tt1 / f2;
			break;
		case NULL:
			err.error("No operation in caculating.");
			res = 0;
		default:
			res = 0;
			break;
		}

		return res;

		/*
		 * lex.floatHolder.add(res); return lex.floatHolder.lastIndexOf(res);
		 */
	}

	private int caculate(Operation opt, int t1, int t2) {
		// TODO Auto-generated method stub
		int res;
		switch (opt) {
		case ADD:
			res = t1 + t2;
			break;
		case SUB:
			res = t1 - t2;
			break;
		case MUL:
			res = t1 * t2;
			break;
		case DIV:
			res = t1 / t2;
			break;
		case NULL:
			err.error("No operation in caculating.");
			res = 0;
		default:
			res = 0;
			break;
		}
		return res;
		/*
		 * lex.intHolder.add(res); return lex.intHolder.lastIndexOf(res);
		 */
	}

}
