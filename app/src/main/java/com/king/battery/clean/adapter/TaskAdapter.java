package com.king.battery.clean.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.battery.clean.bean.TaskInfo;
import com.king.batterytest.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by zhoukang on 2017/4/18.
 */

public class TaskAdapter extends RecyclerView.Adapter {


    private List<TaskInfo> list;
    private Context mContext;


    public TaskAdapter(List<TaskInfo> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeskViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final DeskViewHolder cholder = (DeskViewHolder) holder;
        if (list.get(position).getTask_icon() != null)
            cholder.ivIcon.setImageDrawable(list.get(position).getTask_icon());
        cholder.tvTaskName.setText(list.get(position).getTask_name());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class DeskViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_task_name)
        TextView tvTaskName;
        @BindView(R.id.iv_icon)
        ImageView ivIcon;

        public DeskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
