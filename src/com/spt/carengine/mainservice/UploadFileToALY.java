
package com.spt.carengine.mainservice;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.spt.carengine.define.Define;
import com.spt.carengine.log.LOG;
import com.spt.carengine.log.ReportTraceFile;
import com.spt.carengine.utils.UtilTools;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Rocky
 * @Time 2015年7月13日 下午5:59:27
 * @descrition 上传文件到阿里云服务器上的工具类
 */
public class UploadFileToALY {

    /**
     * 上传视频文件到阿里云
     * 
     * @param context 上下文对象
     * @param m_sDeviceid 设备的ID
     * @param sFilePath 需要上传文件的绝对路径
     * @param sFileName 需要上传的文件名称
     */
    public static void upLoadVideoToALY(final Context context,
            String m_sDeviceid, String sFilePath) {

        final String sFunc = "video";

        OSSService ossService = OSSServiceProvider.getService();

        OSSBucket bucket = ossService.getOssBucket(Define.ALIYUN_OSS_BUCKET);
        // 指明该Bucket的访问权限
        bucket.setBucketACL(AccessControlList.PRIVATE);
        // 指明该Bucket所在数据中心的域名或已经绑定Bucket的CNAME域名
        bucket.setBucketHostId(Define.ALIYUN_BUCKET_HOST);

        File file = new File(sFilePath);
        LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                "upload video file is "
                        + (file.exists() ? "exists!" : "not exists")
                        + ",file path is " + file.toString());
        if (!file.exists()) {
            return;
        }
        String sOssFile = m_sDeviceid + "/" + sFunc + "/"
                + UtilTools.getSpecialFromName(sFilePath);

        OSSFile bigfFile = ossService.getOssFile(bucket, sOssFile);

        try {
            bigfFile.setUploadFilePath(sFilePath, "raw/binary");
            bigfFile.ResumableUploadInBackground(new SaveCallback() {

                @Override
                public void onSuccess(String objectKey) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload video,[onSuccess2] - " + objectKey
                                    + " upload success! and send broadcast");
                    uploadFileSendBroadcast(context, sFunc, objectKey);
                }

                @Override
                public void onProgress(String objectKey, int byteCount,
                        int totalSize) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload video,[onProgress2] - current upload "
                                    + objectKey + " bytes: " + byteCount
                                    + " in total: " + totalSize);
                }

                @Override
                public void onFailure(String objectKey,
                        OSSException ossException) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload video,[onFailure2] - upload " + objectKey
                                    + " failed,,," + ossException.toString());
                    ossException.printStackTrace();
                    ossException.getException().printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // file.FileUpload(sFilePath,sOssFile);

    }

    /**
     * 上传图片到阿里云
     * 
     * @param context Server Context Object
     * @param m_sDeviceid Device ID
     * @param sFilePath android sdcard local photo file
     */
    public static void upLoadPhoto2ALY(final Context context,
            String m_sDeviceid, String sFilePath) {

        final String sFunc = "photo";

        OSSService ossService = null;

        OSSBucket bucket = null;

        ossService = OSSServiceProvider.getService();
        bucket = ossService.getOssBucket(Define.ALIYUN_OSS_BUCKET);

        bucket.setBucketACL(AccessControlList.PRIVATE); // 指明该Bucket的访问权限
        bucket.setBucketHostId("oss-cn-shenzhen.aliyuncs.com"); // 指明该Bucket所在数据中心的域名或已经绑定Bucket的CNAME域名

        File file = new File(sFilePath);
        LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                "upload photo file is "
                        + (file.exists() ? "exists!" : "not exists")
                        + ",file path is " + file.toString());
        if (!file.exists()) {
            return;
        }
        // Set oss file path
        String sOssFile = m_sDeviceid + "/" + sFunc + "/"
                + UtilTools.getSpecialFromName(sFilePath);

        OSSFile bigfFile = ossService.getOssFile(bucket, sOssFile);

        try {
            bigfFile.setUploadFilePath(sFilePath, "raw/binary");
            bigfFile.ResumableUploadInBackground(new SaveCallback() {

                @Override
                public void onSuccess(String objectKey) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload photo, [onSuccess] - " + objectKey
                                    + " upload success!");
                    uploadFileSendBroadcast(context, sFunc, objectKey);
                }

                @Override
                public void onProgress(String objectKey, int byteCount,
                        int totalSize) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload photo, [onProgress] - current upload "
                                    + objectKey + " bytes: " + byteCount
                                    + ", in total: " + totalSize);
                }

                @Override
                public void onFailure(String objectKey,
                        OSSException ossException) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload photo, [onFailure2] - upload " + objectKey
                                    + " failed!\n" + ossException.toString());
                    ossException.printStackTrace();
                    ossException.getException().printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // file.FileUpload(sFilePath,sOssFile);
    }

    /**
     * 上传文件
     * 
     * @param context
     * @param m_sDeviceid
     */
    public static void uploadDrivingTrace(final Context context,
            final String m_sDeviceid) {
        // 喜欢任贤齐的《浪花一朵朵》喜欢歌里的那种意境，当一辈子过去变成了老太婆和糟老头还可以恩爱的去海边看浪花是多么难得和浪漫，让人很向往。
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uploadFile = selectUploadTraceFile();
                if (!uploadFile.equals("")) {
                    // 有文件
                    uploadTraceFile(context, m_sDeviceid, uploadFile);

                } else {
                    // 没有文件就不上传，
                }
            }
        }).start();
    }

    /**
     * 上传行车轨迹 你的口才很好，说什么事都很有逻辑，思路清晰。而我初中都没有上完。
     * 
     * @param context 上下文对象
     * @param m_sDeviceid Device id (车载的编号)
     * @param sFilePath 需要上传的行车估计的文件
     */
    private static void uploadTraceFile(final Context context,
            final String m_sDeviceid, final String sFilePath) {
        final String sFunc = "Trace";
        OSSService ossService = OSSServiceProvider.getService();

        OSSBucket bucket = ossService.getOssBucket(Define.ALIYUN_OSS_BUCKET);
        bucket.setBucketACL(AccessControlList.PRIVATE); // 指明该Bucket的访问权限
        bucket.setBucketHostId("oss-cn-shenzhen.aliyuncs.com"); // 指明该Bucket所在数据中心的域名或已经绑定Bucket的CNAME域名

        File file = new File(sFilePath);
        LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                "upload photo file is "
                        + (file.exists() ? "exists!" : "not exists")
                        + ",file path is " + file.toString());
        if (!file.exists()) {
            return;
        }

        String sOssFile = m_sDeviceid + "/" + sFunc + "/"
                + UtilTools.getSpecialFromName(sFilePath);

        OSSFile bigfFile = ossService.getOssFile(bucket, sOssFile);

        try {
            bigfFile.setUploadFilePath(sFilePath, "raw/binary");
            bigfFile.ResumableUploadInBackground(new SaveCallback() {

                @Override
                public void onSuccess(String objectKey) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload Trace, [onSuccess] - " + objectKey
                                    + " upload success!");
                    uploadFileSendBroadcast(context, sFunc, objectKey);
                    deleteTraceFile(sFilePath);
                    uploadDrivingTrace(context, m_sDeviceid);
                }

                @Override
                public void onProgress(String objectKey, int byteCount,
                        int totalSize) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload Trace, [onProgress] - current upload "
                                    + objectKey + " bytes: " + byteCount
                                    + ", in total: " + totalSize);
                }

                @Override
                public void onFailure(String objectKey,
                        OSSException ossException) {
                    LOG.writeMsg(UploadFileToALY.class, LOG.MODE_UPLOAD_FILE,
                            "upload Trace, [onFailure2] - upload " + objectKey
                                    + " failed!\n" + ossException.toString());
                    ossException.printStackTrace();
                    ossException.getException().printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送广播
     * 
     * @param context
     * @param sFunc
     * @param objectKey
     */
    private static void uploadFileSendBroadcast(Context context, String sFunc,
            String objectKey) {
        Intent intent = new Intent();
        intent.setAction(Define.ACTION_UPLOADFILE_RESULT);
        intent.putExtra(sFunc, objectKey);
        context.sendOrderedBroadcast(intent, null);
    }

    /**
     * 删除文件
     * 
     * @param filePath
     */
    private static void deleteTraceFile(String filePath) {
        //
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 选择上传行车轨迹的文件
     * 
     * @return
     */
    private static String selectUploadTraceFile() {
        String uploadFile = "";//
        File dir = new File(Environment.getExternalStorageDirectory()
                + File.separator + ReportTraceFile.RECORD_WHEEL_PATH);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            int fileCount = files.length;

            if (fileCount == 0) {
                // 没有文件
                uploadFile = "";

            } else if (fileCount == 1) {
                // 正在写入文件，暂时不能上传。
                uploadFile = "";

            } else if (fileCount >= 2) {
                // 有两个以上的文件，上传递归文件。
                uploadFile = files[0].toString();
            }
        }
        return uploadFile;
    }

    // 201507141609.txt，
    // 201507141614.txt
    // 201507141619.txt
    // 201507141624.txt
    // 201507141629.txt
    // 201507141634.txt

}
