package com.example.ascope_lite.Notice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ascope_lite.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class NoticeListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<NoticeDTO> data;

    public int getNoticeID(int position) {
        return data.get(position).getId();
    }

    public NoticeListAdapter(Context context, List<NoticeDTO> data) {
        this.context = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        @SuppressLint({"ViewHolder", "InflateParams"}) View mView = layoutInflater.inflate(R.layout.listview_notice, null);
        TextView tv_title = mView.findViewById(R.id.tv_title);
        TextView tv_author = mView.findViewById(R.id.tv_author);
        TextView tv_date = mView.findViewById(R.id.tv_date);

        String date_st = data.get(position).getUpdated_at();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date date = null;
        try {
            date = format.parse(date_st);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        new_format.setTimeZone(TimeZone.getDefault());
        date_st = new_format.format(date);

        tv_title.setText(data.get(position).getTitle());
        tv_author.setText(data.get(position).getAuthor());
        tv_date.setText(date_st);

        return mView;
    }
}
