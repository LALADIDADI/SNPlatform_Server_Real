package com.fan.boot.modules.cmdrModule.Method;

import com.fan.boot.modules.cmdrModule.Data.ClassificationMethodResultModel;

public class CheckClassificationMethod {
    public CheckClassificationMethod() {
    }

    public ClassificationMethod getClassificationMethod(Object method) {
        return method instanceof ClassificationMethodResultModel ? new DECMDR((ClassificationMethodResultModel)method) : null;
    }
}
