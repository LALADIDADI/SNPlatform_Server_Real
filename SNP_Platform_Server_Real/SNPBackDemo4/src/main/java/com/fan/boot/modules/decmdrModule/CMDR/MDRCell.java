package com.fan.boot.modules.decmdrModule.CMDR;

public class MDRCell {
    private double caseNumber = 0.0D;
    private double controlNumber = 0.0D;
    private int totalNumberOfCase = 0;
    private int totalNumberOfControl = 0;

    public MDRCell(boolean caseOrcontrol, int totalNumberOfCase, int totalNumberOfControl) {
        if (caseOrcontrol) {
            ++this.caseNumber;
        } else {
            ++this.controlNumber;
        }

        this.totalNumberOfControl = totalNumberOfControl;
        this.totalNumberOfCase = totalNumberOfCase;
    }

    public void increaseNumberOfCaseOrControl(boolean caseOrcontrol) {
        if (caseOrcontrol) {
            ++this.caseNumber;
        } else {
            ++this.controlNumber;
        }

    }

    public void decreaseNumberOfCaseOrControl(boolean caseOrcontrol) {
        if (caseOrcontrol) {
            --this.caseNumber;
        } else {
            --this.controlNumber;
        }

    }

    public int getCaseNumber() {
        return (int)this.caseNumber;
    }

    public int getControlNumber() {
        return (int)this.controlNumber;
    }

    public boolean getCaseOrControl() {
        return this.caseNumber / (double)this.totalNumberOfCase / (this.controlNumber / (double)this.totalNumberOfControl) >= 1.0D;
    }
}
