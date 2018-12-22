package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import utility.Quadruple.Operation;

public class DAGNode {

	public enum DAGType {
		TEMPVAR, USERVAR, CONSTANT
	}

	public int id;
	public Operation opt;
	public String mainTag;
	public List<String> subTag;
	public int leftNode;
	public int rightNode;

	public DAGNode() {
		id = -1;
		opt = Operation.NULL;
		mainTag = "";
		subTag = new ArrayList<String>();
		leftNode = -1;
		rightNode = -1;

	}

	public DAGNode(int id, String mainTag) {
		this.id = id;
		this.mainTag = mainTag;
		opt = Operation.NULL;
		subTag = new ArrayList<String>();
		leftNode = -1;
		rightNode = -1;

	}

	public DAGNode(int id, Operation opt, String mTag, int left, int right) {

		this.id = id;
		this.opt = opt;
		this.mainTag = mTag;
		this.leftNode = left;
		this.rightNode = right;
		subTag = new ArrayList<String>();

	}

	public String toString() {
		System.out.print(this.id + "\t" + this.opt + "\t" + this.mainTag + "\t\t" + this.leftNode + "\t\t"
				+ this.rightNode + "\t\t");
		Iterator<String> it = subTag.iterator();
		while (it.hasNext())
			System.out.print(it.next() + "\t");
		System.out.println();

		return "";
	}

}
