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

package com.morgoo.droidplugin.hook;

import android.content.Context;
import com.morgoo.helper.Log;

import java.lang.reflect.Method;

public class HookedMethodHandler {

    private static final String TAG = HookedMethodHandler.class.getSimpleName();
    protected final Context mHostContext;

    private Object mFakedResult = null;//¶¨ÒåÒ»¸öÆÛÆ­ÏµÍ³µÄ³ÉÔ±±äÁ¿
    private boolean mUseFakedResult = false;//ÊÇ·ñÊ¹ÓÃÆÛÆ­ÏµÍ³µÄ½á¹û

    public HookedMethodHandler(Context hostContext) {
        this.mHostContext = hostContext;
    }


    public synchronized Object doHookInner(Object receiver, Method method, Object[] args) throws Throwable {
        long b = System.currentTimeMillis();
        try {
            mUseFakedResult = false;//Ä¬ÈÏÊÇ²»ÆÛÆ­
            mFakedResult = null;//Ä¬ÈÏÎª¿Õ
            boolean suc = beforeInvoke(receiver, method, args);
            Object invokeResult = null;
            if (!suc) {
                invokeResult = method.invoke(receiver, args);//falseÊ±Ö´ĞĞÔ­Ê¼·½·¨
            }
            afterInvoke(receiver, method, args, invokeResult);
            if (mUseFakedResult) {//Èç¹ûÍâ²¿µÄflagÉèÖÃÎªfalse£¬Ôò·µ»ØÆÛÆ­µÄ½á¹û
                return mFakedResult;
            } else {
                return invokeResult;
            }
        } finally {
            long time = System.currentTimeMillis() - b;
            if (time > 5) {
                Log.i(TAG, "doHookInner method(%s.%s) cost %s ms", method.getDeclaringClass().getName(), method.getName(), time);
            }
        }
    }

    public void setFakedResult(Object fakedResult) {
        this.mFakedResult = fakedResult;
        mUseFakedResult = true;
    }

    /**
     * åœ¨æŸä¸ªæ–¹æ³•è¢«è°ƒç”¨ä¹‹å‰æ‰§è¡Œï¼Œå¦‚æœè¿”å›trueï¼Œåˆ™ä¸æ‰§è¡ŒåŸå§‹çš„æ–¹æ³•ï¼Œå¦åˆ™æ‰§è¡ŒåŸå§‹æ–¹æ³•
     */
    protected boolean beforeInvoke(Object receiver, Method method, Object[] args) throws Throwable {
        return false;
    }

    protected void afterInvoke(Object receiver, Method method, Object[] args, Object invokeResult) throws Throwable {
    }

    public boolean isFakedResult() {
        return mUseFakedResult;
    }

    public Object getFakedResult() {
        return mFakedResult;
    }
}
