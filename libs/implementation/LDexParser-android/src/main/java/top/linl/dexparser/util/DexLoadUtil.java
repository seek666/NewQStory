package top.linl.dexparser.util;

import android.content.Context;

import java.io.File;

import dalvik.system.DexClassLoader;

public class DexLoadUtil {
    public static DexClassLoader buildDexClassLoader(Context context, String apkPath, String targetOutputDirectoryName) {
        //在应用安装目录下创建一个名为app_dex文件夹目录,如果已经存在则不创建 /data/data/hostPackageName/app_LActivity_DEXHotLoad
        File optimizedDirectoryFile = context.getDir(targetOutputDirectoryName, Context.MODE_PRIVATE);
        // 构建插件的DexClassLoader类加载器，参数：
        // 1、包含dex的apk文件或jar文件的路径，
        // 2、apk、jar解压缩生成dex存储的目录，
        // 3、本地library库目录，一般为null，
        // 4、父ClassLoader
        DexClassLoader dexClassLoader = new DexClassLoader(apkPath, optimizedDirectoryFile.getPath(),
                null, ClassLoader.getSystemClassLoader());
        return dexClassLoader;
    }
}
