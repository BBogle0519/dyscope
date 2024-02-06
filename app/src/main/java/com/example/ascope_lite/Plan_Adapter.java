package com.example.ascope_lite;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ascope_lite.Planlist.PlanResponse;

import java.util.List;

public class Plan_Adapter extends BaseAdapter {

    private final Context context;
    private final List<PlanResponse> plan_list;

    public Plan_Adapter(Context context, List<PlanResponse> plan_list) {
        this.context = context;
        this.plan_list = plan_list;
    }

    @Override
    public int getCount() {
        return plan_list != null ? plan_list.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return plan_list.get(i).getId();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder") View rootView = LayoutInflater.from(context).inflate(R.layout.spinner_plan_item, viewGroup, false);
        TextView tv_plan_name = rootView.findViewById(R.id.tv_plan_name);
        String result_floor = "";
        int floor = plan_list.get(i).getFloor();

        if (floor > 0) {
            result_floor = "지상 " + floor + "층 ";
        } else if (floor < 0) {
            result_floor = "지하 " + abs(floor) + "층 ";
        } else {
            result_floor = "기타 ";
        }

        tv_plan_name.setText(result_floor + plan_list.get(i).getName());

        return rootView;
    }
}
