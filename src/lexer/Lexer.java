package lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utility.CategoryType.Category;
import utility.EnumType.Typ;
import utility.SymbolTable;
import utility.Token;
import utility.Token.Code;

public class Lexer {

	/**
	 * Top class MarshallLexer
	 * 
	 * @author Marshall Lee
	 * @Time 10/3/2018
	 */

	// keywords
	private String keys[] = { "main", "int", "float", "if", "return", ",", ";", "=", "+", "-", "*", "/", "(", ")", "{",
			"}", ">", "<", ">=", "<=", "==", "!=" }; // keywords and delimiters
	// code generating
	private Code cod[] = { Code.MAIN, Code.INT, Code.FLOAT, Code.IF, Code.RETURN,
			// code for id, constant, main
			Code.COMMA, Code.SEMI, Code.ASSIGN, Code.PLUS, Code.MINUS, Code.STAR, Code.DIV,
			// code for operator
			Code.LP, Code.RP, Code.LC, Code.RC, Code.GT, Code.LT, Code.GE, Code.LE, Code.EQ, Code.NE
			// LP -> '(' RP -> ')' LC -> '{' RC -> '}'
	};

	// flag used for identifying a number as int, float, or scientific number.
	public static enum Flag {
		intFlag, floatFlag, scienceFlag
	};

	/**
	 * Top class MarshallLexer
	 * 
	 * @author Marshall Lee£¨Àî¾Ã·ã£©
	 * @Time 10/3/2018
	 */
	private Hashtable<String, Code> keywords;
	private ArrayList<String> idHolder;

	// code && value
	private Token token;
	private String currentLine;
	private int intNum;
	private float floatNum;
	private int cursor = 0;
	// fileReader
	private FileReader fin;
	private BufferedReader fileBuffer;

	// this flag is useful in creating a token for different kinds of constants.
	private Flag numKind;
	private List<String> keyword;
	private List<String> singleOpt;
	private List<String> doubleOpt;

	// This list is used as the token-sequences holder.
	public static List<Token> list;
	public SymbolTable symbolTable;
	public ArrayList<Integer> intHolder;
	public ArrayList<Float> floatHolder;

	/**
	 * construct method creating keyword and operator lists
	 */
	public Lexer() {

		currentLine = "";
		token = new Token();
		symbolTable = new SymbolTable();
		keyword = new ArrayList<String>();
		idHolder = new ArrayList<String>();
		singleOpt = new ArrayList<String>();
		doubleOpt = new ArrayList<String>();
		intHolder = new ArrayList<Integer>();
		floatHolder = new ArrayList<Float>();
		list = new ArrayList<Token>();

		// 1.create a keyword-list
		String[] key = { "int", "float", "main" };
		for (int i = 0; i < key.length; i++)
			keyword.add(key[i]);

		// 2.create a single-operator-list
		String[] singleOP = { "+", "-", "*", "/", ">", "<", "=", ";", "(", ")", "{", "}", "'", "\"", ",", "!" };
		for (int i = 0; i < singleOP.length; i++) {
			singleOpt.add(singleOP[i]);
		}

		// 3.create a double-operator-list
		String[] doubleOP = { ">=", "<=", "==", "!=" };
		for (String opt : doubleOP)
			doubleOpt.add(opt);

		// keyword for code
		keywords = new Hashtable<String, Code>();
		for (int i = 0; i < keys.length; i++)
			keywords.put(keys[i], cod[i]);

	}

	/**
	 * get identifier or keyword subString
	 * 
	 * @param input
	 * @param i
	 * @return identifier string or keyword string
	 */
	public static String getSubstring(String input, int i) {
		int j = i;
		while (j < input.length()) {
			if (Character.isLetterOrDigit((input.charAt(j))))
				j++;
			else
				return input.substring(i, j);
		}

		return input.substring(i, j);
	}

	/**
	 * get digit-subString from input string
	 * 
	 * @param input
	 * @param i
	 * @return
	 */
	private String getDigitString(String input, int i) {

		// using regular expression to identifying an integer or float digit
		Pattern p = Pattern.compile("([+-]?[0-9]+((\\.[0-9]+)?([Ee][+-]?[0-9]+)?)?)");
		Matcher m = p.matcher(input.substring(i, input.length()));
		String strDigit = "";

		// 1. scientific number when containing 'e' or 'E'
		if (m.find()) {
			strDigit = m.group(1);
			if (strDigit.contains("e") || strDigit.contains("E"))
				numKind = Flag.scienceFlag;

			// 2. float number when containing Dot
			else if (strDigit.contains("."))
				numKind = Flag.floatFlag;

			// 3. integer
			else
				numKind = Flag.intFlag;

		}
		return strDigit;

	}

	/**
	 * isKeyword -- Is the string-key a keyword ?
	 * 
	 */
	private boolean isKeyword(String key) {
		return keyword.contains(key);
	}

	// store the identifier into idHolder
	private boolean isInIdHolder(String ident) {
		return idHolder.contains(ident);
	}

	// store the operator into the optHolder
	private boolean isOpt(char ch) {
		String opt = ch + "";
		return singleOpt.contains(opt);
	}

	// store the doubleOpt into the doubleOptHolder
	private boolean isDoubleOpt(String str) {
		return doubleOpt.contains(str);
	}

	// convert digitString into primitive-int type
	private int strToInt(String str) throws Exception {

		return Integer.parseInt(str);
	}

	// convert digitString into primitive-float type
	private float strToFloat(String str) throws Exception {
		return Float.parseFloat(str);
	}

	/**
	 * Controller Part split a given string, then creating tokens for each word.
	 * 
	 * @param String input
	 * @return List storing token-sequences
	 * @author Marshall Lee
	 * @throws Exception
	 */
	private void lex(String input) {
		// List<token> list = new ArrayList<token>();
		char ch, nextChar; // local variable
		int i; // char location
		String subStr = ""; // local variable

		for (i = 0; i < input.length();) { // main cycle for each line

			ch = input.charAt(i);

			if (i == input.length() - 1)
				nextChar = '#';
			else
				nextChar = input.charAt(i + 1);

			// 1.keyword or identifier
			if (Character.isLetter(ch)) {
				subStr = getSubstring(input, i);
				i += subStr.length();
				// 1.1 keyword
				if (isKeyword(subStr)) {
					token.code = getCode(subStr);
					list.add(new Token(token.code, -1, subStr));
				}
				// create a token for such keyword immediately

				// 1.2 identifier
				else {
					if (!isInIdHolder(subStr)) {
						idHolder.add(subStr);
						symbolTable.writeSymbol(subStr, Typ.NULL, Category.Variable, -1);
					}
					token.value = idHolder.indexOf(subStr);
					list.add(new Token(Code.IDENTIFIER, token.value, subStr));
					// create a token for such identifier immediately

				}
			}

			// 3.skip whitespace then scan the source code string
			else if (Character.isWhitespace(ch))
				i++;

			// 4. is an operator?
			else if (isOpt(ch))

			{

				// 4.3 is a double-operator?
				if (isOpt(nextChar)) {
					subStr = input.substring(i, i + 2);
					// System.out.println(subStr);

					// is double-operator?
					if (isDoubleOpt(subStr)) {
						token.code = getCode(subStr);
						list.add(new Token(token.code, -1, subStr));
						// create a token for such double-operator

						i += 2;
					}

					// this case suitable for ";}" wrong double-operator.
					else {
						token.code = getCode(ch + "");
						list.add(new Token(token.code, -1, ch + ""));
						// create a token for such single operator
						i++;
					}
				}

				// The input char is single operator!
				else {
					token.code = getCode(ch + "");
					list.add(new Token(token.code, -1, ch + ""));
					i++;
				}
			}

			// 5. is a digit?
			else if (Character.isDigit(ch)) {

				subStr = getDigitString(input, i);
				// System.out.println(subStr+" -- Lex!");
				i += subStr.length();

				if (numKind == Flag.intFlag) {
					try {
						intNum = strToInt(subStr);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						error("NAN.");
						e.printStackTrace();
					}
					// System.out.println(num);
					if (!intHolder.contains(intNum))
						intHolder.add(Integer.valueOf(intNum));
					token.value = intHolder.indexOf(intNum);

					// create a token for such int constant.
					list.add(new Token(Code.CONST_INTEGER, token.value, subStr));
				}

				else if (numKind == Flag.floatFlag || numKind == Flag.scienceFlag) {
					// convert the digitString into float-type constant.

					try {
						floatNum = strToFloat(subStr);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!floatHolder.contains(floatNum))
						floatHolder.add(Float.valueOf(floatNum));
					token.value = floatHolder.indexOf(floatNum);
					// create a token for such float-type constant.
					list.add(new Token(Code.CONST_FLOAT, token.value, subStr));
				}

			}

			else // other types
			{
				System.out.println("Something with it.");
				i++;

				// System.out.println("Illeagal Operator!");

			}

		}

		// return list;
	}

	private void error(String errInfo) {
		// TODO Auto-generated method stub
		System.out.println(errInfo);

	}

	private Code getCode(String subStr) {
		// TODO Auto-generated method stub
		return keywords.containsKey(subStr) ? keywords.get(subStr) : null;
	}

	public boolean isConstToken(Token t) {
		return t.code == Code.CONST_INTEGER || t.code == Code.CONST_FLOAT;
	}

	/**
	 * show list -- display all the lists of the ids, constants, operators, strings
	 * and so on and on.
	 * 
	 * @author Marshall Lee
	 * @param res
	 * @time Monday, November 12, 2018
	 */
	// res is the token-holder.
	public void showList(List<Token> res) {
		int newLine = 0;
		// display tokens
		System.out.println("@DisplayToken:\n## Token Sequences:");
		for (Token t : res) {
			newLine++;
			System.out.print(t + "\t");
			if (newLine % 1 == 0)
				System.out.println();
		}

		// display identifier list
		System.out.println("\n@DisplayLists:\n## Identifier List:");
		if (idHolder.isEmpty())
			System.out.println("NULL");
		else
			for (String s : idHolder)
				System.out.println(s);

		// display int list
		System.out.println("\n\n## Int List:");
		if (intHolder.isEmpty())
			System.out.println("NULL");
		else
			for (Integer i : intHolder)
				System.out.print(i.intValue() + "\t");

		// display float list
		System.out.println("\n\n## Float List:");
		if (floatHolder.isEmpty())
			System.out.println("NULL");
		else
			for (Float f : floatHolder)
				System.out.print(f.floatValue() + "\t");
	}

	/**
	 * ReadBuffer reads the source code file content string, and lexer the file line
	 * by line, then create tokens immediately, then read the next line...
	 * 
	 * @author Marshall Lee
	 * @param filename
	 * @version 1.2
	 */

	public boolean readFile(String filename) {

		boolean isFileOpen = true;
		// try-with-resource can automatically close the file and buffer.
		try {

			fin = new FileReader(filename);
			fileBuffer = new BufferedReader(fin);

		} catch (Exception e)

		{
			isFileOpen = false;
			System.out.println("Cannot Open Such File!");
		}

		return isFileOpen;
	}

	public void readAllTokens() {
		try {
			while ((currentLine = fileBuffer.readLine()) != null) {
				// line=buf.readLine();
				lex(currentLine);
				// System.out.println(currentLine);
			}
			list.add(new Token(Code.End, -1, ""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot read a line.");
		}
	}

	public Token next() {
		if (cursor < list.size())
			return list.get(cursor++);
		else
			return new Token();

	}

	public Token getPreToken() {
		if (cursor >= 0)
			return list.get(cursor - 2);
		else
			return new Token();
	}

	public Token getSucToken() {
		if (cursor < list.size())
			return list.get(cursor);
		else
			return new Token();
	}
}
