package utility;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import utility.CategoryType.Category;
import utility.EnumType.Typ;;

public class SymbolTable {

	public LinkedList<SymbolRecord> symbolTable;
	protected Hashtable<SymbolRecord, Integer> map;

	public SymbolTable() {

		symbolTable = new LinkedList<SymbolRecord>();
		map = new Hashtable<SymbolRecord, Integer>();

	}

	public boolean contains(SymbolRecord symbol) {
		boolean isContains = false;
		Iterator<SymbolRecord> it = symbolTable.iterator();
		while (it.hasNext()) {
			isContains = it.next().equals(symbol);
			if (isContains)
				break;
		}
		return isContains;
	}

	public boolean hasSymbol(SymbolRecord symbol) {

		return contains(symbol);
	}

	public int getIndex(SymbolRecord symbol) {
		return map.get(symbol);
	}

	public SymbolRecord getValue(int index) {
		return symbolTable.get(index);
	}

	public boolean hasName(String name) {

		Iterator<SymbolRecord> it = symbolTable.iterator();
		while (it.hasNext()) {
			if (it.next().name().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * ²åÈë·ûºÅ±íÏî
	 * 
	 * @param nam
	 * @param typ
	 * @param cat
	 * @param addr
	 * @return int addr, the address of this symbol in symbolTable
	 */
	public int writeSymbol(String nam, Typ typ, Category cat, int addr) {
		SymbolRecord symbol = new SymbolRecord(nam, typ, cat, addr);
		symbolTable.add(symbol);
		Integer index = new Integer(symbolTable.indexOf(symbol));
		map.put(symbol, index);
		return index;
	}

	public void writeSymbol(SymbolRecord symbol) {
		if (!hasSymbol(symbol)) {
			symbolTable.add(symbol);
			Integer index = new Integer(symbolTable.indexOf(symbol));
			map.put(symbol, index);
		}
	}

	public void writeType(int index, Typ type) {

		symbolTable.get(index).setType(type);
	}

	public void writeCategory(int index, Category cat) {

		symbolTable.get(index).setCat(cat);

	}

	public void writeName(int index, String name) {
		symbolTable.get(index).setName(name);
	}

	public void writeAddress(int index, int addr) {

		symbolTable.get(index).setAddr(addr);
	}

	public LinkedList<SymbolRecord> getSymbolTable() {

		return symbolTable;

	}

	public void print() {
		// System.out.println("Name:\t\tType:\t\tCategory:\t\tAddress");

		Iterator<SymbolRecord> it = symbolTable.iterator();

		while (it.hasNext()) {
			it.next().showSymbol();
			System.out.println();
		}

	}

	/*
	 * public static void main(String[] args) { SymbolTable table = new
	 * SymbolTable(); // table.writeSymbol("int1", 0, Category.Variable, -1);
	 * table.writeSymbol("int2", 0, Category.Variable, -1); SymbolRecord symbol =
	 * new SymbolRecord("int1", 0, Category.Variable, -1);
	 * table.writeSymbol(symbol); System.out.println(table.hasSymbol(symbol)); int
	 * index = table.getIndex(symbol); symbol.showSymbol();
	 * table.getValue(0).showSymbol();
	 * 
	 * }
	 */

}
