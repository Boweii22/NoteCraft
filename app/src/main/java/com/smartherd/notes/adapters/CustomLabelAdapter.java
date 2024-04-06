package com.smartherd.notes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smartherd.notes.R;

import java.util.List;

public class CustomLabelAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mTags;

    public CustomLabelAdapter(Context context, List<String> tags) {
        mContext = context;
        mTags = tags;
    }

    public void updateTags(List<String> tags) {
        mTags = tags;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTags.size();
    }

    @Override
    public Object getItem(int position) {
        return mTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_label, parent, false);
        }

        TextView labelTextView = convertView.findViewById(R.id.labelTextView);
        labelTextView.setText(mTags.get(position));

        return convertView;
    }
}
