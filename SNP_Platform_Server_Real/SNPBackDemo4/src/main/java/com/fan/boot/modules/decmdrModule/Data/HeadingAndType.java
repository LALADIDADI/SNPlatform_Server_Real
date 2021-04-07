package com.fan.boot.modules.decmdrModule.Data;

public class HeadingAndType<T> {
    private String m_heading = "";
    private T m_type = null;

    public HeadingAndType(String heading, T type) {
        this.m_heading = heading;
        this.m_type = type;
    }

    public String getHeading() {
        return this.m_heading;
    }

    public T getType() {
        return this.m_type;
    }
}
