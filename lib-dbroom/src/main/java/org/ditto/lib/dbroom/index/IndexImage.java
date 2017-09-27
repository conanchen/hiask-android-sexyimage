package org.ditto.lib.dbroom.index;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;

/**
 * 图片列表使用的索引存储
 */
@Entity
public class IndexImage implements Parcelable {

    @PrimaryKey
    @NonNull
    public String url;
    public String infoUrl;
    public String title;
    public String desc;
    public String type;//NORMAL,POSTER,SEXY,PORN
    public long visitCount;
    public long created;
    public long lastUpdated;
    public boolean active;
    public boolean toprank;

    public IndexImage() {
    }

    private IndexImage(@NonNull String url, String infoUrl, String title, String desc, String type, long visitCount, long created, long lastUpdated, boolean active, boolean toprank) {
        this.url = url;
        this.infoUrl = infoUrl;
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.visitCount = visitCount;
        this.created = created;
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
        private String type;//NORMAL,POSTER,SEXY,PORN
        private long visitCount;
        private long created;
        private long lastUpdated;
        private boolean active;
        private boolean toprank;

        Builder() {
        }


        public IndexImage build() {
            String missing = "";

            if (Strings.isNullOrEmpty(url)) {
                missing += " url";
            }

            if (Strings.isNullOrEmpty(type)) {
                missing += " type";
            }


            if (lastUpdated < 1) {
                missing += " lastUpdated";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            IndexImage indexImage = new
                    IndexImage(url, infoUrl, title, desc, type,visitCount, created,lastUpdated,active,toprank);
            return indexImage;
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

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setVisitCount(long visitCount) {
            this.visitCount = visitCount;
            return this;
        }

        public Builder setCreated(long created) {
            this.created = created;
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


    public static final Creator<IndexImage> CREATOR = new Creator<IndexImage>() {
        @Override
        public IndexImage createFromParcel(Parcel in) {
            return new IndexImage(in);
        }

        @Override
        public IndexImage[] newArray(int size) {
            return new IndexImage[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeString(url);
    }

    protected IndexImage(Parcel in) {
        this.url = in.readString();
    }


}
