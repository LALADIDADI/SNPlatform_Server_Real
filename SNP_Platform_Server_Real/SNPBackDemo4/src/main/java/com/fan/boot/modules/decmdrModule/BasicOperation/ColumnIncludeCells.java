package com.fan.boot.modules.decmdrModule.BasicOperation;

import com.fan.boot.modules.decmdrModule.Data.Cell;

public interface ColumnIncludeCells {
    void setCells(Cell[] var1);

    Cell[] getCells();

    Cell getCells(int var1);
}
