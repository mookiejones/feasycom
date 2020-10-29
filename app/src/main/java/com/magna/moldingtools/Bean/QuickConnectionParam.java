package com.magna.moldingtools.Bean;


import android.app.Activity;

public class QuickConnectionParam {
    private final String name;
    private final String mac;
    private final Activity activity;
    private final String data;

    public QuickConnectionParam(String name, String mac, String data, Activity activity) {
        this.name = name;
        this.mac = mac;
        this.data = data;
        this.activity = activity;
    }

    public String getName() {
        return this.name;
    }

    public String getMac() {
        return this.mac;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public String getData() {
        return this.data;
    }

    public static final class Builder {
        private String mName;
        private String mMac;
        private Activity mActivity;
        private String mData;

        public Builder() {
        }

        public Builder setmName(String mName) {
            if ("".equals(mName)) {
                mName = null;
            }

            this.mName = mName;
            return this;
        }

        public Builder setmMac(String mMac) {
            if ("".equals(mMac)) {
                mMac = null;
            }

            this.mMac = mMac;
            return this;
        }

        public Builder setmActivity(Activity mActivity) {
            this.mActivity = mActivity;
            return this;
        }

        public Builder setmData(String mData) {
            if ("".equals(mData)) {
                mData = null;
            }

            this.mData = mData;
            return this;
        }

        public QuickConnectionParam build() {
            return new QuickConnectionParam(this.mName, this.mMac, this.mData, this.mActivity);
        }
    }
}
