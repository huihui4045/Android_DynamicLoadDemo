package com.huihui.client;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class ProxyActivity extends AppCompatActivity {


    private static final String TAG = "ProxyActivity";

    public static final String FROM = "extra.from";
    public static final int FROM_EXTERNAL = 0;
    public static final int FROM_INTERNAL = 1;

    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";

    private String mClass;
    private String mDexPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDexPath = getIntent().getStringExtra(EXTRA_DEX_PATH);
        mClass = getIntent().getStringExtra(EXTRA_CLASS);

        Log.e(TAG, "mClass=" + mClass + " mDexPath=" + mDexPath);
        if (mClass == null) {
            launchTargetActivity();
        } else {
            launchTargetActivity(mClass);
        }
    }


    private void launchTargetActivity() {

        PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(mDexPath, PackageManager.GET_ACTIVITIES);

        if (packageInfo==null) return;

        if (packageInfo.activities!=null && packageInfo.activities.length>0){

           String activityName= packageInfo.activities[0].name;

            launchTargetActivity(activityName);
        }


    }

    private void launchTargetActivity(String className) {

        Log.e(TAG, "start launchTargetActivity, className=" + className);


        File dirOutputDir = this.getDir("dex", 0);

        String dexOutputDir = dirOutputDir.getAbsolutePath();


        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();


        DexClassLoader dexClassLoader=new DexClassLoader(mDexPath,dexOutputDir,null,localClassLoader);


        try {
            Class<?> localClass = dexClassLoader.loadClass(className);

            Constructor<?> localConstructor = localClass.getConstructor(new Class[]{});


            Object instance = localConstructor.newInstance(new Object[]{});

            Log.e(TAG, "instance = " + instance);

            Method setProxy = localClass.getMethod("setProxy", new Class[]{Activity.class});

            setProxy.setAccessible(true);

            setProxy.invoke(instance,new Object[]{this});

            Method onCreate = localClass.getDeclaredMethod("onCreate", new Class[]{Bundle.class});

            Log.e(TAG, "onCreate = ");

            onCreate.setAccessible(true);

            Bundle bundle=new Bundle();

            bundle.putInt(FROM,FROM_EXTERNAL);

            onCreate.invoke(instance,new Object[]{bundle});


        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        } catch (NoSuchMethodException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {

            e.printStackTrace();
        }


    }

}
