package com.fan.boot.modules.cmdrModule.BasicOperation;

import com.fan.boot.modules.cmdrModule.Data.Cell;

public interface ColumnIncludeCells {
    void setCells(Cell[] var1);

    Cell[] getCells();

    Cell getCells(int var1);
}
