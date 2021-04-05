package com.fan.boot.modules.cmdrModule.Method;

import com.fan.boot.modules.cmdrModule.BasicOperation.CategoryColumnIncludeCells;
import com.fan.boot.modules.cmdrModule.Data.CategoryColumn;
import com.fan.boot.modules.cmdrModule.Data.Cell;

public class ColumnTools {
    public ColumnTools() {
    }

    public CategoryColumn NumberToCategory(Object[] Data, String heading) {
        return this.CreateCategoryColumn(Data, heading);
    }

    public CategoryColumn CreateCategoryColumn(Object[] Data, String heading) {
        String[] CategoryData = new String[Data.length];

        for(int k = 0; k < Data.length; ++k) {
            CategoryData[k] = Data[k] != null ? Data[k].toString() : "";
        }

        return new CategoryColumn(CategoryData, heading);
    }

    public CategoryColumnIncludeCells CreateCategoryColumnIncludeCells(Cell[] cells, String heading) {
        return new CategoryColumnIncludeCells(cells, heading);
    }

    public boolean isNumeric(Object[] column) {
        return false;
    }
}
