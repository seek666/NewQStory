package lin.xposed.hook.item;

import android.content.Intent;

import lin.util.ReflectUtils.MethodTool;
import lin.xposed.hook.annotation.HookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;

@HookItem("净化/聊天/分享弹窗不显示好友和群")
public class ForwardDialogCloseGroupAndFriend extends BaseSwitchFunctionHookItem {
    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {
        hookBefore(
                MethodTool.find("com.tencent.mobileqq.widget.share.ShareActionSheetV2")
                .name("setIntentForStartForwardRecentActivity")
                .params(Intent.class)
                .get(),
                param -> param.setResult(null)
        );
    }
}
