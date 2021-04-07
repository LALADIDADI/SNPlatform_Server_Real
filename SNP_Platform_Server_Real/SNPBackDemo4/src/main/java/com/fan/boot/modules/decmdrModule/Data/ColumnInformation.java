package com.fan.boot.modules.decmdrModule.Data;

import com.fan.boot.modules.decmdrModule.UI.AbstractType;

import java.text.DecimalFormat;
import java.util.*;

public class ColumnInformation<T> {
    private ArrayList<Integer> m_missingValue_index = null;
    public T[] m_columnInformation = null;
    private String m_heading = "";
    private T[] m_distinct = null;
    private DecimalFormat m_df = new DecimalFormat("#");
    private AbstractType m_type = null;

    public ColumnInformation(T[] value, String heading, AbstractType type) {
        this.m_columnInformation = value.clone();
        this.m_heading = heading;
        this.m_type = type;
    }

    public ColumnInformation(String heading) {
        this.m_heading = heading;
    }

    public AbstractType getType() {
        return this.m_type;
    }

    public T[] getColumnValues() {
        return this.m_columnInformation;
    }

    public String getHeading() {
        return this.m_heading;
    }

    public int getLength() {
        if (this.m_columnInformation == null) {
            throw new IllegalArgumentException("The Array must not be null (Length of ColumnInformation)");
        } else if (this.m_columnInformation.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty (Length of ColumnInformation)");
        } else {
            return this.m_columnInformation.length;
        }
    }

    public void update(T[] value) {
        if (value == null) {
            throw new IllegalArgumentException("The Array must not be null (update of ColumnInformation)");
        } else if (value.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty (update of ColumnInformation)");
        } else {
            this.m_columnInformation = value.clone();
            this.getMissingValue();
        }
    }

    public ArrayList<Integer> getMissingValue() {
        this.m_missingValue_index = new ArrayList();

        for(int i = 0; i < this.m_columnInformation.length; ++i) {
            if (this.m_columnInformation[i].toString().length() <= 0) {
                this.m_missingValue_index.add(i);
            }
        }

        return this.m_missingValue_index;
    }

    public int getMissingValue_number() {
        return this.getMissingValue().size();
    }

    public String getMissingValue_NumberAndPercent() {
        int missing = this.getMissingValue_number();
        return missing + " (" + missing / this.getLength() * 100 + "%)";
    }

    public int getDistinct() {
        List<T> initiaList = Arrays.asList(this.m_columnInformation);
        Set<T> treesetList = new TreeSet(initiaList);
        // 这里被修改或
        this.m_distinct = (T[]) treesetList.toArray(new Object[0]);
        return this.m_distinct.length;
    }

    public String getUnique() {
        int occurrence = 0;
        int Unique = 0;
        this.getDistinct();

        for(int i = 0; i < this.m_distinct.length; ++i) {
            for(int j = 0; j < this.m_columnInformation.length; ++j) {
                if (this.m_distinct[i].equals(this.m_columnInformation[j])) {
                    ++occurrence;
                }
            }

            if (occurrence == 1) {
                ++Unique;
            }

            occurrence = 0;
        }

        return Unique + " (" + this.m_df.format((double)Unique / (double)this.getLength() * 100.0D) + "%)";
    }
}
