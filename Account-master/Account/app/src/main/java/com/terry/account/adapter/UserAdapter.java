package com.terry.account.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.terry.account.R;
import com.terry.account.bean.UserBean;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserBean> mList;
    private Activity mAct;
    OnUserClick mOnNewsItemClick;

    /**
     * 回调接口，点击了哪个item
     */
    public interface OnUserClick {
        void OnClickType(UserBean userBean);
    }


    public UserAdapter(Activity act, List<UserBean> list, OnUserClick onNewsItemClick) {
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
        final UserBean info = mList.get(i);
        viewHolder.mtvName.setText("账户："+info.getName());
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
