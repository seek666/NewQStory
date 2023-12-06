package lin.xposed.hook.view.main.itemview.info;

import lin.xposed.hook.load.base.BaseHookItem;

public class ItemUiInfo extends BaseItemUiInfo {
    public BaseHookItem item;
    public String tips;//提示

    public ItemUiInfo(String[] paths) {
        super(paths);
    }

    public String getGroupPath() {
        if (paths.length != 4) return null;
        String[] group = new String[3];
        System.arraycopy(super.paths, 0, group, 0, 3);
        StringBuffer buffer = new StringBuffer();
        String interval = "";
        for (String str : group) {
            buffer.append(interval);
            interval = "/";
            buffer.append(str);
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        return "ItemUiInfo{" +
                "item=" + item +
                ", tips='" + tips + '\'' +
                '}';
    }
}
