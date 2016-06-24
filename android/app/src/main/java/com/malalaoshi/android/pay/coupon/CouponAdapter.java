package com.malalaoshi.android.pay.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.Number;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public final class CouponAdapter extends BaseRecycleAdapter<CouponAdapter.ViewHolder, CouponEntity> {

    private int textBlueColor;
    private int textGrayColor;
    private boolean canSelect;
    private long amount;
    private CouponListFragment.OnCouponSelectListener listener;

    public CouponAdapter(Context context) {
        super(context);
        textGrayColor = context.getResources().getColor(R.color.text_color);
        textBlueColor = context.getResources().getColor(R.color.item_color_bg);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_coupon_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    private String getCondition(int fen) {
        float value = fen / 100f;
        if (value == 0) {
            return "没有金额限制";
        }
        return String.format(Locale.getDefault(), "满%.2f元可用", value);
    }

    @Override
    public void addData(List<CouponEntity> data) {
        if (data == null) {
            return;
        }
        for (CouponEntity entity : data) {
            entity.setExpired_at(entity.getExpired_at() * 1000);
            entity.setDescription(getCondition(entity.getMini_total_price()));
            entity.setExpiredDate("有效期至 " + MiscUtil.formatDate(entity.getExpired_at()));
        }
        super.addData(data);
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }

    public void setCouponSelectedListener(CouponListFragment.OnCouponSelectListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CouponEntity data = getItem(position);
        if (data.isUsed()) {
            holder.statusContainer.setBackgroundResource(R.drawable.ic_coupon_sum_bk_invalid);
            holder.conditionView.setTextColor(textGrayColor);
            holder.checkView.setVisibility(View.INVISIBLE);
            holder.statusView.setVisibility(View.VISIBLE);
            holder.statusView.setImageResource(R.drawable.ic_coupon_used);
        } else if (data.getExpired_at() < System.currentTimeMillis()) {
            holder.statusContainer.setBackgroundResource(R.drawable.ic_coupon_sum_bk_invalid);
            holder.conditionView.setTextColor(textGrayColor);
            holder.checkView.setVisibility(View.INVISIBLE);
            holder.statusView.setVisibility(View.VISIBLE);
            holder.statusView.setImageResource(R.drawable.ic_coupon_expired);
        } else if (data.getMini_total_price() > amount && canSelect) {
            holder.statusContainer.setBackgroundResource(R.drawable.ic_coupon_sum_bk_invalid);
            holder.conditionView.setTextColor(textGrayColor);
            holder.checkView.setVisibility(View.INVISIBLE);
            holder.statusView.setVisibility(View.VISIBLE);
            holder.statusView.setVisibility(View.INVISIBLE);
        } else {
            holder.statusContainer.setBackgroundResource(R.drawable.ic_coupon_sum_bk);
            holder.conditionView.setTextColor(textBlueColor);
            holder.statusView.setVisibility(View.INVISIBLE);
        }
        String amount = Number.subZeroAndDot(Double.valueOf(data.getAmount()) * 0.01);
        holder.amountView.setText(amount);
        holder.conditionView.setText(data.getDescription());
        holder.expireView.setText(data.getExpiredDate());
        holder.checkView.setVisibility(data.isCheck() ? View.VISIBLE : View.INVISIBLE);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheck(holder.getAdapterPosition(), data);
            }
        });
    }

    private void setCheck(int position, CouponEntity entity) {
        if (entity.isUsed() || entity.getExpired_at() < System.currentTimeMillis() || !canSelect) {
            return;
        }
        if (amount < entity.getMini_total_price()) {
            com.malalaoshi.android.core.utils.MiscUtil.toast("订单金额不符合使用条件");
            return;
        }
        for (int i = 0; i < getItemCount(); i++) {
            getDataList().get(i).setCheck(i == position && !entity.isCheck());
        }
        if (listener != null) {
            listener.onCouponSelect(entity);
        }
        notifyDataSetChanged();
    }

    public void setSelected(CouponEntity entity) {
        if (entity == null) {
            return;
        }
        for (CouponEntity data : getDataList()) {
            data.setCheck(data.getId() == entity.getId());
        }
        notifyDataSetChanged();
    }

    /**
     * 订单金额
     */
    public void setAmount(long amount) {
        this.amount = amount;
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {

        TextView amountView;
        TextView conditionView;
        ImageView statusView;
        ImageView checkView;
        View statusContainer;
        TextView expireView;
        View rootView;

        public ViewHolder(View view) {
            super(view);
            amountView = (TextView) view.findViewById(R.id.tv_amount);
            conditionView = (TextView) view.findViewById(R.id.tv_condition);
            statusView = (ImageView) view.findViewById(R.id.iv_status);
            checkView = (ImageView) view.findViewById(R.id.iv_check);
            statusContainer = view.findViewById(R.id.fl_status);
            expireView = (TextView) view.findViewById(R.id.tv_expire);
            rootView = view.findViewById(R.id.view_root);
        }

    }
}
