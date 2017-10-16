package com.yl.bsdk.utils;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class YLCommonUtils {

    /**
     * 检测是否安装apk
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkHasInstallApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        //获取系统中安装的应用包的信息
        List<PackageInfo> listPackageInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < listPackageInfo.size(); i++) {
            if (listPackageInfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }
}
