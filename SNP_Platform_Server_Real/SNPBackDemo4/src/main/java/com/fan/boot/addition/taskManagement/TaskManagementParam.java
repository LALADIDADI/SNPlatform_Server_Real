package com.fan.boot.addition.taskManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManagementParam {
    // 任务管理界面所需属性
    ArrayList<String> queryId = new ArrayList<String>();
    ArrayList<String> queryTime = new ArrayList<String>();
    ArrayList<String> methodName = new ArrayList<String>();
    ArrayList<String> runState = new ArrayList<String>();

    ArrayList<Object> objects = new ArrayList<Object>();


    // 在controller中用到的方法

    // 刷新任务控制界面
    public Map<String, String>[] getTask() {
        int length = this.queryId.size();
        Map[] maps = new Map[length];
        for(int i = 0; i < length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("number", queryId.get(i));
            map.put("date", queryTime.get(i));
            map.put("method", methodName.get(i));
            map.put("state", runState.get(i));
            maps[i] = map;
        }
        return maps;
    }


    // 增加任务
    public void addTask(String queryId, String queryTime, String method, String runState) {
        this.queryId.add(queryId);
        this.queryTime.add(queryTime);
        this.methodName.add(method);
        this.runState.add(runState);
    }
    // 删除任务
    public void deleteTask(int index) {
        queryId.remove(index);
        queryTime.remove(index);
        methodName.remove(index);
        runState.remove(index);
    }

    // 增加对象
    public void addObject(Object ob) {
        objects.add(ob);
    }
    // 删除对象
    public void deleteObject(int index) {
        objects.remove(index);
    }






}
