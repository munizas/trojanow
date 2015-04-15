package com.example.asmuniz.trojanow.util;

import android.widget.ArrayAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asmuniz.trojanow.R;
import com.example.asmuniz.trojanow.obj.Post;

import java.util.List;

/**
 * Created by asmuniz on 4/12/15.
 */
public class PostListAdapter extends ArrayAdapter<Post> {

    private final Context context;
    private final List<Post> values;

    public PostListAdapter(Context context, int post_row_layout, List<Post> values) {
        super(context, R.layout.post_row_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.post_row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.firstLine);
        TextView bottomTextView = (TextView) rowView.findViewById(R.id.secondLine);
        Post post = values.get(position);
        textView.setText(post.getMessage());
        bottomTextView.setText(post.getUsername());
        return rowView;
    }
}
