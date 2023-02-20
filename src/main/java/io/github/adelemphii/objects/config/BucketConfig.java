package io.github.adelemphii.objects.config;

import io.github.adelemphii.objects.exceptions.InvalidConfigException;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class BucketConfig {

    @Getter @Setter
    private String projectID;
    @Getter @Setter
    private String bucketName;
    @Getter @Setter
    private String objectName;
    @Getter @Setter
    private String objectPath;

    public BucketConfig(String projectID, String bucketName, String objectName, String objectPath) {
        this.projectID = projectID;
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.objectPath = objectPath;
    }

    public void validate(File configFile) throws InvalidConfigException {
        if(projectID == null || projectID.isEmpty()) {
            throw new InvalidConfigException("Project ID is not set", configFile);
        }
        if(bucketName == null || bucketName.isEmpty()) {
            throw new InvalidConfigException("Bucket Name is not set", configFile);
        }
        if(objectName == null || objectName.isEmpty()) {
            throw new InvalidConfigException("Object Name is not set", configFile);
        }
        if(objectPath == null || objectPath.isEmpty()) {
            throw new InvalidConfigException("Object Path is not set", configFile);
        }
    }
}
