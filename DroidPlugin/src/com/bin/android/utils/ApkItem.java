package com.bin.android.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author dhunter
 * @time 2016-7-5 上午11:43:29
 */
public class ApkItem {
	private static final String TAG = ApkItem.class.getSimpleName();
	public Drawable icon;
	public CharSequence title;
	public String versionName;
	public int versionCode;
	public String apkfile;
	public PackageInfo packageInfo;
	public boolean installing = false;

	public ApkItem(Context context, PackageInfo info, String path) {
		PackageManager pm = context.getPackageManager();
		Resources resources = null;
		try {
			resources = getResources(context, path);
		} catch (Exception e) {
		}
		try {
			if (resources != null) {
				icon = resources.getDrawable(info.applicationInfo.icon);
			}
		} catch (Exception e) {
			icon = pm.getDefaultActivityIcon();
		}
		try {
			if (resources != null) {
				title = resources.getString(info.applicationInfo.labelRes);
			}
		} catch (Exception e) {
			title = info.packageName;
		}

		versionName = info.versionName;
		versionCode = info.versionCode;
		apkfile = path;
		packageInfo = info;
	}

	public ApkItem(PackageManager pm, PackageInfo info, String path) {
    	// 必须设置, 否则title无法获取
    	info.applicationInfo.sourceDir = path;
    	info.applicationInfo.publicSourceDir = path;
        try {
            icon = pm.getApplicationIcon(info.applicationInfo);
        } catch (Exception e) {
            icon = pm.getDefaultActivityIcon();
        }
        try {
        	title = pm.getApplicationLabel(info.applicationInfo);
        } catch (Exception e){
        	title = info.packageName;
        }
        versionName = info.versionName;
        versionCode = info.versionCode;
        apkfile = path;
        packageInfo = info;
    }

	public static Resources getResources(Context context, String apkPath)
			throws Exception {
		String PATH_AssetManager = "android.content.res.AssetManager";
		Class assetMagCls = Class.forName(PATH_AssetManager);
		Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
		Object assetMag = assetMagCt.newInstance((Object[]) null);
		Class[] typeArgs = new Class[1];
		typeArgs[0] = String.class;
		Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
				"addAssetPath", typeArgs);
		Object[] valueArgs = new Object[1];
		valueArgs[0] = apkPath;
		assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
		Resources res = context.getResources();
		typeArgs = new Class[3];
		typeArgs[0] = assetMag.getClass();
		typeArgs[1] = res.getDisplayMetrics().getClass();
		typeArgs[2] = res.getConfiguration().getClass();
		Constructor resCt = Resources.class.getConstructor(typeArgs);
		valueArgs = new Object[3];
		valueArgs[0] = assetMag;
		valueArgs[1] = res.getDisplayMetrics();
		valueArgs[2] = res.getConfiguration();
		res = (Resources) resCt.newInstance(valueArgs);
		return res;
	}

}
