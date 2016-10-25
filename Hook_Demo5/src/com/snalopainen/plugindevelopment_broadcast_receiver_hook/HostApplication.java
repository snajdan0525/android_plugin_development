package com.snalopainen.plugindevelopment_broadcast_receiver_hook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.hook_demo5.R;

import dalvik.system.DexClassLoader;

import android.app.Application;

public class HostApplication extends Application {

    private File m_apk;
    private DexClassLoader m_dexClassLoader;


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            //模拟从服务器拉取apk
            obtainApkFromServer();
            //初始化classLoader
            setupClazzLoader();
            //开始安装插件加载器
            setupPluginReceiverLoader();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void obtainApkFromServer() throws IOException {
        InputStream inputStream = getResources().openRawResource(R.raw.plugin);
        byte[] bytes = new byte[256];
        int length = -1;


        File dir = getDir("plugin", MODE_PRIVATE);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        m_apk = new File(dir, "plugin.apk");
        FileOutputStream fileOutputStream = new FileOutputStream(m_apk);

        while ((length = inputStream.read(bytes)) != -1) {
            fileOutputStream.write(bytes, 0, length);
        }

        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private void setupClazzLoader() {
        m_dexClassLoader = new DexClassLoader(
                m_apk.getAbsolutePath(),
                getDir("pluginOpt", MODE_PRIVATE).getAbsolutePath(),
                null, getClassLoader());
    }

    private void setupPluginReceiverLoader() throws
            ClassNotFoundException, NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, InstantiationException, InvocationTargetException {
        Class<?> packageParserClazz = Class.forName("android.content.pm.PackageParser", false, getClassLoader());

        /**
         * Parse the package at the given location. Automatically detects if the
         * package is a monolithic style (single APK file) or cluster style
         * (directory of APKs).
         * <p>
         * This performs sanity checking on cluster style packages, such as
         * requiring identical package name and version codes, a single base APK,
         * and unique split names.
         * <p>
         * Note that this <em>does not</em> perform signature verification; that
         * must be done separately in {@link #collectCertificates(Package, int)}.
         *
         * @see #parsePackageLite(File, int)
         */
        Method parsePackageMethod = packageParserClazz.getDeclaredMethod("parsePackage", File.class, int.class);

        //生成一个package parser 对象
        Object packageParserObject = packageParserClazz.newInstance();

        //获得Package对象
        Object packageObject = parsePackageMethod.invoke(packageParserObject, m_apk, 0);

        //获得package的receivers域
        Class<?> packageClazz = Class.forName("android.content.pm.PackageParser$Package", false, getClassLoader());
        Field receiversField = packageClazz.getDeclaredField("receivers");
        receiversField.setAccessible(true);

        //获得所有的receivers 他是PackageParser$Activity类型的
        List<?> receiversList = (List<?>) receiversField.get(packageObject);

        /*
         * 现在已经获得了所有的receiver
         * 就只剩获得receiver的intent filter
         * 下面就开始获得receiver的intent filter
         * 其中receiversList容器的模板实参类型是 android.content.pm.PackageParser$Activity
        * */



        /*
        * android.content.pm.PackageParser$Activity 其实是 android.content.pm.PackageParser$Component
        * public final static class Activity extends Component<ActivityIntentInfo>
        * 其中域 intents存放的是action信息
        * public final ArrayList<II> intents;
        *
        * 而II类型是 Component<II extends IntentInfo> 模板参数
        *
        * */
        Class<?> componentClazz = Class.forName("android.content.pm.PackageParser$Component");
        Field intentsField = componentClazz.getDeclaredField("intents");

        Class<?> packageParser$ActivityIntentInfoClazz = Class.forName(
                "android.content.pm.PackageParser$ActivityIntentInfo",
                false, getClassLoader());


        Method countActionsMethod = packageParser$ActivityIntentInfoClazz.getMethod("countActions");
        Method getActionMethod = packageParser$ActivityIntentInfoClazz.getMethod("getAction", int.class);

        Map<String, List<String>> receiverAndIntentFilterMap = new HashMap<>();

        /*
        * 下面的receiver 其实是 android.content.pm.PackageParser$Activity
        * 他有一个field 名为className 就是存放的receiver的className
        * 我们获得 这个className就能通过反射获得receiver对象
        * */
        Class<?> packageParser$ActivityClazz = Class.forName("android.content.pm.PackageParser$Activity", false, getClassLoader());
        Field classNameField = packageParser$ActivityClazz.getField("className");



        for (Object receiver : receiversList) {

            List<?> activityIntentInfoList = (List<?>) intentsField.get(receiver);

            if (activityIntentInfoList != null) {

                List<String> intentFilter = new ArrayList<>();
                for (Object activityIntentInfo : activityIntentInfoList) {

                    //添加所有的action 到intent filter中
                    final int count = (int) countActionsMethod.invoke(activityIntentInfo);
                    for (int i = 0; i < count; ++i) {
                        intentFilter.add((String) getActionMethod.invoke(activityIntentInfo, i));
                    }
                }

                //记录下来
                receiverAndIntentFilterMap.put((String) classNameField.get(receiver), intentFilter);
            }
        }

        PluginReceiverLoader.init(m_dexClassLoader, receiverAndIntentFilterMap);
    }
} 