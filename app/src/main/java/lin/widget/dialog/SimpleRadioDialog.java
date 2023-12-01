package lin.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import lin.widget.dialog.base.BaseSimpleDialog;
import lin.xposed.R;

public class SimpleRadioDialog extends BaseSimpleDialog {
    private final RadioGroup radioGroup;
    private final TextView title;
    private final String[] data;
    private String selected;
    private OnChecked onChecked;

    public SimpleRadioDialog(Context context, String[] data) {
        super(context);
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.simple_dialog_radio_view, null);
        radioGroup = root.findViewById(R.id.dialog_radio_group);
        this.title = root.findViewById(R.id.simple_radio_dialog_title);
        this.data = data;
        setContentView(root);
    }

    public void setTitle(String text) {
        this.title.setText(text);
    }

    public void setSelected(String text) {
        this.selected = text;
    }

    public void setOnClick(OnChecked onClick) {
        this.onChecked = onClick;
    }

    @Override
    public void show() {
        for (String text : data) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.radio_view, null);
            radioButton.setText(text);
            radioButton.setOnClickListener(v -> {
                if (onChecked != null) onChecked.onClick(text);
            });
            radioGroup.addView(radioButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            if (text.equals(selected))
                radioButton.setChecked(true);//先添加到RadioGroup再设置开关状态
        }
        super.show();
    }

    public interface OnChecked {
        void onClick(String text);
    }
}
