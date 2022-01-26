package com.smart.eduvanz.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.smart.eduvanz.R;

import java.util.List;

public class MobileNumberListViewAdapter extends ArrayAdapter<String> {

    Context context;
    List<String> mobileNumberList;
    public MobileNumberListViewAdapter(Context context, int resourceId,
                                       List<String> items) {
        super(context, resourceId, items);
        this.context = context;
        mobileNumberList =items;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtPhoneNumber;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_popup_item, null);
            holder = new ViewHolder();
            holder.txtPhoneNumber = (TextView) convertView.findViewById(R.id.phone_numberId);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtPhoneNumber.setText(mobileNumberList.get(position));
        return convertView;
    }
}
