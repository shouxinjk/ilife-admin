package com.pcitech.iLife.util;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *上传FastDFS图片
 */
public class FastDFSUtils {
	
    private static final Logger logger = LoggerFactory.getLogger(FastDFSUtils.class);
    private static final String CONF_NAME = "fastdfs-client.properties";
    private StorageClient storageClient;
    private StorageServer storageServer;
    private TrackerClient trackerClient;
    private TrackerServer trackerServer;
    
    private static FastDFSUtils instance = null;
    
    public static FastDFSUtils getInstance() throws Exception {
    	if(instance == null) {
    		instance = new FastDFSUtils();
    		instance.initStorageClient();
    	}
    	return instance;
    }
    
    public String[] upload(byte[] data, String name, long size){
        String[] path = {};
        try {
            //扩展名, 获取扩展名, apach 下common包中已有公用方法.
            String extension = FilenameUtils.getExtension(name);
            //设置图片meta信息
            NameValuePair[] meta_list = new NameValuePair[3];
            meta_list[0] = new NameValuePair("filename", name);
            meta_list[1] = new NameValuePair("fileext", extension);
            meta_list[2] = new NameValuePair("filesize", String.valueOf(size));
            //上传且返回path
            path = storageClient.upload_file(data, extension, meta_list);
            for(String str:path)
            	logger.debug("result {}", str);
        } catch (Exception e) {
            logger.error("failed upload file.",e);
        }
        return path;
    }
    
    public int delete(String groupName, String fileId) {
        try {
            int result = storageClient.delete_file(groupName, fileId);
            return result;
        } catch (Exception e) {
        	logger.error("failed upload file.",e);
            return 0;
        } 
    }

    public void initStorageClient() throws Exception {
        ClientGlobal.initByProperties(CONF_NAME);
        trackerClient = new TrackerClient();
        trackerServer = trackerClient.getTrackerServer();
        storageServer = null;
        storageClient = new StorageClient(trackerServer, storageServer);
    }

    public void close() {
        if(storageClient != null){
            try {
               storageClient.close();
            }catch (Exception e){
                e.printStackTrace();
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
        storageServer = null;
        trackerServer = null;
        storageClient = null;
        trackerClient = null;
    }
}