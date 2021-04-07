package com.fan.boot.modules.decmdrModule.Data;

import com.fan.boot.modules.decmdrModule.UI.AbstractType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nfoldData {
    private String[] m_heading = null;
    private AbstractType[] m_type = null;
    private int m_sampleSize = 0;
    private int m_seed = 0;
    private Map<String, ArrayList<Sample>> m_samplesInclude_in_nfold = null;

    public nfoldData() {
        this.m_samplesInclude_in_nfold = new HashMap();
    }

    public void setType(AbstractType[] type) {
        this.m_type = type;
    }

    public AbstractType[] getType() {
        return this.m_type;
    }

    public HeadingAndType<?>[] getHeadingAndType() {
        if (this.m_samplesInclude_in_nfold != null) {
            Object[] oo = this.m_samplesInclude_in_nfold.keySet().toArray();
            if (oo != null) {
                return ((Sample)((ArrayList)this.m_samplesInclude_in_nfold.get(oo[0])).get(0)).getHeadingAndType();
            }
        }

        return null;
    }

    public void setHeading(String[] heading) {
        this.m_heading = (String[])heading.clone();
    }

    public String[] getHeadings() {
        return this.m_heading;
    }

    public void setSampleSize(int size) {
        this.m_sampleSize = size;
    }

    public int getSampleSize() {
        return this.m_sampleSize;
    }

    public void setSeed(int seed) {
        this.m_seed = seed;
    }

    public int getSeed() {
        return this.m_seed;
    }

    public void addnfoldData(int n, List<Sample> samples) {
        ArrayList sa;
        if (this.m_samplesInclude_in_nfold != null) {
            if (this.m_samplesInclude_in_nfold.containsKey(String.valueOf(n))) {
                ((ArrayList)this.m_samplesInclude_in_nfold.get(String.valueOf(n))).addAll(samples);
            } else {
                sa = new ArrayList();
                sa.addAll(samples);
                this.m_samplesInclude_in_nfold.put(String.valueOf(n), sa);
            }
        } else {
            this.m_samplesInclude_in_nfold = new HashMap();
            sa = new ArrayList();
            sa.addAll(samples);
            this.m_samplesInclude_in_nfold.put(String.valueOf(n), sa);
        }

    }

    public ArrayList<Sample> getnfoldData(int n) {
        return this.m_samplesInclude_in_nfold != null ? (ArrayList)this.m_samplesInclude_in_nfold.get(String.valueOf(n)) : null;
    }

    public Map<String, ArrayList<Sample>> getnfoldData() {
        return this.m_samplesInclude_in_nfold;
    }

    public int getnfold() {
        return this.getnfoldData().keySet().size();
    }
}
