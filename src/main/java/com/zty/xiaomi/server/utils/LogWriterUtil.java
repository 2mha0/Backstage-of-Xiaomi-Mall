package com.zty.xiaomi.server.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriterUtil {
    /**
     * 将content写入到filePath文件中
     * @param content
     * @param filePath
     */
    public void writeLog(String content,String filePath){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,true));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
