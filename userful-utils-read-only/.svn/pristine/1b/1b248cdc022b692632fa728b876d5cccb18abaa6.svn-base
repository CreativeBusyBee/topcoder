package com.utils.calculate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CalculateLines {

    public static int totalLines = 0;
    
    public int CalFileLines(File file, int lines) throws Exception {

        if (file.exists()) {
            // is file
            if (file.isFile()) {
                int fileLines = 0;
                String filePath = file.getAbsolutePath();
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                while (reader.readLine() != null) {
                    fileLines++;
                }
                lines += fileLines;
                System.out.println(file.getName() + " : " + fileLines);
                
            } else if (file.isDirectory()) {
                System.out.println();
                System.out.println("Under Directory : " + file.getName());
                System.out.println();
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {

                    CalFileLines(files[i], lines);
                }
            }
            return this.totalLines;
        }
        else
        {
            System.out.println("File does not exist!");
            return 0;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        
        String folderPath = "D:/workspace/weibo4j/src";
        File folder = new File(folderPath);
        
        CalculateLines cal = new CalculateLines();
        int lines = cal.CalFileLines(folder, CalculateLines.totalLines);
        
        System.out.println("Total lines: " + lines);

    }
}
