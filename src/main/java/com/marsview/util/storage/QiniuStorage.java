package com.marsview.util.storage;

import com.zhouzifei.tool.config.QiniuFileProperties;
import com.zhouzifei.tool.config.SimpleFsProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.listener.ProgressListener;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.service.FileUploader;
import org.springframework.web.multipart.MultipartFile;

public class QiniuStorage {

    /**
     * 上传文件
     *
     * @param simpleFsProperties 配置
     * @param file               附件
     * @return 文件地址，例如：https://oss.qiniu.com/tahh3b2tkkhe4n5r.txt
     */
    public static String uploadFile(SimpleFsProperties simpleFsProperties, MultipartFile file) {
        System.out.println(simpleFsProperties);
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void start(String s) {
                System.out.println("开始上传");
            }

            @Override
            public void process(int i, int i1) {
                System.out.println("i=" + i);
                System.out.println("i1=" + i1);
            }

            @Override
            public void end(VirtualFile virtualFile) {
                System.out.println("上传完成");
                System.out.println(virtualFile);

            }
        };
        QiniuFileProperties qiniuFileProperties = simpleFsProperties.getQiniu();
        System.out.println(qiniuFileProperties);

        String domainUrl = qiniuFileProperties.getDomainUrl();
        String accessKey = qiniuFileProperties.getAccessKey();
        String secretKey = qiniuFileProperties.getSecretKey();
        String endpoint = "";
        String region = qiniuFileProperties.getRegion();
        String bucketName = qiniuFileProperties.getBucketName();
        String storageType = StorageTypeConst.QINIUYUN.getStorageType();
        //(simpleFsProperties,progressListener,domainUrl,accessKey,secretKey,endpoint,region,bucketName,storageType);
        FileUploader uploader = FileUploader.builder()
                .simpleFsProperties(simpleFsProperties)
                .progressListener(progressListener)
                .domainUrl(domainUrl)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .endpoint(endpoint)
                .region(region)
                .bucketName(bucketName)
                .storageType(storageType)
                .build();

        ApiClient apiClient = uploader.execute();

        VirtualFile virtualFile = apiClient.uploadFile(file);
        System.out.println(virtualFile.getFullFilePath());
        System.out.println(virtualFile.getFileHash());

        return virtualFile.getFullFilePath();
    }

    /**
     * 上传文件，返回文件对象
     * @param simpleFsProperties
     * @param file
     * @return
     */
    public static VirtualFile uploadFileAndReturnFile(SimpleFsProperties simpleFsProperties, MultipartFile file) {
        System.out.println(simpleFsProperties);
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void start(String s) {
                System.out.println("开始上传");
            }

            @Override
            public void process(int i, int i1) {
                System.out.println("i=" + i);
                System.out.println("i1=" + i1);
            }

            @Override
            public void end(VirtualFile virtualFile) {
                System.out.println("上传完成");
                System.out.println(virtualFile);

            }
        };
        QiniuFileProperties qiniuFileProperties = simpleFsProperties.getQiniu();
        System.out.println(qiniuFileProperties);

        String domainUrl = qiniuFileProperties.getDomainUrl();
        String accessKey = qiniuFileProperties.getAccessKey();
        String secretKey = qiniuFileProperties.getSecretKey();
        String endpoint = "";
        String region = qiniuFileProperties.getRegion();
        String bucketName = qiniuFileProperties.getBucketName();
        String storageType = StorageTypeConst.QINIUYUN.getStorageType();
        //(simpleFsProperties,progressListener,domainUrl,accessKey,secretKey,endpoint,region,bucketName,storageType);
        FileUploader uploader = FileUploader.builder()
                .simpleFsProperties(simpleFsProperties)
                .progressListener(progressListener)
                .domainUrl(domainUrl)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .endpoint(endpoint)
                .region(region)
                .bucketName(bucketName)
                .storageType(storageType)
                .build();

        ApiClient apiClient = uploader.execute();

        VirtualFile virtualFile = apiClient.uploadFile(file);
        System.out.println(virtualFile.getFullFilePath());
        System.out.println(virtualFile.getFileHash());

        return virtualFile;
    }

}
