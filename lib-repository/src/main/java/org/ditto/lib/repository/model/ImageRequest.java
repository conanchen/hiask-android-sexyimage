package org.ditto.lib.repository.model;

import org.ditto.sexyimage.grpc.Common;

public class ImageRequest {
    public Common.ImageType imageType;
    public int page;
    public int pageSize;
    public boolean refresh;
    public boolean loadMore;

    public ImageRequest() {
    }

    private ImageRequest(Common.ImageType imageType, int page, int pageSize, boolean refresh, boolean loadMore) {
        this.imageType = imageType;
        this.page = page;
        this.pageSize = pageSize;
        this.refresh = refresh;
        this.loadMore = loadMore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Common.ImageType imageType;
        private int page;
        private int pageSize;
        private boolean refresh;
        private boolean loadMore;

        Builder() {
        }

        public ImageRequest build() {
            String missing = "";

            if (imageType == null) {
                missing += " imageType";
            }


            if (page < 0) {
                missing += " page";
            }

            if (pageSize < 10) {
                missing += " pageSize";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new ImageRequest(imageType, page, pageSize, refresh, loadMore);
        }

        public Builder setImageType(Common.ImageType imageType) {
            this.imageType = imageType;
            return this;
        }

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder setRefresh(boolean refresh) {
            this.refresh = refresh;
            return this;
        }

        public Builder setLoadMore(boolean loadMore) {
            this.loadMore = loadMore;
            return this;
        }
    }


}