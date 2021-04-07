package com.fan.boot.modules.decmdrModule.UI;

import com.fan.boot.modules.decmdrModule.Data.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Generate_nfoldData {
    private ArrayList<ColumnInformation> column = null;
    private OutcomeVariableTools outComeInformation = null;
    private Map<String, ArrayList<Sample>> Samples = null;
    private nfoldData nfolddata = null;
    private OutComeVariable m_outcome = null;

    public Generate_nfoldData() {
    }

    public void doGeneratenfoldData(ArrayList<ColumnInformation> column, OutComeVariable outcome, int seed) {
        this.column = column;
        this.m_outcome = outcome;
        this.countNumberOfType();
        this.generateSamples();
        this.randomSortSampleAccordingSeed(seed);
        this.generatenfoldData();
    }

    public nfoldData getnfoldData() {
        return this.nfolddata;
    }

    public ArrayList<ColumnInformation> getRankingDataSet() {
        return this.column;
    }

    private void countNumberOfType() {
        this.outComeInformation = new OutcomeVariableTools(this.m_outcome, "5-fold");
    }

    private void generateSamples() {
        this.Samples = new HashMap();
        int columnSize = ((ColumnInformation)this.column.get(0)).getColumnValues().length;
        int headingIndex = 0;

        for(Iterator var4 = this.column.iterator(); var4.hasNext(); ++headingIndex) {
            ColumnInformation columns = (ColumnInformation)var4.next();
            if (columns.getHeading().equals(this.outComeInformation.getHeading())) {
                break;
            }
        }

        for(int s = 0; s < columnSize; ++s) {
            if (!this.Samples.containsKey(((ColumnInformation)this.column.get(headingIndex)).getColumnValues()[s].toString())) {
                this.Samples.put(((ColumnInformation)this.column.get(headingIndex)).getColumnValues()[s].toString(), new ArrayList());
            }

            Sample sample = new Sample(s + 1);

            for(int i = 0; i < this.column.size(); ++i) {
                CategoryCell cell = new CategoryCell(((ColumnInformation)this.column.get(i)).getColumnValues()[s].toString(), ((CategoryColumn)this.column.get(i)).getCategoryLabel());
                cell.setHeading(((ColumnInformation)this.column.get(i)).getHeading());
                sample.addCell(cell);
            }

            ArrayList<Sample> sa = (ArrayList)this.Samples.get(((ColumnInformation)this.column.get(headingIndex)).getColumnValues()[s].toString());
            sa.add(sample);
            this.Samples.put(((ColumnInformation)this.column.get(headingIndex)).getColumnValues()[s].toString(), sa);
        }

    }

    private void randomSortSampleAccordingSeed(int seed) {
        Random r = new Random();
        r.setSeed((long)seed);

        for(int i = 0; i < this.Samples.keySet().size(); ++i) {
            String name = this.Samples.keySet().toArray()[i].toString();
            Collections.shuffle((List)this.Samples.get(name), r);
        }

    }

    private void generatenfoldData() {
        String name = this.Samples.keySet().toArray()[0].toString();
        this.nfolddata = new nfoldData();
        this.nfolddata.setSeed(1);
        this.nfolddata.setHeading(((Sample)((ArrayList)this.Samples.get(name)).get(0)).getHeading());
        this.nfolddata.setSampleSize(((ColumnInformation)this.column.get(0)).getLength());

        for(int i = 0; i < this.Samples.keySet().size(); ++i) {
            name = this.Samples.keySet().toArray()[i].toString();
            int region = this.outComeInformation.getNumberOfType(name);
            int n = 5;

            for(int j = 0; j < n - 1; ++j) {
                this.nfolddata.addnfoldData(j + 1, ((ArrayList)this.Samples.get(name)).subList(j * region, (j + 1) * region));
            }

            this.nfolddata.addnfoldData(n, ((ArrayList)this.Samples.get(name)).subList((n - 1) * region, ((ArrayList)this.Samples.get(name)).size()));
        }

    }
}
