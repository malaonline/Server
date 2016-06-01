package com.malalaoshi.android.adapter;

import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.OrderInfoActivity;
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


public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder>{
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //没有更多数据,到底了
    public static final int NODATA_LOADING = 2;
    //没有更多数据,到底了
    public static final int GONE_LOADING = 3;
    //上拉加载更多状态-默认为0
    private int load_more_status=0;

    public static final int TEACHER_LIST_PAGE_SIZE = 10;

    private  List<Order> orderList;

    public OrderRecyclerViewAdapter(List<Order> items){
        orderList = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = null;

        switch(viewType){
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_refresh_footer, null));
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, null);
                return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){
        holder.update(position);
    }

    @Override
    public int getItemCount(){
        if(orderList != null){
            return orderList.size()+1;
        }else{
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position){
        int type = TYPE_NORMAL;
        if (position==getItemCount()-1){
            type = TYPE_LOAD_MORE;
        }
        return type;
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void setMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    public int getMoreStatus(){
        return load_more_status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ViewHolder(View itemView){
            super(itemView);
        }

        protected void update(int position){}
    }

    public class NoValueViewHolder extends ViewHolder{
        protected NoValueViewHolder(View itemView){
            super(itemView);
        }
    }

    public class LoadMoreViewHolder extends ViewHolder{
        @Bind(R.id.item_load_more_icon_loading)
        protected View iconLoading;

        @Bind(R.id.iv_normal_refresh_footer_chrysanthemum)
        protected ImageView ivProgress;

        @Bind(R.id.tv_normal_refresh_footer_status)
        protected TextView tvStatusText;

        protected LoadMoreViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void update(int position){
            if (position<=2){
                iconLoading.setVisibility(View.GONE);
            }else{
                iconLoading.setVisibility(View.VISIBLE);
            }
            AnimationDrawable animationDrawable = null;
            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    tvStatusText.setText("上拉加载更多...");
                    ivProgress.setVisibility(View.GONE);
                    animationDrawable = (AnimationDrawable) ivProgress.getDrawable();
                    animationDrawable.stop();
                    break;
                case LOADING_MORE:
                    tvStatusText.setText("加载中...");
                    ivProgress.setVisibility(View.VISIBLE);
                    animationDrawable = (AnimationDrawable) ivProgress.getDrawable();
                    animationDrawable.start();
                    break;
                case NODATA_LOADING:
                    tvStatusText.setText("到底了,没有更多数据了!");
                    ivProgress.setVisibility(View.GONE);
                    animationDrawable = (AnimationDrawable) ivProgress.getDrawable();
                    animationDrawable.stop();
                    break;
                case GONE_LOADING:
                    //iconLoading.setVisibility(View.GONE);
                    break;
            }

        }

    }

    public class NormalViewHolder extends ViewHolder{

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


        protected Order order;

        protected View view;

        protected NormalViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        @Override
        protected void update(int position){
            if(position >= orderList.size()){
                return;
            }
            order = orderList.get(position);
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


    private static final class CancelCourseOrderRequest extends BaseApiContext<NormalViewHolder, OkResult> {

        private String orderId;

        public CancelCourseOrderRequest(NormalViewHolder viewHolder, String orderId) {
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
