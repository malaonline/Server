package com.malalaoshi.android.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 这个Activity是用来测试支付与优惠券功能，不是正式UI
 * Created by tianwei on 2/27/16.
 */
public class PayTestActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.btn_create)
    protected TextView createOrderView;
    @Bind(R.id.btn_pay)
    protected TextView payView;
    @Bind(R.id.btn_login)
    protected TextView loginView;

    @Bind(R.id.btn_coupong)
    protected TextView couponView;

    @Bind(R.id.tv_message)
    protected TextView messageView;

    private CreateCourseOrderResultEntity resultEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_test);
        ButterKnife.bind(this);
        createOrderView.setOnClickListener(this);
        payView.setOnClickListener(this);
        loginView.setOnClickListener(this);
        couponView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_create) {
            createOrder();
        } else if (v.getId() == R.id.btn_pay) {
            pay();
        } else if (v.getId() == R.id.btn_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_coupong) {
            startActivity(new Intent(this, CouponActivity.class));
        }
    }

    /**
     * Pay step two: Start payActivity
     */
    private void pay() {
        if (resultEntity != null) {
            PayActivity.startPayActivity(resultEntity, this);
        } else {
            messageView.setText("Order id is null");
        }
    }

    /**
     * Pay step one: Create order id
     */
    private void createOrder() {
        CreateCourseOrderEntity entity = new CreateCourseOrderEntity();
        entity.setGrade(1);
        entity.setHours(4);
        entity.setSchool(1);
        entity.setSubject(1);
        entity.setTeacher(1);
        //entity.setCoupon(2);
        List<Integer> times = new ArrayList<>();
        times.add(3);
        times.add(8);
        entity.setWeekly_time_slots(times);
        PayManager.getInstance().createOrder(entity, new ResultCallback<Object>() {
            @Override
            public void onResult(Object obj) {
                CreateCourseOrderResultEntity result = CreateCourseOrderResultEntity.parse(obj);
                if (result == null) {
                    messageView.setText("创建订单失败");
                } else {
                    resultEntity = result;
                    messageView.setText(result.getOrder_id());
                }
            }
        });
    }
}
