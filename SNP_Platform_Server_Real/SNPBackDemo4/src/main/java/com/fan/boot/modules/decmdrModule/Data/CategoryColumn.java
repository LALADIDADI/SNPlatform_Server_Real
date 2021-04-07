package com.fan.boot.modules.decmdrModule.Data;

import com.fan.boot.modules.decmdrModule.UI.AbstractCategorical;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CategoryColumn extends ColumnInformation<String> {
    private Map<String, Integer> m_CategoryCount = null;

    public CategoryColumn(String[] values, String Heading) {
        super(values, Heading, new AbstractCategorical());
        this.getCategoryCountMap();
    }

    public CategoryColumn(String Heading) {
        super(Heading);
    }

    private void getCategoryCountMap() {
        this.m_CategoryCount = new HashMap();
        String[] var4;
        int var3 = (var4 = (String[])this.m_columnInformation).length;

        for(int var2 = 0; var2 < var3; ++var2) {
            String value = var4[var2];
            if (this.m_CategoryCount.containsKey(value)) {
                this.m_CategoryCount.put(value, (Integer)this.m_CategoryCount.get(value) + 1);
            } else {
                this.m_CategoryCount.put(value, 1);
            }
        }

    }

    public Set<String> getCategoryLabel() {
        return this.m_CategoryCount.keySet();
    }

    public Collection<Integer> getCategoryValue() {
        return this.m_CategoryCount.values();
    }

    public Map<String, Integer> getCategoryCount() {
        return this.m_CategoryCount;
    }

    public int getCategorySize() {
        return this.m_CategoryCount.keySet().size();
    }

    public AbstractCategorical getType() {
        return new AbstractCategorical();
    }
}
