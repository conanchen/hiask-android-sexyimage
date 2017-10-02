package org.ditto.lib.repository.model;

public class Status {
    public Code code;
    public String message;
    public boolean refresh;
    public boolean loadMore;

    public enum Code {
        START,
        LOADING,
        END_SUCCESS,
        END_ERROR,
        END_DISCONNECTED,
        END_UNKNOWN
    }

    public Status() {
    }

    private Status(Code code, String message, boolean refresh, boolean loadMore) {
        this.code = code;
        this.message = message;
        this.refresh = refresh;
        this.loadMore = loadMore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Code code;
        private String message;
        private boolean refresh;
        private boolean loadMore;

        Builder() {
        }

        public Status build() {
            String missing = "";

            if (code == null) {
                missing += " code";
            }

            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }

            return new Status(code, message,refresh,loadMore);
        }

        public Builder setCode(Code code) {
            this.code = code;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
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
