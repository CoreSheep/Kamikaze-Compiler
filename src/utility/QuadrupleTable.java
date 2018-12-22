package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuadrupleTable {
	public List<Quadruple> Quad;

	public QuadrupleTable() {
		Quad = new ArrayList<Quadruple>();
	}

	public void showQuadruple() {
		Iterator<Quadruple> it = Quad.iterator();
		while (it.hasNext())
			System.out.println(it.next() + "\n");
	}
}
