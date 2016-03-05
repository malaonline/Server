package com.malalaoshi.android.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.ModifyUserNameActivity;
import com.malalaoshi.android.activitys.ModifyUserSchoolActivity;
import com.malalaoshi.android.dialog.RadioDailog;
import com.malalaoshi.android.dialog.SingleChoiceDialog;
import com.malalaoshi.android.entity.BaseEntity;
import com.malalaoshi.android.entity.User;
import com.malalaoshi.android.pay.CouponActivity;
import com.malalaoshi.android.usercenter.SmsAuthActivity;
import com.malalaoshi.android.util.AuthUtils;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.ImageUtil;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.view.CircleImageView;
import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/1/24.
 */
public class UserFragment extends Fragment {

    public static final int REQUEST_CODE_PICK_IMAGE = 0x03;
    public static final int REQUEST_CODE_CAPTURE_CAMEIA = 0x04;

    private static final String USER_INFO_PATH_V1 = "/api/v1/user";

    @Bind(R.id.tv_user_name)
    protected TextView tvUserName;

    @Bind(R.id.tv_stu_name)
    protected TextView tvStuName;

    @Bind(R.id.tv_user_city)
    protected TextView tvUserCity;

    @Bind(R.id.iv_user_avatar)
    protected CircleImageView ivAvatar;

    @Bind(R.id.btn_logout)
    protected Button btnLogout;

    private String userName;
    private BaseEntity userCity;


    private String strAvatorLocPath;
    private RequestQueue requestQueue;
    //图片缓存
    private ImageLoader imageLoader;
    private User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        initDatas();
        initViews();
        return view;
    }

    private void initDatas() {
        requestQueue = MalaApplication.getHttpRequestQueue();
        imageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        if (MalaApplication.getInstance().getToken()!=null&&!MalaApplication.getInstance().getToken().isEmpty()){
            loadDatas();
        }
    }

    private void initViews() {
        //先添加缓存数据
        updateUI();
    }

    private void updateUI() {
        if (MalaApplication.getInstance().getToken()!=null&&!MalaApplication.getInstance().getToken().isEmpty()){
            btnLogout.setVisibility(View.VISIBLE);
            if (mUser!=null){
                tvUserName.setText("用户姓名");
                tvStuName.setText("学生姓名");
                tvUserCity.setText("所在城市");
                //String string = mUser.getAvatar();
                //imageLoader.get(string != null ? string : "", ImageLoader.getImageListener(ivAvatar, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));*/
            }
        }else{
            tvUserName.setText("点击登录");
            tvStuName.setText("");
            tvUserCity.setText("");
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void loadDatas() {
        String url = MalaApplication.getInstance().getMalaHost() + USER_INFO_PATH_V1;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Type listType = new TypeToken<ArrayList<MemberService>>(){}.getType();
                User user = JsonUtil.parseStringData(response, User.class);
                if (user == null) {
                    Log.e(LoginFragment.class.getName(), "school list request failed!");
                    return;
                }
                mUser = user;
                updateUI();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });
        jstringRequest.setTag(USER_INFO_PATH_V1);
        requestQueue.add(jstringRequest);
    }

    @OnClick(R.id.iv_user_avatar)
    public void OnClickUserAvatar(View view){
        if (checkLogin()==false) return;
        ArrayList<BaseEntity> datas = new ArrayList<>();
        datas.add(new BaseEntity(1L,"拍照"));
        datas.add(new BaseEntity(2L,"相册"));
        SingleChoiceDialog dailog = SingleChoiceDialog.newInstance(0, 0, datas);
        dailog.setOnSingleChoiceClickListener(new SingleChoiceDialog.OnSingleChoiceClickListener() {
            @Override
            public void onChoiceClick(View view, BaseEntity entity) {
                if (entity.getId() == 1L) {
                    getPhotoFromCamera();
                } else if (entity.getId() == 2L) {
                    getPhotoFromGallay();
                }
            }
        });
        dailog.show(getFragmentManager(), SingleChoiceDialog.class.getName());
    }

    //拍照
    private void getPhotoFromCamera() {
        /*String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
        }
        else {
            Toast.makeText(getContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }*/

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            String outFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/malaonline";
            File dir = new File(outFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            strAvatorLocPath = outFilePath + "/" + System.currentTimeMillis() + ".png";
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(strAvatorLocPath)));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
        }
        else {
            Toast.makeText(getContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    //从相册获取照片
    private void getPhotoFromGallay(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }


    @OnClick(R.id.rl_user_name)
    public void OnClickUserName(View view){
        if (checkLogin()==false) return;
        if (userName==null){
            userName = "欧阳娜娜";
        }
        Intent intent = new Intent(getActivity(), ModifyUserNameActivity.class);
        intent.putExtra(ModifyUserNameActivity.EXTRA_USER_NAME, userName);
        startActivityForResult(intent, ModifyUserNameActivity.RESULT_CODE_NAME);

    }

    @OnClick(R.id.rl_user_school)
    public void OnClickUserSchool(View view){
        if (checkLogin()==false) return;
        Intent intent = new Intent(getActivity(), ModifyUserSchoolActivity.class);
        intent.putExtra(ModifyUserSchoolActivity.EXTRA_USER_GRADE,"高三");
        intent.putExtra(ModifyUserSchoolActivity.EXTRA_USER_SCHOOL, "洛阳中学");
        startActivity(intent);
    }

    @OnClick(R.id.rl_user_city)
    public void OnClickUserCity(View view){
        if (checkLogin()==false) return;
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        ArrayList<BaseEntity> datas = new ArrayList<>();
        initCityDatas(datas);
        RadioDailog dailog = RadioDailog.newInstance(width, height, "选择城市", datas);
        dailog.setOnOkClickListener(new RadioDailog.OnOkClickListener() {
            @Override
            public void onOkClick(View view, BaseEntity entity) {
                if (entity != null) {
                    if (userCity == null || userCity.getId() != entity.getId()) {
                        userCity = entity;
                        tvUserCity.setText(userCity.getName() != null ? userCity.getName() : "");
                    }
                }

            }
        });
        dailog.show(getFragmentManager(), "tag");
    }

    private void initCityDatas(ArrayList<BaseEntity> datas) {
        BaseEntity entity = null;
        entity = new BaseEntity();
        entity.setId(1L);
        entity.setName("洛阳");
        datas.add(entity);
        entity = new BaseEntity();
        entity.setId(0L);
        entity.setName("其它");
        datas.add(entity);
    }


    @OnClick(R.id.rl_user_schoolship)
     public void OnClickUserSchoolShip(View view){
        if (checkLogin()==false) return;
        Intent intent = new Intent(getContext(),CouponActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_about_mala)
    public void OnClickAboutMala(View view){

    }

    @OnClick(R.id.btn_logout)
    public void OnClickLogout(View view){
        //清除本地登录信息
        MalaApplication.getInstance().logout();
        //清除数据
        mUser = null;
        //跟新UI
        updateUI();
        //跳转到登录页面
        AuthUtils.redirectLoginActivity(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==ModifyUserNameActivity.RESULT_CODE_NAME){
            userName = data.getStringExtra(ModifyUserNameActivity.EXTRA_USER_NAME);
            tvStuName.setText(userName);
            tvUserName.setText(userName);
        }
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_PICK_IMAGE:
                    Uri uri = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getActivity().getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    postUserAvator(picturePath);
                break;
                case REQUEST_CODE_CAPTURE_CAMEIA:
                    postUserAvator(strAvatorLocPath);
                    break;
            }
        }
    }

    private void postUserAvator(String path) {
        if (path!=null&&!path.isEmpty())
        {
            int width = getResources().getDimensionPixelSize(R.dimen.avatar_width);
            int height = getResources().getDimensionPixelSize(R.dimen.avatar_height);
            Bitmap bitmap = ImageUtil.decodeSampledBitmapFromFile(path, 2*width, 2*height, ImageCache.getInstance(MalaApplication.getInstance()));
            ivAvatar.setImageBitmap(bitmap);
        }
    }

    private boolean checkLogin(){
        if (MalaApplication.getInstance().getToken()!=null&&!MalaApplication.getInstance().getToken().isEmpty()){
            return true;
        }else{
            AuthUtils.redirectLoginActivity(getContext());
            return false;
        }
    }

}
