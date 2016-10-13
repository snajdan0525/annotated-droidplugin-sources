/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.hook.proxy;

import android.content.Context;
import android.util.AndroidRuntimeException;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.IActivityManagerHookHandle;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.reflect.Utils;
import com.morgoo.helper.Log;
import com.morgoo.helper.MyProxy;
import com.morgoo.helper.compat.ActivityManagerNativeCompat;
import com.morgoo.helper.compat.IActivityManagerCompat;
import com.morgoo.helper.compat.SingletonCompat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Hook some function on IActivityManager
 * <p/>
 * Code by Andy Zhang (zhangyong232@gmail.com) on 15/2/7.
 */
public class IActivityManagerHook extends ProxyHook {

    private static final String TAG = IActivityManagerHook.class.getSimpleName();

    public IActivityManagerHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    public BaseHookHandle createHookHandle() {
        return new IActivityManagerHookHandle(mHostContext);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return super.invoke(proxy, method, args);
        } catch (SecurityException e) {
            String msg = String.format("msg[%s],args[%s]", e.getMessage(), Arrays.toString(args));
            SecurityException e1 = new SecurityException(msg);
            e1.initCause(e);
            throw e1;
        }
    }

    @Override
    public void onInstall(ClassLoader classLoader) throws Throwable {
        Class cls = ActivityManagerNativeCompat.Class();
        Object obj = FieldUtils.readStaticField(cls, "gDefault");
        if (obj == null) {
            ActivityManagerNativeCompat.getDefault();
            obj = FieldUtils.readStaticField(cls, "gDefault");// »ñµÃÔ­Ê¼µÄIActivityManager¶ÔÏó
        }

        if (IActivityManagerCompat.isIActivityManager(obj)) {
            setOldObj(obj);
            Class<?> objClass = mOldObj.getClass();
            List<Class<?>> interfaces = Utils.getAllInterfaces(objClass);//»ñÈ¡Ò»×é½Ó¿Ú
            Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
			//¸ù¾İÒ»×é½Ó¿ÚºÍÀà×°ÔØÆ÷»¹ÓĞ¹¹Ôìº¯ÊıÖĞnew³öÀ´µÄIActivityManagerHookHandleµ÷ÓÃ´¦ÀíÆ÷À´Éú³ÉÒ»¸ö¶¯Ì¬µ÷ÓÃ´úÀíÀàµÄÊµÀı
			Object proxiedActivityManager = MyProxy.newProxyInstance(objClass.getClassLoader(), ifs, this);
			
            FieldUtils.writeStaticField(cls, "gDefault", proxiedActivityManager);// °ÑËûµÄmInstanceÓòÌæ»»µô ³ÉÎªÎÒÃÇ×Ô¼ºµÄHook¶ÔÏó
            Log.i(TAG, "Install ActivityManager Hook 1 old=%s,new=%s", mOldObj, proxiedActivityManager);
        } else if (SingletonCompat.isSingleton(obj)) {
            Object obj1 = FieldUtils.readField(obj, "mInstance");
            if (obj1 == null) {
                SingletonCompat.get(obj);
                obj1 = FieldUtils.readField(obj, "mInstance");
            }
            setOldObj(obj1);
            List<Class<?>> interfaces = Utils.getAllInterfaces(mOldObj.getClass());
            Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
            final Object object = MyProxy.newProxyInstance(mOldObj.getClass().getClassLoader(), ifs, IActivityManagerHook.this);
            Object iam1 = ActivityManagerNativeCompat.getDefault();

            //è¿™é‡Œå…ˆå†™ä¸€æ¬¡ï¼Œé˜²æ­¢åé¢æ‰¾ä¸åˆ°Singletonç±»å¯¼è‡´çš„æŒ‚é’©å­å¤±è´¥çš„é—®é¢˜ã€‚
            FieldUtils.writeField(obj, "mInstance", object);

            //è¿™é‡Œä½¿ç”¨æ–¹å¼1ï¼Œå¦‚æœæˆåŠŸçš„è¯ï¼Œä¼šå¯¼è‡´ä¸Šé¢çš„å†™æ“ä½œè¢«è¦†ç›–ã€‚
            FieldUtils.writeStaticField(cls, "gDefault", new android.util.Singleton<Object>() {
                @Override
                protected Object create() {
                    Log.e(TAG, "Install ActivityManager 3 Hook  old=%s,new=%s", mOldObj, object);
                    return object;
                }
            });

            Log.i(TAG, "Install ActivityManager Hook 2 old=%s,new=%s", mOldObj.toString(), object);
            Object iam2 = ActivityManagerNativeCompat.getDefault();
            // æ–¹å¼2
            if (iam1 == iam2) {
                //è¿™æ®µä»£ç æ˜¯åºŸçš„ï¼Œæ²¡å•¥ç”¨ï¼Œå†™è¿™é‡Œåªæ˜¯ä¸æƒ³æ”¹è€Œå·²ã€‚
                FieldUtils.writeField(obj, "mInstance", object);
            }
        } else {
            throw new AndroidRuntimeException("Can not install IActivityManagerNative hook");
        }
    }
}
