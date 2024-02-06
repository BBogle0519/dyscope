package com.example.ascope_lite.Inspect_ViewList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ascope_lite.CrackInspect.CrackListResponse;
import com.example.ascope_lite.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Inspect_ViewListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<CrackListResponse> data;
    ViewHolder viewHolder;

    public Inspect_ViewListAdapter(Context context, List<CrackListResponse> data) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View mView, ViewGroup viewGroup) {

        if (mView == null) {
            mView = layoutInflater.inflate(R.layout.listview_inspect, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_type = mView.findViewById(R.id.tv_type);
//            viewHolder.tv_floor = mView.findViewById(R.id.tv_floor);
            viewHolder.tv_zone = mView.findViewById(R.id.tv_zone);
            viewHolder.tv_position = mView.findViewById(R.id.tv_position);
            viewHolder.tv_userid = mView.findViewById(R.id.tv_userid);
            viewHolder.tv_etc = mView.findViewById(R.id.tv_etc);
//            viewHolder.tv_updated_at = mView.findViewById(R.id.tv_updated_at);
            viewHolder.tv_size = mView.findViewById(R.id.tv_size);
            viewHolder.tv_length = mView.findViewById(R.id.tv_length);

            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }

        // 각 셀에 넘겨받은 텍스트 데이터를 넣는다.
        viewHolder.tv_type.setText(data.get(position).getType());
//        viewHolder.tv_floor.setText(data.get(position).getFloor() + " 층");
        viewHolder.tv_zone.setText(data.get(position).getZone());
        viewHolder.tv_position.setText(data.get(position).getPosition());
        viewHolder.tv_userid.setText(data.get(position).getUserid());
        viewHolder.tv_etc.setText(data.get(position).getEtc());
        viewHolder.tv_size.setText(data.get(position).getSize());
        viewHolder.tv_length.setText(data.get(position).getLength());

//        String date_st = data.get(position).getUpdated_at();
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//
//        Date date = null;
//        try {
//            date = format.parse(date_st);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        new_format.setTimeZone(TimeZone.getDefault());
//        date_st = new_format.format(date);
//        viewHolder.tv_updated_at.setText(date_st);

        return mView;

    }

    class ViewHolder {
        TextView tv_type;
        //        TextView tv_floor;
        TextView tv_zone;
        TextView tv_position;
        TextView tv_userid;
        TextView tv_etc;
        //        TextView tv_updated_at;
        TextView tv_size;
        TextView tv_length;
    }

    public List<CrackListResponse> getData() {
        return data;
    }

    public void setData(List<CrackListResponse> data) {
        this.data = data;
    }
}
