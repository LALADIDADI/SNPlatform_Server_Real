package com.fan.boot.modules.cmdrModule.Method;

import com.fan.boot.modules.cmdrModule.Data.ColumnInformation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoadData {
    public LoadData() {
    }

    public class columnData {
        ArrayList<Object[]> m_data_transfered = new ArrayList();
        boolean[] m_columnType = null;

        public columnData() {
        }

        public ArrayList<ColumnInformation> getColumnData() {
            ArrayList<ColumnInformation> data_Column = new ArrayList();
            String[] heading = (String[])this.m_data_transfered.remove(0);
            Object[] Data = new Object[this.m_data_transfered.size()];
            ColumnTools columnTools = new ColumnTools();

            for(int i = 0; i < ((Object[])this.m_data_transfered.get(0)).length; ++i) {
                String head = heading[i];

                for(int j = 0; j < Data.length; ++j) {
                    Data[j] = ((Object[])this.m_data_transfered.get(j))[i];
                }

                data_Column.add(columnTools.CreateCategoryColumn(Data, head));
            }

            return data_Column;
        }
    }

    public class columnData2 {
        ArrayList<Object[]> m_data_transfered = new ArrayList();
        boolean[] m_columnType = null;

        public columnData2() {
        }

        public ArrayList<ColumnInformation<?>> getColumnData() {
            ArrayList<ColumnInformation<?>> data_Column = new ArrayList();
            String[] heading = (String[])this.m_data_transfered.remove(0);
            Object[] Data = new Object[this.m_data_transfered.size()];
            ColumnTools columnTools = new ColumnTools();

            for(int i = 0; i < ((Object[])this.m_data_transfered.get(0)).length; ++i) {
                String head = heading[i];

                for(int j = 0; j < Data.length; ++j) {
                    Data[j] = ((Object[])this.m_data_transfered.get(j))[i];
                }

                data_Column.add(columnTools.CreateCategoryColumn(Data, head));
            }

            return data_Column;
        }
    }



    public class readFile extends LoadData.columnData {
        public readFile(String fileName) {
            super();
            String strLine = "";

            try {
                FileReader fr = new FileReader(fileName);
                BufferedReader bfr = new BufferedReader(fr);

                while((strLine = bfr.readLine()) != null) {
                    if (strLine.contains(",")) {
                        this.m_data_transfered.add(strLine.split(","));
                    } else if (strLine.contains("\t")) {
                        this.m_data_transfered.add(strLine.split("\t"));
                    }
                }

                bfr.close();
                fr.close();
            } catch (IOException var6) {
                System.err.println(var6);
            }

        }
    }
}
