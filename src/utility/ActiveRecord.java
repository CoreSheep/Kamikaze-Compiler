package utility;

import utility.Quadruple.Active;

/**
 * Active Record for active-table
 * 
 * @author Marshall Lee
 * @time 2018.12.18
 *
 */
public class ActiveRecord {
	public String tag;
	public Active active;

	public ActiveRecord() {
		tag = null;
		active = Active.NoActive;
	}

	public ActiveRecord(String tag, Active active) {
		this.tag = tag;
		this.active = active;
	}

	public String toString() {
		System.out.println(tag);
		return "";
	}
}
