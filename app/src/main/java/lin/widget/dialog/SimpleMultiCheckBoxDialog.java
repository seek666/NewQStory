package lin.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import lin.widget.dialog.base.BaseSimpleDialog;
import lin.widget.dialog.view.MultipleAdapter;
import lin.xposed.R;

public class SimpleMultiCheckBoxDialog extends BaseSimpleDialog {
    ListView listView;
    MultipleAdapter<?> multipleAdapter;

    public <T> SimpleMultiCheckBoxDialog(Context context, List<Item<T>> dataList) {
        super(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.simple_dialog_multi_checkbox, null, false);
        listView = rootView.findViewById(R.id.multi_checkbox_listview);
        multipleAdapter = new MultipleAdapter<>(getContext(), dataList);
        listView.setAdapter(multipleAdapter);
        setContentView(rootView);
    }

    public void setText(String text) {

    }

    public void setOnCheckbox(OnCheck onCheck) {
        multipleAdapter.setOnCheckbox(onCheck);
    }

    public interface OnCheck<T> {
        void onChecked(Item<T> item, boolean isChecked);
    }

    public static class Item<E> {
        private final String text;
        private E key;

        public Item(String text) {
            this.text = text;
        }

        public Item(String text, E key) {
            this.text = text;
            this.key = key;
        }

        public E getKey() {
            return this.key;
        }

        public String getText() {
            return text;
        }
    }

}
