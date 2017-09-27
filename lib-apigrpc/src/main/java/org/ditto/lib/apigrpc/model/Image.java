package org.ditto.lib.apigrpc.model;

import com.google.common.base.Strings;

import org.ditto.sexyimage.grpc.Common;


public class Image {
    public String url;
    public String infoUrl;
    public String title;
    public String desc;
    public Common.ImageType type;//NORMAL,POSTER,SEXY,PORN
    public long lastUpdated;
    public boolean active;
    public boolean toprank;

    public Image() {
    }

    private Image(String url, String infoUrl, String title, String desc, Common.ImageType type, long lastUpdated, boolean active, boolean toprank) {
        this.url = url;
        this.infoUrl = infoUrl;
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.lastUpdated = lastUpdated;
        this.active = active;
        this.toprank = toprank;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String url;
        private String infoUrl;
        private String title;
        private String desc;
        private Common.ImageType type;
        private long lastUpdated;
        private boolean active;
        private boolean toprank;

        Builder() {
        }

        public Image build() {
            String missing = "";
            if (Strings.isNullOrEmpty(url)) {
                missing += " url";
            }
            if (type == null) {
                missing += " type";
            }

            if (lastUpdated < 1) {
                missing += " lastUpdated";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            Image image = new Image(url, infoUrl, title, desc, type, lastUpdated,active,toprank);
            return image;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setInfoUrl(String infoUrl) {
            this.infoUrl = infoUrl;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder setType(Common.ImageType type) {
            this.type = type;
            return this;
        }

        public Builder setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder setToprank(boolean toprank) {
            this.toprank = toprank;
            return this;
        }
    }

}
