package com.hms.demo.itla;

import android.app.Application;

import com.huawei.hms.maps.MapsInitializer;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MapsInitializer.setApiKey("CgB6e3x9geIwMR6GUfmTL+8RmgCG6cgNL81GlM39w7J3XYQvUksRRVFZJUomGxMxfJlqkIScxbWgDX7UQP7eBSwS");
    }
}
