package com.github.mirs.banxiaoxiao.framework.swagger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: bc
 * @date: 2021-03-03 14:17
 **/
@ConfigurationProperties("swagger.config")
public class SwaggerProperties {
    /**
     * 文档版本
     */
    private String docVersion;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档简介
     */
    private String description;
    /**
     * 文档联系人姓名
     */
    private String contactName;
    /**
     * 文档联系人URL
     */
    private String contactUrl;
    /**
     * 文档联系人邮箱
     */
    private String contactEmail;
    /**
     * 文档使用license 版本
     */
    private String license;
    /**
     * 文档使用license url
     */
    private String licenseUrl;


    public String getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(String docVersion) {
        this.docVersion = docVersion;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactUrl() {
        return contactUrl;
    }

    public void setContactUrl(String contactUrl) {
        this.contactUrl = contactUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }
}
