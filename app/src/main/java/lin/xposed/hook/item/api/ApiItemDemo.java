package lin.xposed.hook.item.api;

import java.lang.reflect.Method;

import lin.xposed.hook.load.base.ApiHookItem;
import lin.xposed.hook.load.methodfind.IMethodFinder;
import lin.xposed.hook.load.methodfind.MethodFinder;

//@HookItem("Demo名称")
public class ApiItemDemo extends ApiHookItem implements IMethodFinder {

    @Override
    protected Method getApiMethod() {
        return null;
    }

    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {

    }

    @Override
    public void startFind(MethodFinder finder) throws Exception {

    }

    @Override
    public void getMethod(MethodFinder finder) {

    }
}
