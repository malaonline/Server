package com.malalaoshi.android.pay;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.event.ChoiceCouponEvent;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.EventDispatcher;
import com.malalaoshi.android.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Coupon Activity.
 * Created by tianwei on 1/24/16.
 */
public class CouponListFragment extends Fragment {

    public static CouponListFragment newInstance() {
        CouponListFragment fragment = new CouponListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.listview)
    protected ListView listView;

    private CouponAdapter adapter;

    public CouponListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coupon_list, container, false);
        ButterKnife.bind(this, view);
        adapter = new CouponAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CouponEntity entity = (CouponEntity) adapter.getItem(position);
                if (entity == null && entity.isUsed() ||
                        entity.getExpired_at() < System.currentTimeMillis()) {
                    return;
                }
                EventDispatcher.getInstance().post(new ChoiceCouponEvent(entity));
                adapter.setCheck(position);
            }
        });
        fakeData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void fetchData() {
        NetworkSender.getCouponList(new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                Log.i("AABB", json.toString());
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.i("AABB", error.toString());
            }
        });
    }

    private void fakeData() {
        List<CouponEntity> entities = new ArrayList<>();
        CouponEntity entity = new CouponEntity();
        entity.setAmount(10000 + "");
        entity.setDescription("我们是个好老师");
        entity.setExpired_at(System.currentTimeMillis() - 2000);
        entity.setName("买一送一");
        entity.setUsed(false);
        entities.add(entity);
        entity = new CouponEntity();
        entity.setAmount(1000 + "");
        entity.setDescription("我们是个好老师");
        entity.setExpired_at(System.currentTimeMillis() + 20000);
        entity.setName("买一送一");
        entity.setUsed(false);
        entities.add(entity);
        entity = new CouponEntity();
        entity.setAmount(10000 + "");
        entity.setDescription("我们是个好老师");
        entity.setExpired_at(System.currentTimeMillis() - 2000);
        entity.setName("买一送一");
        entity.setUsed(true);
        entities.add(entity);
        entity = new CouponEntity();
        entity.setAmount(10000 + "");
        entity.setDescription("我们是个好老师");
        entity.setExpired_at(System.currentTimeMillis() - 2000);
        entity.setName("买一送一");
        entity.setUsed(false);
        entities.add(entity);
        adapter.addAll(entities);
        adapter.notifyDataSetChanged();
    }

    static final class CouponAdapter extends MalaBaseAdapter<CouponEntity> {

        private int grayColor;
        private int redColor;
        private int titleColor;
        private int subTitleColor;

        public CouponAdapter(Context context) {
            super(context);
            grayColor = context.getResources().getColor(R.color.text_color_gray_db);
            redColor = context.getResources().getColor(R.color.coupon_red);
            titleColor = context.getResources().getColor(R.color.coupon_title);
            subTitleColor = context.getResources().getColor(R.color.coupon_sub_title);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_coupon_item, null);
            ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void fillView(int position, View convertView, CouponEntity data) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (data.getExpired_at() < System.currentTimeMillis() && !data.isUsed()) {
                holder.layout.setImageResource(R.drawable.ic_coupon_exp);
                holder.currencyView.setTextColor(grayColor);
                holder.amountView.setTextColor(grayColor);
                holder.conditionView.setTextColor(grayColor);
                holder.titleView.setTextColor(grayColor);
                holder.statusView.setTextColor(grayColor);
                holder.statusView.setText(R.string.coupon_expired);
                holder.statusView.setVisibility(View.VISIBLE);
            } else {
                if (data.isUsed()) {
                    holder.layout.setImageResource(R.drawable.ic_coupon_used);
                    holder.statusView.setVisibility(View.GONE);
                } else {
                    holder.layout.setImageResource(R.drawable.ic_coupon_nor);
                    holder.statusView.setVisibility(View.VISIBLE);
                    holder.statusView.setText(R.string.coupon_not_use);
                }
                holder.currencyView.setTextColor(redColor);
                holder.amountView.setTextColor(redColor);
                holder.conditionView.setTextColor(subTitleColor);
                holder.titleView.setTextColor(titleColor);
                holder.statusView.setTextColor(redColor);
            }
            holder.amountView.setText(data.getAmount());
            holder.conditionView.setText(data.getDescription());
            holder.expireView.setText(MiscUtil.formatDate(data.getExpired_at()));
            holder.titleView.setText(data.getName());
            holder.useTypeView.setText(data.getUserType());
            holder.choiceView.setVisibility(data.isCheck() ? View.VISIBLE : View.GONE);
        }

        public void setCheck(int position) {
            for (int i = 0; i < getCount(); i++) {
                if (i == position) {
                    getList().get(i).setCheck(true);
                } else {
                    getList().get(i).setCheck(false);
                }
            }
            notifyDataSetChanged();
        }

        static final class ViewHolder {
            @Bind(R.id.tv_amount)
            TextView amountView;
            @Bind(R.id.tv_title)
            TextView titleView;
            @Bind(R.id.tv_condition)
            TextView conditionView;
            @Bind(R.id.tv_status)
            TextView statusView;
            @Bind(R.id.iv_choice)
            ImageView choiceView;
            @Bind(R.id.tv_expire)
            TextView expireView;
            @Bind(R.id.tv_use_type)
            TextView useTypeView;
            @Bind(R.id.rl_layout)
            ImageView layout;
            @Bind(R.id.iv_currency)
            TextView currencyView;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

        }
    }
}
