package com.k.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on 15/9/7.
 */
public class AutoAdapter extends BaseAdapter {

    private Context mContext;

    private List<String> list = new ArrayList<>();

    private LayoutInflater mInflater;

    private int hidePos = -1;

    private int movePos = -1;

    public AutoAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<String> list) {
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
        notifyDataSetChanged();
    }

    public boolean movePos(final int pos) {
        if (pos != movePos && pos > -1 && pos < getCount() && hidePos > -1) {
            final String item = list.remove(hidePos);
            list.add(pos, item);
            setHidePos(pos);
            return true;
        }
        return false;
    }

    public void setHidePos(final int pos) {
        this.hidePos = pos;
        this.movePos = pos;
        notifyDataSetChanged();
    }

    public int getHidePos() {
        return hidePos;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_for_auto_grid_view, null);
            holder.mTextView = (TextView) convertView.findViewById(R.id.num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String title = getItem(position);
        holder.mTextView.setText(title);
        if (hidePos == position) {
            convertView.setVisibility(View.INVISIBLE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(mContext, title, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView mTextView;
    }
}
