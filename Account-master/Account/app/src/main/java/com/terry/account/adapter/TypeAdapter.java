package com.terry.account.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.terry.account.R;
import com.terry.account.bean.TypeBean;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private List<TypeBean> mList;
    private Activity mAct;
    OnZichanClick mOnNewsItemClick;

    /**
     * 回调接口，点击了哪个item
     */
    public interface OnZichanClick {
        void OnClickType(TypeBean typeBean);
    }


    public TypeAdapter(Activity act, List<TypeBean> list, OnZichanClick onNewsItemClick) {
        this.mList = list;
        this.mAct = act;
        mOnNewsItemClick = onNewsItemClick;

    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_zichan, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final TypeBean info = mList.get(i);
        viewHolder.mtvName.setText(info.getName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnNewsItemClick != null) {
                    mOnNewsItemClick.OnClickType(info);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mtvName;


        public ViewHolder(View itemView) {
            super(itemView);
            mtvName = (TextView) itemView
                    .findViewById(R.id.tv_name);
        }

    }
}
