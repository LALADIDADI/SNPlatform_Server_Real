package com.fan.boot.modules.cmdrModule.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultModelResult extends BasicSaveUnit implements Serializable {
    public String m_resultName = "";
    public String[] m_heading = null;
    public Date m_time = null;
    public StringBuffer m_output = null;

    public DefaultModelResult() {
        this.m_time = new Date(System.currentTimeMillis());
        this.m_output = new StringBuffer();
    }

    public DefaultModelResult(String name) {
        this.m_time = new Date(System.currentTimeMillis());
        this.m_output = new StringBuffer();
        this.m_resultName = name;
    }

    public void appendResult(String record) {
        this.m_output.append(record + "\n");
    }

    public void setResultName(String name) {
        this.m_resultName = name;
    }

    public void setHeadings(String[] headings) {
        this.m_heading = headings;
    }

    public String getResultName() {
        return (new SimpleDateFormat("HH:mm:ss")).format(this.m_time.getTime()) + " - " + this.m_resultName;
    }

    public String[] getHeadings() {
        return this.m_heading;
    }

    public Date getTime() {
        return this.m_time;
    }

    public String getTimeforHHMMSS() {
        return (new SimpleDateFormat("HH:mm:ss")).format(this.m_time.getTime());
    }

    public StringBuffer getOutput() {
        return this.m_output;
    }
}
