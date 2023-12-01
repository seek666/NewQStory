package lin.widget.dialog.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import lin.widget.dialog.SimpleMultiCheckBoxDialog;
import lin.xposed.R;

public class MultipleAdapter<T> extends BaseAdapter {

    private final LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
    private final List<SimpleMultiCheckBoxDialog.Item<T>> dataList;//数据集合
    private SimpleMultiCheckBoxDialog.OnCheck<T> onCheck;

    public MultipleAdapter(Context context, List<SimpleMultiCheckBoxDialog.Item<T>> dataList) {
        this.mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
    }

    public void setOnCheckbox(SimpleMultiCheckBoxDialog.OnCheck<T> onCheck) {
        this.onCheck = onCheck;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public SimpleMultiCheckBoxDialog.Item<T> getItem(int i) {
        return this.dataList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final SimpleMultiCheckBoxDialog.Item item = getItem(i);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.checkbox_view, null);
            holder = new ViewHolder();
            holder.checkbox = convertView.findViewById(R.id.checkbox);
            holder.textView = convertView.findViewById(R.id.checkbox_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CheckBox checkbox = (CheckBox) holder.checkbox;
        TextView textView = (TextView) holder.textView;
        textView.setText(item.getText());
        convertView.setOnClickListener(v -> {
            //反选状态 已勾选会变未勾选 / 未勾选会变已勾选
            checkbox.toggle();
            boolean checked = checkbox.isChecked();
            if (onCheck != null) {
                onCheck.onChecked(item, checked);
            }
        });

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
//        return false;//Item不可点击
        return super.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
//        return false;//Item不可点击
        // 拦截事件交给上一级处理
        return super.isEnabled(position);
    }


    /**
     * View持有者
     * 里面放已经findViewById过的View
     */
    private static class ViewHolder {
        private View textView;
        private View checkbox;
    }
}
