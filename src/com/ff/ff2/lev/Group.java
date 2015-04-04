package com.ff.ff2.lev;

import com.ff.ff2.lib.IdManager;

public class Group {
	public int size;
	public char[][] cells;
	public int groupX;
	public int groupY;
	public int type;
	public char primeCellId;
	
	public Group() {
		this.size = Generator.GROUP_SIZE;		
		cells = new char[size][size];
		reset();
	}
	
	public Group(int groupX, int groupY) {
		this();
		this.groupX = groupX * size;
		this.groupY = groupY * size;
	}
	
	public Group(int groupX, int groupY, int type) {
		this(groupX, groupY);
		this.type = type;
	}

	
	public void setGroupXY(int x, int y) {
		this.groupX = x * size;
		this.groupY = y * size;
	}
	
	public void reset() {
		// fill all with empty cells
		type = Generator.GROUP_EMPTY;
		primeCellId = IdManager.EMPTY;
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				cells[x][y] = primeCellId;
			}
		}
	}
	
}