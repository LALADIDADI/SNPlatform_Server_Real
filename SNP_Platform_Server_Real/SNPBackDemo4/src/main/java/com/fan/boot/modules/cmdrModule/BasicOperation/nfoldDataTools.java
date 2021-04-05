package com.fan.boot.modules.cmdrModule.BasicOperation;

import com.fan.boot.modules.cmdrModule.Data.BasicSaveUnit;
import com.fan.boot.modules.cmdrModule.Data.Cell;
import com.fan.boot.modules.cmdrModule.Data.IndependentAndDependentVariables;
import com.fan.boot.modules.cmdrModule.Data.Sample;
import com.fan.boot.modules.cmdrModule.Data.nfoldData;
import com.fan.boot.modules.cmdrModule.Method.ColumnTools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class nfoldDataTools extends BasicSaveUnit {
    Map<String, IndependentAndDependentVariables> DataColumn = new HashMap();
    public ArrayList<String> independentHeadings = null;

    public nfoldDataTools(nfoldData data) {
        this.setnfoldData(data);
    }

    public Map<String, ArrayList<Sample>> getSamplesData() {
        return this.getnfoldData().getnfoldData();
    }

    public ArrayList<Sample> getSamplesData(int n) {
        return this.getnfoldData().getnfoldData(n);
    }

    public ArrayList<String> getIndependentHeading() {
        return this.independentHeadings;
    }

    public void generateColumnData() {
        Iterator var2 = this.getSamplesData().entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, ArrayList<Sample>> Samples = (Entry)var2.next();
            this.DataColumn.put((String)Samples.getKey(), (new nfoldDataTools.transposesSamplesArrayToColumn((ArrayList)Samples.getValue())).getColumns());
        }

    }

    public Map<String, IndependentAndDependentVariables> getColumnData() {
        return this.DataColumn;
    }

    public class columnData {
        ArrayList<Sample> data_transfered = new ArrayList();
        boolean[] columnType = null;

        public columnData() {
        }

        public IndependentAndDependentVariables getColumnData() {
            nfoldDataTools.this.independentHeadings = new ArrayList();
            IndependentAndDependentVariables data_Column = new IndependentAndDependentVariables();
            String[] heading = ((Sample)this.data_transfered.get(0)).getHeading();
            Cell[] Data = new Cell[this.data_transfered.size()];
            ColumnTools columnTools = new ColumnTools();

            for(int i = 0; i < ((Sample)this.data_transfered.get(0)).getCells().size(); ++i) {
                String head = heading[i];

                for(int j = 0; j < Data.length; ++j) {
                    Data[j] = (Cell)((Sample)this.data_transfered.get(j)).getCells().get(i);
                }

                if (head.equals(nfoldDataTools.this.getOutComeVariable().getHeading())) {
                    data_Column.setDependentVariable(columnTools.CreateCategoryColumnIncludeCells(Data, head));
                } else {
                    nfoldDataTools.this.independentHeadings.add(head);
                    data_Column.addIndependentVariable(columnTools.CreateCategoryColumnIncludeCells(Data, head));
                }
            }

            return data_Column;
        }
    }

    public class transposesSamplesArrayToColumn extends nfoldDataTools.columnData {
        public transposesSamplesArrayToColumn(ArrayList<Sample> samples) {
            super();
            if (samples != null) {
                this.data_transfered = samples;
            } else {
                System.out.println("transposesSamplesArrayToColumn.samples: null");
            }

        }

        public IndependentAndDependentVariables getColumns() {
            return this.getColumnData();
        }
    }
}
