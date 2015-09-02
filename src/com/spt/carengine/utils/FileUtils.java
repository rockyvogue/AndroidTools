/*
 * 文 件 名:  FileUtils.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月12日
 */

package com.spt.carengine.utils;

import java.io.File;
import java.util.ArrayList;

/**
 * 跟文件有关的操作
 * 
 * @author Heaven
 */
public class FileUtils {

    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
    }
    
    /**
     * 扫描指定目录中的指定类型的文件，并且存放到容器中
     * 
     * @param dir
     *            指定的目录
     * @param suffix
     *            文件的扩展名
     * @param container
     *            存放文件的容器
     */
    public static void scanDir(File dir, String suffix, ArrayList<File> container) {
        File[] files = dir.listFiles();
        if (files == null) {// 如果文件夹是空的，则不处理
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {// 是目录的话则再次进入该目录
                scanDir(file, suffix, container);
            } else {// 如果是文件
                if (file.getName().endsWith(suffix)) {// 符合要求的文件放入容器中
                    container.add(file);
                }
            }
        }

    }

}
