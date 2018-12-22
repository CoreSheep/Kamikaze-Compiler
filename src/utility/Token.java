package utility;

/**
 * 定义Token
 * 
 * @author Marshall
 *
 */
public class Token {
	// 单词种别码
	/**
	 * code for identifier, constant, keyword, all kinds of operators and ending
	 * identifier : 1 Integer : 2 Real : 3 keyword : 4 ~ 25 ending : 0
	 * 
	 * @total 26 kinds of code
	 * @author Marshall Lee
	 *
	 */
	public enum Code {
		End, IDENTIFIER, CONST_INTEGER, CONST_FLOAT, MAIN,
		// code for id, constant, main
		INT, FLOAT, IF, RETURN, COMMA, SEMI, ASSIGN, PLUS, MINUS, STAR, DIV,
		// code for operator
		LP, RP, LC, RC, GT, LT, GE, LE, EQ, NE
		// LP -> '(' RP -> ')' LC -> '{' RC -> '}'
	};

	public Code code; // 种别码
	public int value; // 指针
	public String word;

	public Token() {
		code = Code.End;
		value = -1;
		word = "";
	}

	public Token(Code code, int value, String word) {
		this.code = code;
		this.value = value;
		this.word = word;
	}

	public Token(Token t) {
		code = t.code;
		value = t.value;
		word = t.word;
	}

	/**
	 * Override toString method. 打印 token
	 */
	public String toString() {
		System.out.println("code: " + code);
		System.out.println("value: " + value);
		System.out.println("word: " + word);
		return "";

	}

}
