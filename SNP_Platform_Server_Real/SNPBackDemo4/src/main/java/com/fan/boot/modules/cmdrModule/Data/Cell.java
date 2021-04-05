package com.fan.boot.modules.cmdrModule.Data;

import com.fan.boot.modules.cmdrModule.UI.AbstractType;

public interface Cell {
    AbstractType getType();

    void setHeading(String var1);

    String getHeading();

    HeadingAndType<?> getHeadingAndType();
}
