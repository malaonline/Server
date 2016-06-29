package com.malalaoshi.android.pay.coupon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.pay.api.CouponListApi;
import com.malalaoshi.android.pay.api.CouponListMoreApi;

/**
 * Coupon Activity.
 * Created by tianwei on 1/24/16.
 */
public class CouponListFragment extends BaseRefreshFragment<CouponResult> {

    public interface OnCouponSelectListener {
        void onCouponSelect(CouponEntity entity);
    }

    //Current selected
    private CouponEntity coupon;

    //订单金额
    private long amount;

    private boolean onlyValid;

    private String nextUrl;

    private CouponAdapter adapter;

    public CouponListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getStatName() {
        return "奖学金页面";
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        adapter = new CouponAdapter(getContext());
        adapter.setCouponSelectedListener(new OnCouponSelectListener() {
            @Override
            public void onCouponSelect(CouponEntity entity) {
                coupon = entity;
            }
        });
        return adapter;
    }

    @Override
    protected CouponResult refreshRequest() throws Exception {
        return new CouponListApi().get(onlyValid);
    }

    @Override
    protected CouponResult loadMoreRequest() throws Exception {
        return new CouponListMoreApi().loadMore(nextUrl, onlyValid);
    }

    @Override
    protected void refreshFinish(CouponResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
        adapter.setSelected(coupon);
    }

    @Override
    protected void loadMoreFinish(CouponResult response) {
        super.loadMoreFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected void afterCreateView() {
        initBundle();
    }

    private void initBundle() {
        Bundle bundle = getArguments();
        boolean canSelect;
        if (bundle != null) {
            canSelect = bundle.getBoolean(CouponActivity.EXTRA_CAN_SELECT, true);
            coupon = bundle.getParcelable(CouponActivity.EXTRA_COUPON);
            amount = bundle.getLong(CouponActivity.EXTRA_AMOUNT);
            onlyValid = bundle.getBoolean(CouponActivity.EXTRA_ONLY_VALID);
        } else {
            canSelect = true;
        }
        adapter.setCanSelect(canSelect);
        adapter.setAmount(amount);

        setEmptyViewText("当前暂无奖学金");
    }

    public void onBackClicked() {
        Intent intent = new Intent();
        if (coupon != null) {
            if (coupon.isCheck()) {
                intent.putExtra(CouponActivity.EXTRA_COUPON, coupon);
            }
            getActivity().setResult(Activity.RESULT_OK, intent);
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED);
        }
        getActivity().finish();
    }
}
