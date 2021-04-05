package com.fan.boot.modules.cmdrModule.Data;

import com.fan.boot.modules.cmdrModule.UI.AbstractCategorical;
import java.util.Set;

public class CategoryCell implements Cell {
    private String m_Value = "";
    private String m_Heading = "";
    private boolean[] m_booleanCategory = null;

    public CategoryCell(String value, Set<String> categoryLabel) {
        this.m_Value = value;
        this.generateBooleanCategory(categoryLabel);
    }

    private void generateBooleanCategory(Set<String> categoryLabel) {
        this.m_booleanCategory = new boolean[categoryLabel.size()];

        for(int index = 0; index < this.m_booleanCategory.length; ++index) {
            if (this.m_Value.equals(categoryLabel.toArray()[index])) {
                this.m_booleanCategory[index] = true;
                break;
            }
        }

    }

    public boolean[] getBooleanCategory() {
        return this.m_booleanCategory;
    }

    public boolean getBooleanCategory(int index) {
        return this.m_booleanCategory[index];
    }

    public void setValue(String value) {
        this.m_Value = value;
    }

    public String getValue() {
        return this.m_Value;
    }

    public AbstractCategorical getType() {
        return new AbstractCategorical();
    }

    public String getHeading() {
        return this.m_Heading;
    }

    public void setHeading(String heading) {
        this.m_Heading = heading;
    }

    public HeadingAndType<AbstractCategorical> getHeadingAndType() {
        return new HeadingAndType(this.getHeading(), this.getType());
    }
}
