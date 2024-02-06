package com.example.ascope_lite.Project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ascope_lite.R;

import java.util.List;

public class ProjectListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<ProjectGetListResponse> data;

    public ProjectListAdapter(Context context, List<ProjectGetListResponse> data) {
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
        @SuppressLint({"ViewHolder", "InflateParams"}) View mView = layoutInflater.inflate(R.layout.listview_project, null);
        TextView facilityname = mView.findViewById(R.id.facilityName);
        TextView facilityaddress = mView.findViewById(R.id.facilityAddress);
        TextView facilityowner = mView.findViewById(R.id.facilityOwner);

        facilityname.setText(data.get(position).getName());
        facilityaddress.setText(data.get(position).getAddress());
        facilityowner.setText(data.get(position).getEtc());

        return mView;
    }
}
