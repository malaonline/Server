package com.malalaoshi.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.OrderInfoActivity;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.course.CourseConfirmActivity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.pay.api.DeleteOrderApi;
import com.malalaoshi.android.result.OkResult;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.Number;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OrderAdapter extends BaseRecycleAdapter<OrderAdapter.ViewHolder,Order>{
    public OrderAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        holder.update(getItem(position));
    }

    static final class ViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.rl_order_id)
        protected RelativeLayout rlOrderId;

        @Bind(R.id.tv_order_id)
        protected TextView tvOrderId;

        @Bind(R.id.tv_teacher_name)
        protected TextView tvTeacherName;

        @Bind(R.id.iv_teacher_avator)
        protected SimpleDraweeView avater;

        @Bind(R.id.tv_course_name)
        protected TextView tvCourseName;

        @Bind(R.id.tv_course_address)
        protected TextView tvCourseAddress;

        @Bind(R.id.tv_order_status)
        protected TextView tvOrderStatus;

        @Bind(R.id.tv_buy_course)
        protected TextView tvBuyCourse;

        @Bind(R.id.tv_cancel_order)
        protected TextView tvCancelOrder;

        @Bind(R.id.tv_cost)
        protected TextView tvCost;

        @Bind(R.id.tv_teacher_status)
        protected TextView tvTeacherStatus;


        protected Order order;

        protected View view;

        protected ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        protected void update(Order order){
            this.order = order;
            updateItem();
        }

        public void updateItem(){
            if(order == null){
                return;
            }
            tvOrderId.setText(order.getOrder_id());
            tvTeacherName.setText(order.getTeacher_name());
            tvCourseName.setText(order.getGrade()+" "+order.getSubject());
            tvCourseAddress.setText(order.getSchool());
            String strTopay = "金额异常";
            Double toPay = order.getTo_pay();
            if(toPay!=null){
                strTopay = Number.subZeroAndDot(toPay*0.01d);
            };
            tvCost.setText(strTopay);
            Resources resources = view.getContext().getResources();
            if ("u".equals(order.getStatus())){
                rlOrderId.setBackgroundColor(resources.getColor(R.color.colorPrimary));
                tvOrderStatus.setTextColor(resources.getColor(R.color.theme_red));
                tvOrderStatus.setText("订单待支付");
                tvCancelOrder.setVisibility(View.VISIBLE);
                tvBuyCourse.setVisibility(View.VISIBLE);
                tvBuyCourse.setBackground(resources.getDrawable(R.drawable.bg_pay_order_btn));
                tvBuyCourse.setText("立即支付");
                tvBuyCourse.setTextColor(resources.getColor(R.color.white));
            }else if ("p".equals(order.getStatus())){
                rlOrderId.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPrimary));
                tvOrderStatus.setTextColor(resources.getColor(R.color.colorPrimary));
                tvOrderStatus.setText("支付成功");
                tvCancelOrder.setVisibility(View.GONE);
                tvBuyCourse.setVisibility(View.VISIBLE);
                tvBuyCourse.setBackground(resources.getDrawable(R.drawable.bg_buy_course_btn));
                tvBuyCourse.setText("再次购买");
                tvBuyCourse.setTextColor(resources.getColor(R.color.theme_red));
            }else if ("d".equals(order.getStatus())){
                rlOrderId.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorDisable));
                tvOrderStatus.setTextColor(resources.getColor(R.color.text_color_dlg));
                tvOrderStatus.setText("订单已关闭");
                tvCancelOrder.setVisibility(View.GONE);
                tvBuyCourse.setVisibility(View.VISIBLE);
                tvBuyCourse.setBackground(resources.getDrawable(R.drawable.bg_buy_course_btn));
                tvBuyCourse.setText("再次购买");
                tvBuyCourse.setTextColor(resources.getColor(R.color.theme_red));

            }else{
                rlOrderId.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPrimary));
                tvOrderStatus.setTextColor(resources.getColor(R.color.colorLightGreen));
                tvOrderStatus.setText("退款成功");
                tvCancelOrder.setVisibility(View.GONE);
                tvBuyCourse.setVisibility(View.GONE);
            }

            if (!order.is_teacher_published()){
                tvCancelOrder.setVisibility(View.GONE);
                tvBuyCourse.setVisibility(View.GONE);
                tvTeacherStatus.setVisibility(View.VISIBLE);
            }else{
                tvTeacherStatus.setVisibility(View.GONE);
            }

            String imgUrl = order.getTeacher_avatar();
            if (!EmptyUtils.isEmpty(imgUrl)){
                avater.setImageURI(Uri.parse(imgUrl));
            }

        }

        @OnClick(R.id.tv_buy_course)
        protected void onClickBuyCourse(){
            if ("u".equals(order.getStatus())){
                //订单详情页
                OrderInfoActivity.open(this.view.getContext(), order.getId()+"");
            }else{
                //确认课程页
                startCourseConfirmActivity();
            }
        }

        //启动购买课程页
        private void startCourseConfirmActivity() {
            if (order != null && order.getTeacher() != null) {
                Subject subject = Subject.getSubjectIdByName(order.getSubject());
                Long teacherId = Long.valueOf(order.getTeacher());
                if (teacherId!=null&&subject!=null){
                    CourseConfirmActivity.open(view.getContext(),teacherId,order.getTeacher_name(),order.getTeacher_avatar(),subject);
                }
            }
        }

        @OnClick(R.id.tv_cancel_order)
        protected void onClickCancelOrder(){
            if (order.getId()!=null){
                //取消订单
                startProcessDialog("正在取消订单...");
                ApiExecutor.exec(new CancelCourseOrderRequest(this, order.getId()+""));
            }else{
                MiscUtil.toast("订单id错误!");
            }

        }

        @OnClick(R.id.ll_order_item)
        protected void onItemClick(){
            //订单详情
            OrderInfoActivity.open(this.view.getContext(), order.getId()+"");
        }

        public void startProcessDialog(String message){
            DialogUtil.startCircularProcessDialog(view.getContext(),message,true,true);
        }

        public void stopProcessDialog(){
            DialogUtil.stopProcessDialog();
        }
    }


    private static final class CancelCourseOrderRequest extends BaseApiContext<ViewHolder, OkResult> {

        private String orderId;

        public CancelCourseOrderRequest(ViewHolder viewHolder, String orderId) {
            super(viewHolder);
            this.orderId = orderId;
        }

        @Override
        public OkResult request() throws Exception {
            return new DeleteOrderApi().delete(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull OkResult response) {
            get().stopProcessDialog();
            if (response.isOk()) {
                get().order.setStatus("d");
                get().updateItem();
                MiscUtil.toast("订单已取消!");
            } else {
                MiscUtil.toast("订单取消失败,请下拉刷新订单列表!");
            }
        }

        @Override
        public void onApiFinished() {
            get().stopProcessDialog();
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单状态取消失败,请检查网络!");
        }
    }
}
