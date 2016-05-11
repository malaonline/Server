package com.malalaoshi.android.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.dialog.CouponProtocolDialog;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Coupon Activity.
 * Created by tianwei on 1/24/16.
 */
public class CouponActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    private static final String EXTRA_CAN_SELECT = "extra_can_select";
    public static final String EXTRA_COUPON = "extra_coupon_selected";

    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;
    //The coupon that is chose currently.
    private CouponEntity couponEntity;

    public static void launch(Context context, boolean canSelect) {
        Intent intent = new Intent(context, CouponActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_CAN_SELECT, canSelect);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void launch(Activity activity, int requestCode, CouponEntity coupon) {
        Intent intent = new Intent(activity, CouponActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_CAN_SELECT, true);
        bundle.putParcelable(EXTRA_COUPON, coupon);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);
        titleBarView.setTitle(R.string.scholarship);
        titleBarView.setOnTitleBarClickListener(this);
        initFragment();
        if (!UserManager.getInstance().isLogin()) {
            finish();
            UserManager.getInstance().startLoginActivity();
        }
    }

    private void initFragment() {
        boolean canSelect = false;
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            canSelect = bundle.getBoolean(EXTRA_CAN_SELECT);
            couponEntity = bundle.getParcelable(EXTRA_COUPON);
        }
        CouponListFragment fragment = CouponListFragment.newInstance(canSelect, couponEntity);
        fragment.setOnCouponSelectListener(new CouponListFragment.OnCouponSelectListener() {
            @Override
            public void onCouponSelect(CouponEntity entity) {
                couponEntity = entity;
            }
        });
        FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(),
                null, fragment, "couponfragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTitleLeftClick() {
        finishWithResult();
    }

    private void finishWithResult() {
        Intent intent = new Intent();
        if (couponEntity != null) {
            if (couponEntity.isCheck()) {
                intent.putExtra(EXTRA_COUPON, couponEntity);
            }
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    @Override
    public void onTitleRightClick() {
        //奖学金使用规则
        CouponProtocolDialog couponProtocolDialog = CouponProtocolDialog.newInstance();
        couponProtocolDialog.show(getSupportFragmentManager(),CouponProtocolDialog.class.getName());
    }

    @Override
    protected String getStatName() {
        return "优惠劵";
    }
}
