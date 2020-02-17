package com.github.binarywang.utils.qrcode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void main(String[] args) throws IOException {
        File writename = new File("F:\\data.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
        writename.createNewFile(); // 创建新文件
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        for(int i=0;i<10000;i++){
            String value = "";
            if(i < 10 ){
                value = "000" + i;
            } else if(i < 100 ){
                value = "00" + i;
            } else if(i < 1000 ){
                value = "0" + i;
            } else {
                value = "" + i;
            }
            out.write("dfcx88X7QQ2CBNT8TMJUJBPX9J4F7C3XFPTCZM87CHMJNDF7Z8BQUB23FFBN"+value+">>>>>>2422"+value+">>>>>>"+value+"-QM88-X7QQ-2CFX\r\n"); // \r\n即为换行
        }
        out.flush(); // 把缓存区内容压入文件
        out.close(); // 最后记得关闭文件
    }
}
