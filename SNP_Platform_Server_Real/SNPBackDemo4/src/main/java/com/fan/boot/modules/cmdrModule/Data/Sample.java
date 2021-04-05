package com.fan.boot.modules.cmdrModule.Data;

import com.fan.boot.modules.cmdrModule.UI.AbstractType;
import java.util.ArrayList;
import java.util.Iterator;

public class Sample {
    private int m_sampleID = 0;
    public ArrayList<Cell> cells = null;

    public Sample(int ID) {
        this.m_sampleID = ID;
        this.cells = new ArrayList();
    }

    public AbstractType[] getType() {
        AbstractType[] type = new AbstractType[this.cells.size()];

        for(int i = 0; i < this.cells.size(); ++i) {
            type[i] = ((Cell)this.cells.get(i)).getType();
        }

        return type;
    }

    public HeadingAndType<?>[] getHeadingAndType() {
        HeadingAndType[] titleAndType = new HeadingAndType[this.cells.size()];

        for(int i = 0; i < this.cells.size(); ++i) {
            titleAndType[i] = ((Cell)this.cells.get(i)).getHeadingAndType();
        }

        return titleAndType;
    }

    public void addCell(Cell cell) {
        this.cells.add(cell);
    }

    public void removeCell(int index) {
        this.cells.remove(index);
    }

    public void addCell(int index, Cell cell) {
        this.cells.add(index, cell);
    }

    public void removeAll() {
        this.cells = new ArrayList();
    }

    public ArrayList<Cell> getCells() {
        return this.cells;
    }

    public int getSampleID() {
        return this.m_sampleID;
    }

    public String[] getHeading() {
        if (this.cells == null) {
            return null;
        } else {
            String[] titles = new String[this.cells.size()];
            int i = 0;

            for(Iterator var4 = this.cells.iterator(); var4.hasNext(); ++i) {
                Cell cell = (Cell)var4.next();
                titles[i] = cell.getHeading();
            }

            return titles;
        }
    }
}
