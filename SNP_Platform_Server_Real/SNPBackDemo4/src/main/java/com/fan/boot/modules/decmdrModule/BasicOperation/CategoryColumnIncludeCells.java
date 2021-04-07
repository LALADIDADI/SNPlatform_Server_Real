package com.fan.boot.modules.decmdrModule.BasicOperation;


import com.fan.boot.modules.decmdrModule.Data.CategoryCell;
import com.fan.boot.modules.decmdrModule.Data.CategoryColumn;
import com.fan.boot.modules.decmdrModule.Data.Cell;

public class CategoryColumnIncludeCells extends CategoryColumn implements ColumnIncludeCells {
    public Cell[] cell = null;

    public CategoryColumnIncludeCells(String[] values, String heading) {
        super(values, heading);
    }

    public CategoryColumnIncludeCells(Cell[] cells, String heading) {
        super(heading);
        this.setCells(cells);
    }

    public void setCells(Cell[] cell) {
        if (cell == null) {
            throw new IllegalArgumentException("CategoryColumnIncludeCells.setCells: null");
        } else {
            this.cell = (Cell[])cell.clone();
            this.m_columnInformation = new String[this.cell.length];

            for(int i = 0; i < this.cell.length; ++i) {
                ((String[])this.m_columnInformation)[i] = ((CategoryCell)this.cell[i]).getValue();
            }

        }
    }

    public Cell[] getCells() {
        return this.cell != null ? this.cell : null;
    }

    public Cell getCells(int index) {
        return this.cell[index] != null ? this.cell[index] : null;
    }

    public String getValue(int index) {
        return (CategoryCell)this.cell[index] != null ? ((CategoryCell)this.cell[index]).getValue() : null;
    }

    public boolean getBooleanValue(int index) {
        return !((CategoryCell)this.cell[index]).getValue().equals("0.0") && !((CategoryCell)this.cell[index]).getValue().equals("0");
    }
}
