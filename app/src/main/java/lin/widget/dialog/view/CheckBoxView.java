package lin.widget.dialog.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.CheckedTextView;

import lin.xposed.R;

@SuppressLint("AppCompatCustomView")
public class CheckBoxView extends CheckBox {
    private CheckedTextView checkedTextView;

    public CheckBoxView(Context context) {
        super(context);
        //绑定布局
        this.checkedTextView = this.findViewById(R.id.checkbox);
    }

    public CheckedTextView getCheckbox() {
        return this.checkedTextView;
    }
}
