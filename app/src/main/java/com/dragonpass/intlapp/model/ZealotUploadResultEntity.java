package com.dragonpass.intlapp.model;

/**
 * Description :
 *
 * @author Jam 2025/3/17
 */
public class ZealotUploadResultEntity {

    private String size;
    private String text_changelog;
    private String version;
    private String source;
    private String release_version;
    private String release_url;
    private String qrcode_url;
    private String platform;
    private String install_url;
    private String id;
    private String icon_url;
    private String device_type;
    private String created_at;
    private String build_version;
    private App app;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getText_changelog() {
        return text_changelog;
    }

    public void setText_changelog(String text_changelog) {
        this.text_changelog = text_changelog;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRelease_version() {
        return release_version;
    }

    public void setRelease_version(String release_version) {
        this.release_version = release_version;
    }

    public String getRelease_url() {
        return release_url;
    }

    public void setRelease_url(String release_url) {
        this.release_url = release_url;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getBuild_version() {
        return build_version;
    }

    public void setBuild_version(String build_version) {
        this.build_version = build_version;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public static class App{
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "App{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ZealotUploadResultEntity{" +
                "size=" + size +
                ", text_changelog='" + text_changelog + '\'' +
                ", version='" + version + '\'' +
                ", source='" + source + '\'' +
                ", release_version='" + release_version + '\'' +
                ", release_url='" + release_url + '\'' +
                ", qrcode_url='" + qrcode_url + '\'' +
                ", platform='" + platform + '\'' +
                ", install_url='" + install_url + '\'' +
                ", id='" + id + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", device_type='" + device_type + '\'' +
                ", created_at='" + created_at + '\'' +
                ", build_version='" + build_version + '\'' +
                ", app=" + app +
                '}';
    }
}
