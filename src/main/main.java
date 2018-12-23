package main;

import generator.Generator;
import lexer.Lexer;

public class Main {
	public Generator gen;
	private String filename = "H:\\Java Workplace Marshall\\Kamikaze\\test1.txt";

	public Main() {
		gen = new Generator();
	}

	public void output(String information) {
		System.out.println(information);
	}

	/**
	 * display token sequences, all kinds of tables and lists
	 */
	public void testLexer() {
		output("\nLexer is gonna work...\n");
		gen.optimizer.parser.lex.readFile(filename);
		gen.optimizer.parser.lex.showList(Lexer.list);
		output("\n\nWonderful! Lexer is working successfully! ^-^  ^-^  ^-^ \n\n\n");

	}

	/**
	 * display the process of parsing, and Symbol Table System and Quadruple Table.
	 */
	public void testParser() {
		output("\nParser is gonna work...");
		output("\nPART ONE:  Filling Symbol Table");
		gen.optimizer.parser.lex.symbolTable.print();
		output("\nPART TWO:  Creating Quadruple Table");
		output("\n#Display Quadruple Table:");
		gen.optimizer.parser.Quad.showQuadruple();
		output("\nAwesome! Parser is working successfully! ^-^  ^-^  ^-^\n\n\n");
	}

	/**
	 * display DAG, and optimized Quadruple Table
	 */
	public void testOptimizer() {
		output("Optimizer is gonna work...\n");
		output("#Display DAG Table:");
		gen.optimizer.build();
		gen.optimizer.showDGATable();
		gen.optimizer.getOptimizedQT();
		output("\n#Display Optimized Quadruple Table:");
		gen.optimizer.qt.showQuadruple();
		output("\nPerfect! Optimizer is working successfully! ^-^  ^-^  ^-^\n\n\n");

	}

	/**
	 * display Object-code -- ASM
	 */
	public void testGenerator() {
		output("Generator is gonna work...\n");
		gen.optimizer.build();
		gen.optimizer.getOptimizedQT();
		gen.fillActive();
		gen.genCode();
		output("#Display Object-Code (ASM_CODE_X8086):");
		gen.showCode();
		output("\nGorgeous! Generator is working successfully! ^-^  ^-^  ^-^\n\n\n");

	}

	public static void main(String[] args) {
		Main test = new Main();
		test.testLexer();
		test.testParser();
		test.testOptimizer();
		test.testGenerator();

	}

}
