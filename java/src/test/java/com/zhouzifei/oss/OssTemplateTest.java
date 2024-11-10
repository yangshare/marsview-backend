package com.zhouzifei.oss;

import com.zhouzifei.tool.config.QiniuFileProperties;
import com.zhouzifei.tool.config.SimpleFsProperties;
import com.zhouzifei.tool.consts.StorageTypeConst;
import com.zhouzifei.tool.dto.VirtualFile;
import com.zhouzifei.tool.listener.ProgressListener;
import com.zhouzifei.tool.service.ApiClient;
import com.zhouzifei.tool.service.FileUploader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;

@SpringBootApplication(scanBasePackages = "com.zhouzifei.*")
@SpringBootTest
@ActiveProfiles("oss")
public class OssTemplateTest {

    @Autowired
    private SimpleFsProperties simpleFsProperties;

    /**
     * 测试用文件名,该文件在测试资源文件夹下
     */
    private static final String TEST_OBJECT_NAME = "test.txt";

    @Test
    @SneakyThrows
    public void test() {
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


        FileInputStream file = new FileInputStream(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + TEST_OBJECT_NAME));

        VirtualFile virtualFile = apiClient.uploadFile(file, TEST_OBJECT_NAME);
        System.out.println(virtualFile.getFullFilePath());
        System.out.println(virtualFile.getFileHash());


    }

}
