package lin.xposed.hook.item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import lin.util.ReflectUtils.ConstructorUtils;
import lin.xposed.hook.annotation.HookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.load.methodfind.IMethodFinder;
import lin.xposed.hook.load.methodfind.MethodFinder;
import lin.xposed.hook.util.qq.QQEnvTool;

@HookItem("辅助功能/聊天/灰字提示显示QQ号")
public class GrayPromptShowUin extends BaseSwitchFunctionHookItem implements IMethodFinder {
    private Method getJsonElementTextMethod;

    @Override
    public String getTips() {
        return "比如打卡,撤回,戳一戳";
    }

    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {
        Method method = classLoader.loadClass("com.tencent.qqnt.kernel.nativeinterface.XmlElement").getDeclaredMethod("getMembers");
        hookAfter(method, param -> {
            HashMap<String, String> resultMap = (HashMap<String, String>) param.getResult();
            HashMap<String, String> newResult = new HashMap<>();
            if (newResult == null) return;
            for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                String peerUid = entry.getKey();
                String name = resultMap.get(peerUid) + "(" + QQEnvTool.getUinFromUid(peerUid) + ")";
                newResult.put(peerUid, name);
            }
            param.setResult(newResult);
        });
        Class<?> JsonGrayElementClass = classLoader.loadClass("com.tencent.qqnt.kernel.nativeinterface.JsonGrayElement");
        hookBefore(getJsonElementTextMethod, param -> {
            Object param0json = param.args[0];
            JSONObject jsonResult = new JSONObject(String.valueOf(param0json.getClass().getMethod("getJsonStr").invoke(param0json)));
            JSONArray optJSONArray = jsonResult.optJSONArray("items");
            if (optJSONArray == null) return;
            JSONArray array = new JSONArray();
            for (int i = 0; i < optJSONArray.length(); i++) {
                JSONObject optJSONObject = optJSONArray.getJSONObject(i);
                String optString;
                if (optJSONObject == null || (optString = optJSONObject.optString("type")) == null)
                    return;
                if (optString.equals("qq")) {
                    String qq = optJSONObject.optString("uin");
                    if (qq.equals("")) qq = QQEnvTool.getUinFromUid(optJSONObject.optString("uid"));
                    String name = optJSONObject.optString("nm");
                    optJSONObject.remove("nm");
                    optJSONObject.put("nm", name + "(" + qq + ")");
                }
                array.put(optJSONObject);
            }
            jsonResult.put("items", array);
            Object newResult = ConstructorUtils.newInstance(JsonGrayElementClass,
                    new Class[]{long.class, String.class, String.class, boolean.class, classLoader.loadClass("com.tencent.qqnt.kernel.nativeinterface.XmlToJsonParam")},
                    JsonGrayElementClass.getMethod("getBusiId").invoke(param0json),
                    jsonResult.toString(),
                    JsonGrayElementClass.getMethod("getRecentAbstract").invoke(param0json),
                    JsonGrayElementClass.getMethod("getIsServer").invoke(param0json),
                    JsonGrayElementClass.getMethod("getXmlToJsonParam").invoke(param0json));
            param.args[0] = newResult;
        });
    }

    @Override
    public void startFind(MethodFinder finder) throws Exception {
        finder.putMethod("JsonGrayElementText", finder.findMethodString("getHighlightMsgText(ssb,…ightItems, limitIconSize)")[0]);
    }

    @Override
    public void getMethod(MethodFinder finder) {
        getJsonElementTextMethod = finder.getMethod("JsonGrayElementText");
    }
}
