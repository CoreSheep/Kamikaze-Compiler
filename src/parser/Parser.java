package parser;

import java.util.Stack;

import kamikazeException.exception;
import lexer.Lexer;
import utility.EnumType.Typ;
import utility.Quadruple;
import utility.Quadruple.Operation;
import utility.QuadrupleTable;
import utility.SymbolRecord;
import utility.Token;
import utility.Token.Code;

public class Parser {

	private Token token; // token & lex
	private Token preToken; // private Token tmpToken;
	private Token sucToken;
	private SymbolRecord symbol;
	private Quadruple q;
	private Stack<Token> sem;
	private int tempTokenCount;

	public Lexer lex;
	public QuadrupleTable Quad;

	/**
	 * Parser Constructor
	 */
	public Parser() {

		token = new Token();
		// tmpToken = new Token();
		preToken = new Token();
		sucToken = new Token();
		lex = new Lexer();
		Quad = new QuadrupleTable();
		symbol = new SymbolRecord();
		q = new Quadruple();
		sem = new Stack<Token>();
		tempTokenCount = 0;

	}

	/**
	 * parser entry
	 */

	public void out(Token t) {
		System.out.println(t);
	}

	public void error(String errMessage) {
		System.out.println(errMessage);
	}

	/**
	 * Parsing entry
	 * 
	 * @throws exception
	 * 
	 */
	public void Compiler(String filename) {
		// open file
		if (!lex.readFile(filename))
			return; // cannot open this file
		lex.readAllTokens();
		token = lex.next();
		PG();
		if (token.code != Code.End)
			error("Compile unsuccessfully.");
		else
			System.out.println("Parsing successfully.");

	}

	/**
	 * Program -> main Block PG -> main BK
	 */
	private void PG() {

		if (token.code == Code.MAIN) {
			token = lex.next();
			BK();
		} else
			error("NoMainException.");

	}

	/**
	 * Block: Block -> { VaribleDeclare ; Statement ; } BK -> { VD ; ST ;}
	 */
	private void BK() {
		// TODO Auto-generated method stub
		if (token.code == Code.LC) {
			token = lex.next();
			VD();
			if (token.code == Code.SEMI) {
				token = lex.next();
				ST();
				// System.out.println("\nafter ST:" + token);
				if (token.code == Code.SEMI) {
					token = lex.next();
					if (token.code == Code.RC)
						token = lex.next();
					else
						error("Requiring a Right-Curly-Brace.");
				} else
					error("Requiring a semicolon.");
			} else
				error("Requiring a semicolon.");

		} else
			error("Please add an left curly brace.");

	}

	/**
	 * VaribleDeclare -> Type IdentifierTable; VD -> TP IT;
	 */
	private void VD() {
		// TODO Auto-generated method stub
		TP();
		IT();
		sucToken = lex.getSucToken();
		while (token.code == Code.SEMI && (sucToken.code == Code.INT || sucToken.code == Code.FLOAT)) {
			token = lex.next();
			VD();
		}
	}

	/**
	 * IdentifierTable -> DeclareList {; Definition} ; IT -> ID { , ID }
	 */
	private void IT() {
		// TODO Auto-generated method stub
		ID();
		while (token.code == Code.COMMA) {
			token = lex.next();
			ID();
		}

	}

	/**
	 * IdentifierDefinition -> id IdentifierAssign ID -> id IS
	 */
	private void ID() {
		// TODO Auto-generated method stub
		if (token.code == Code.IDENTIFIER) {
			// add semantic action
			lex.symbolTable.writeType(token.value, symbol.typ());
			token = lex.next();
			IS();
		} else
			error("Requiring an identifier.");

	}

	/**
	 * IdentifierAssign -> = Expression IS -> = E
	 */
	private void IS() {
		// TODO Auto-generated method stub
		if (token.code == Code.ASSIGN) {
			preToken = lex.getPreToken();
			token = lex.next();
			E();
			ASSIGN(preToken);

			// assignAction();
		} else
			; // null statement
	}

	/**
	 * Statement -> Assignment { : Assignment } ST -> AS {, AS}
	 */
	private void ST() {
		// TODO Auto-generated method stub
		AS();
		sucToken = lex.getSucToken();
		while (token.code == Code.SEMI & sucToken.code == Code.IDENTIFIER) {
			token = lex.next();
			AS();
			sucToken = lex.getSucToken();

		}

	}

	/**
	 * Assignment -> id = Expression AS -> id = E
	 */
	private void AS() {
		if (token.code == Code.IDENTIFIER) {
			preToken = token;
			token = lex.next();
			if (token.code == Code.ASSIGN) {
				token = lex.next();
				E();
				ASSIGN(preToken);

			} else
				error("Requiring an assignment.");

		} else
			error("Requiring a leftValue.");
	}

	/**
	 * ASSIGN Statement Assign-action for quadruple.
	 * 
	 * @param idToken
	 */

	private void ASSIGN(Token idToken) {
		// TODO Auto-generated method stub
		Token tmpToken = new Token();
		tmpToken = sem.pop();
		q = new Quadruple(Operation.ASS, tmpToken.word, "", idToken.word);
		// sem.push(idToken);
		Quad.Quad.add(q);
	}

	/**
	 * Type statement Type -> int | float TP -> int | float
	 */
	private void TP() {
		if (token.code == Code.INT) {
			token = lex.next();
			symbol.setType(Typ.INTEGER);
		} else if (token.code == Code.FLOAT) {
			token = lex.next();
			symbol.setType(Typ.FLOAT);
		} else
			error("Requiring a type.");
	}

	/**
	 * Expression Statement E -> T { [+|-] T }
	 */
	private void E() {
		Operation opt;
		T();
		while (token.code == Code.PLUS || token.code == Code.MINUS) {

			if (token.code == Code.PLUS)
				opt = Operation.ADD;
			else if (token.code == Code.MINUS)
				opt = Operation.SUB;
			else
				opt = Operation.NULL;

			token = lex.next();
			T();
			GEQ(opt);
		}
	}

	private String getTmpName() {
		return "t" + tempTokenCount++;
	}

	/**
	 * GEQ Statement Expression Quadruple Constructing.
	 * 
	 * @param operation
	 */
	private void GEQ(Operation operation) {
		// TODO Auto-generated method stub
		Token tmpToken = new Token();
		Operation opt = operation;
		String op1 = "", op2 = "", res = "";
		String tempName = "";

		Token t1 = new Token();
		Token t2 = new Token();

		// fetch top token
		if (!sem.isEmpty()) {
			t1 = sem.pop();
			// System.out.println("pop " + t1.word + " out sem-stack...");
		} else
			error("SEM stack is empty.");

		// fetch another token
		if (!sem.isEmpty()) {
			t2 = sem.pop();
			// System.out.println("pop " + t2.word + " out sem-stack...");
		} else
			error("SEM stack is empty.");

		op2 = t1.word;
		op1 = t2.word;

		/**
		 * GEQ -- two operands are constants.
		 */
		tempName = getTmpName();
		tmpToken.code = Code.IDENTIFIER;
		tmpToken.value = -1;
		tmpToken.word = tempName;
		res = tempName;
		sem.push(tmpToken);
		// System.out.println("push " + tmpToken.word + " into sem-stack...");
		q = new Quadruple(opt, op1, op2, res);
		Quad.Quad.add(q);

	}

	/**
	 * Term Statement T -> F { [*|/] F }
	 */
	private void T() {
		Operation opt;
		F();

		while (token.code == Code.STAR || token.code == Code.DIV) {

			if (token.code == Code.STAR)
				opt = Operation.MUL;
			else if (token.code == Code.DIV)
				opt = Operation.DIV;
			else
				opt = Operation.NULL;

			token = lex.next();
			F();
			GEQ(opt);
		}
	}

	/**
	 * Factor Statement F -> id | const_int | const_float
	 */
	private void F() {
		if (token.code == Code.IDENTIFIER || token.code == Code.CONST_INTEGER || token.code == Code.CONST_FLOAT) {
			// System.out.println("push " + token.word + " into sem-stack...");
			sem.push(token);
			token = lex.next();
		}

		else if (token.code == Code.LP) {
			token = lex.next();
			E();

			if (token.code == Code.RP)
				token = lex.next();
			else
				error("Requiring a right parenthese.");
		} else
			error("No Factor in Expression.");
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * Parser parse = new Parser(); String filename =
	 * "H:\\Java Workplace Marshall\\Kamikaze\\test1.txt"; parse.Compiler(filename);
	 * // // parse.lex.showList(Lexer.list); // parse.lex.symbolTable.print(); // //
	 * parse.Quad.showQuadruple();
	 * 
	 * // System.out.println(Lexer.list); }
	 * 
	 * /** µÝ¹éÏÂ½µ×Ó³ÌÐò
	 */

}