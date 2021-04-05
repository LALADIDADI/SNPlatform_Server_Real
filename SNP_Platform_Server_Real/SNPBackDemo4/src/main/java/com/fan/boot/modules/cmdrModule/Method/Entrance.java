package com.fan.boot.modules.cmdrModule.Method;
import com.fan.boot.modules.cmdrModule.UI.Preprocess;
import com.fan.boot.modules.cmdrModule.UI.UserSetParameters;

public class Entrance {
    private final int DEFAULT_VALUE = -1;

    public Entrance(String dataPath, String resDataPath, int seed, int populationSize,
                    int maxGeneration, double mutationFactor, double CRFactor,
                    int orderOfSNPInteraction) throws Exception {
        // 算法所需参数
        String dataName = "";
        UserSetParameters parameters = new UserSetParameters();
        boolean flag = true;

        // 设置参数
        parameters.setMaxGeneration(maxGeneration);
        parameters.setMutationFactor(mutationFactor);
        parameters.setOrder(orderOfSNPInteraction);
        parameters.setSwarmSize(populationSize);
        parameters.setRecombinationCRfactor(CRFactor);
        parameters.setSeed(seed);
        parameters.setResDataPath(resDataPath);

        dataName = dataPath;

        // 开始进行预处理
        new Preprocess(dataName, "Class", parameters);

    }


    public static void main(String[] args) {
        try {
            String s = "D:\\example\\Example_data.txt";
            String res = "D:\\resData.txt";
            new Entrance(s,res, 1, 100, 300, 0.5, 0.5, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 获得程序当前路径
        // System.out.println(System.getProperty("user.dir"));

    }
}
