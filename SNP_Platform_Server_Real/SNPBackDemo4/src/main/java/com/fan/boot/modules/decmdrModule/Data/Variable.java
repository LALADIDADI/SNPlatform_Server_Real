package com.fan.boot.modules.decmdrModule.Data;

import com.fan.boot.modules.decmdrModule.UI.AbstractType;

public class Variable {
    private String m_name = null;
    private AbstractType m_type = null;

    public Variable() {
    }

    public Variable(String name) {
        this.setName(name);
    }

    public Variable(AbstractType type) {
        this.setType(type);
    }

    public Variable(String name, AbstractType type) {
        this.setName(name);
        this.setType(type);
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public void setType(AbstractType type) {
        this.m_type = type;
    }

    public String getName() {
        return this.m_name != null ? this.m_name : null;
    }

    public AbstractType getType() {
        return this.m_type != null ? this.m_type : null;
    }
}
