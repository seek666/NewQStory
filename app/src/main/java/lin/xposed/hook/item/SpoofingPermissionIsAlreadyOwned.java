package lin.xposed.hook.item;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import lin.xposed.hook.annotation.HookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;

@HookItem("辅助功能/实验功能/伪装应用权限已持有")
public class SpoofingPermissionIsAlreadyOwned extends BaseSwitchFunctionHookItem {
    private static final String[][] PERMISSION_LIST = {
            {"相机", Manifest.permission.CAMERA},
//            {"GPS", "android.permission.ACCESS_FINE_LOCATION"},
            {"语音", Manifest.permission.RECORD_AUDIO}
    };

    @Override
    public String getTips() {
        return "即时生效 开启后需要手动到应用详情页给予权限 目前仅支持伪装语音权限和拍照权限 正在完善可选与更多功能";
    }

    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {
        hookBefore(Activity.class.getMethod("checkPermission", String.class, int.class, int.class), param -> {
            String permissionName = (String) param.args[0];
            for (String[] permission : PERMISSION_LIST) {
                if (permission[1].equals(permissionName)) {
                    param.setResult(PackageManager.PERMISSION_GRANTED);
                    break;
                }
            }
        });
        hookBefore(Activity.class.getMethod("checkSelfPermission", String.class), param -> {
            String permissionName = (String) param.args[0];
            for (String[] permission : PERMISSION_LIST) {
                if (permission[1].equals(permissionName)) {
                    param.setResult(PackageManager.PERMISSION_GRANTED);
                    break;
                }
            }
        });

    }
}
