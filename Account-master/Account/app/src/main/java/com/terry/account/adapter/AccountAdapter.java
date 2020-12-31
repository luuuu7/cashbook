package com.terry.account.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.terry.account.R;
import com.terry.account.bean.AccountBean;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    private List<AccountBean> mList;
    private Activity mAct;
    OnAccountClick mOnNewsItemClick;

    /**
     * 回调接口，点击了哪个item
     */
    public interface OnAccountClick {
        void OnClickAccount(AccountBean accountInfo);
    }


    public AccountAdapter( Activity act,List<AccountBean> list, OnAccountClick onNewsItemClick) {
        this.mList = list;
        this.mAct = act;
        mOnNewsItemClick = onNewsItemClick;

    }



    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_account, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final AccountBean info = mList.get(i);

        if(info.getAccountType().equals("income")){
            viewHolder.mtvType.setText("收入");
            viewHolder.mtvMoney.setText(info.getMoney());
            viewHolder.mtvName.setText(info.getType());
            viewHolder.mtvTime.setText(info.getTime());
            if(info.getType().equals("工资收入")){
                viewHolder.mIcon.setImageResource(R.drawable.gongzishouru);
            }else if(info.getType().equals("股票收入")){
                viewHolder.mIcon.setImageResource(R.drawable.gupiaoshouru);
            }else if(info.getType().equals("其他收入")){
                viewHolder.mIcon.setImageResource(R.drawable.qitashouru);
            }else{
                viewHolder.mIcon.setImageResource(R.drawable.qitashouru);
            }
        }else {
            viewHolder.mtvType.setText("支出");
            viewHolder.mtvMoney.setText(info.getMoney());
            viewHolder.mtvName.setText(info.getType());
            viewHolder.mtvTime.setText(info.getTime());
            if (info.getType().equals("日常购物")) {
                viewHolder.mIcon.setImageResource(R.drawable.richanggouwu);
            } else if (info.getType().equals("交际送礼")) {
                viewHolder.mIcon.setImageResource(R.drawable.jiaojisongli);
            } else if (info.getType().equals("餐饮开销")) {
                viewHolder.mIcon.setImageResource(R.drawable.canyinkaixiao);
            } else if (info.getType().equals("购置衣物")) {
                viewHolder.mIcon.setImageResource(R.drawable.gouzhiyiwu);
            } else if (info.getType().equals("娱乐开销")) {
                viewHolder.mIcon.setImageResource(R.drawable.yulekaixiao);
            } else if (info.getType().equals("网费话费")) {
                viewHolder.mIcon.setImageResource(R.drawable.wangfeihuafei);
            } else if (info.getType().equals("交通出行")) {
                viewHolder.mIcon.setImageResource(R.drawable.jiaotongchuxing);
            } else if (info.getType().equals("水电煤气")) {
                viewHolder.mIcon.setImageResource(R.drawable.shuidianmeiqi);
            } else if (info.getType().equals("其他花费")) {
                viewHolder.mIcon.setImageResource(R.drawable.qita);
            } else {
                viewHolder.mIcon.setImageResource(R.drawable.jin);
            }
        }
         viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnNewsItemClick !=null){
                    mOnNewsItemClick.OnClickAccount(info);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIcon;
        public TextView mtvName;
        public TextView mtvMoney;
        public TextView mtvTime;
        public TextView mtvType;


        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView
                    .findViewById(R.id.img_type);
            mtvName = (TextView) itemView
                    .findViewById(R.id.tv_type);
            mtvMoney = (TextView) itemView
                    .findViewById(R.id.tv_money);
            mtvTime= (TextView) itemView
                    .findViewById(R.id.tv_time);
            mtvType = (TextView)itemView
                    .findViewById(R.id.tv_accounttype);
        }

    }
}
