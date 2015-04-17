package com.example.asmuniz.trojanow.util;

import com.example.asmuniz.trojanow.R;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.widget.TextView;
import android.widget.CheckBox;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.List;

import com.example.asmuniz.trojanow.obj.User;

/**
 * Created by asmuniz on 4/16/15.
 */
public class InteractiveArrayAdapter extends ArrayAdapter<UserSelectionModel> {

    private final List<UserSelectionModel> list;
    private final Activity context;

    public InteractiveArrayAdapter(Activity context, List<UserSelectionModel> list) {
        super(context, R.layout.row_checkbox_layout, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_checkbox_layout, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            UserSelectionModel element = (UserSelectionModel) viewHolder.checkbox
                                    .getTag();
                            element.setSelected(buttonView.isChecked());

                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getUsername());
        holder.checkbox.setChecked(list.get(position).isSelected());
        return view;
    }
}
