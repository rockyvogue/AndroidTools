/*
 * 文 件 名:  AlbumListInfo.java
 * 版    权:  Shenzhen spt-tek. Copyright 2015-2025,  All rights reserved
 * 描    述:  <描述>
 * 作    者:  Heaven
 * 创建时间:  2015年8月11日
 */

package com.spt.carengine.album.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册实体
 * 
 * @author Heaven
 */
public class AlbumListInfo {
    public int month;
    public List<String> paths = new ArrayList<String>();
    public List<PhotoListInfo> subFilePaths = new ArrayList<PhotoListInfo>();
    public String title;
}
