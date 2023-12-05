package lin.xposed.hook.util.qq;

import lin.util.ReflectUtils.ClassUtils;

public class CommonClass {
    public static Class<?> getAIOContactClass() {
        return ClassUtils.getClass("com.tencent.aio.data.AIOContact");
    }

    public static Class<?> getAIOMsgItemClass() {
        return ClassUtils.getClass("com.tencent.mobileqq.aio.msg.AIOMsgItem");
    }
}
