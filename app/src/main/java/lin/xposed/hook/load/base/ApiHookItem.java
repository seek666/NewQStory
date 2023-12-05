package lin.xposed.hook.load.base;

import java.lang.reflect.Method;

/**
 * API类 不用作功能 只对接功能 保持一个类只返回一个api方法 但可以进行挂钩
 */
public abstract class ApiHookItem extends BaseHookItem {
    protected abstract Method getApiMethod();
}
