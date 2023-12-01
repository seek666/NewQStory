package lin.xposed.hook.util.qq;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import lin.xposed.common.utils.ActivityTools;
import lin.xposed.hook.HookEnv;

public class ToastTool {
    public static void show(Object content) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context activity;
                if (HookEnv.getHostAppContext() == null) {
                    activity = ActivityTools.getActivity();
                    return;
                } else {
                    activity = HookEnv.getHostAppContext();
                }
                Toast.makeText(activity, String.valueOf(content), Toast.LENGTH_LONG).show();
                /*MethodTool.find("com.tencent.mobileqq.widget.QQToast")
                        .name("makeText")
                        .params(android.content.Context.class, java.lang.CharSequence.class, int.class)
                        .call(null,activity, String.valueOf(content), 1000);*/
            }
        });
    }
}
