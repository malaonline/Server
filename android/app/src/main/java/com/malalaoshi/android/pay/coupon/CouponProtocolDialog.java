package com.malalaoshi.android.pay.coupon;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/5/11.
 */
public class CouponProtocolDialog extends DialogFragment {

    @Bind(R.id.tv_coupon_protocol)
    protected TextView tvCouponProtocol;

      public static CouponProtocolDialog newInstance() {
          CouponProtocolDialog f = new CouponProtocolDialog();
          return f;
      }

      @Override
      public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setStyle(DialogFragment.STYLE_NO_TITLE, 0);
      }


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
         /* this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
              @Override
              public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                  // TODO Auto-generated method stub 返回键关闭dialog
                  if (keyCode == KeyEvent.KEYCODE_BACK) {
                      dismiss();
                      return true;
                  }
                  return false;
              }
          });*/
          View view = inflater.inflate(R.layout.dialog_coupon_protocol, container, false);
          ButterKnife.bind(this, view);
          //tvCouponProtocol.setMovementMethod(ScrollingMovementMethod.getInstance());
          return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.coupon_protocol_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.coupon_protocol_dialog_height);
        Window window;
        if (getDialog() != null) {
            window = getDialog().getWindow();
        } else {
            // This DialogFragment is used as a normal fragment, not a dialog
            window = getActivity().getWindow();
        }
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = width;
            lp.height = height;//WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit(View view){
        dismiss();
    }

}
