package utility;

import utility.CategoryType.Category;
import utility.EnumType.Typ;

public class SymbolRecord {
	private String Name; // 名字
	private Typ Type; // 类型
	private Category Category; // 种类
	private int Address; // 地址，指向对应的函数表，常量表，活动记录

	SymbolRecord(String nam, Typ typ, Category cat, int addr) {
		Name = nam;
		Type = typ;
		Category = cat;
		Address = addr;
	}

	public SymbolRecord() {
		Name = "";
		Type = Typ.NULL;
		Category = utility.CategoryType.Category.Variable;
		Address = -1;
	}

	boolean equals(SymbolRecord symbol) {
		if (this.Name.equals(symbol.name()) && this.Type == symbol.typ() && this.Category == symbol.cat()
				&& this.Address == symbol.addr())
			return true;
		else
			return false;
	}

	public String name() {
		return this.Name;
	}

	public Typ typ() {
		return this.Type;
	}

	public Category cat() {
		return this.Category;
	}

	public int addr() {
		return this.Address;
	}

	public void setType(Typ type) {
		// TODO Auto-generated method stub
		this.Type = type;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public void setCat(Category cat) {
		this.Category = cat;
	}

	public void setAddr(int addr) {
		this.Address = addr;
	}

	public void showSymbol() {
		System.out.println("name: " + this.Name);
		System.out.println("type: " + this.Type);
		System.out.println("cat : " + this.Category);
		System.out.println("addr: " + this.Address);

	}

}
