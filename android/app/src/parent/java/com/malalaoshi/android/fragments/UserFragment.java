package com.malalaoshi.android.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.AboutActivity;
import com.malalaoshi.android.activitys.ModifyUserNameActivity;
import com.malalaoshi.android.activitys.ModifyUserSchoolActivity;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.dialog.RadioDailog;
import com.malalaoshi.android.dialog.SingleChoiceDialog;
import com.malalaoshi.android.entity.BaseEntity;
import com.malalaoshi.android.entity.User;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.pay.CouponActivity;
import com.malalaoshi.android.result.UserListResult;
import com.malalaoshi.android.util.AuthUtils;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.ImageUtil;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.PermissionUtil;
import com.malalaoshi.android.view.CircleImageView;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/1/24.
 */
public class UserFragment extends Fragment {

    public static final int REQUEST_CODE_PICK_IMAGE = 0x03;
    public static final int REQUEST_CODE_CAPTURE_CAMEIA = 0x04;

    //拍照相关权限
    public static final int PERMISSIONS_REQUEST_CAMERA = 0x05;
    //打开相册权限
    public static final int PERMISSIONS_REQUEST_GALLAY = 0x06;

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

    private String strAvatorLocPath;

    //图片缓存
    private ImageLoader imageLoader;

    private Bitmap bmpAvatar;

    private final BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.ACTION_LOGINED.equals(intent.getAction())) {
                updateUI();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        initDatas();
        initViews();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        IntentFilter intentFilter = new IntentFilter(UserManager.ACTION_LOGINED);
        MalaContext.getLocalBroadcastManager().registerReceiver(loginReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_UPDATE_USERCENTER_UI:
                updateUI();
                break;
            case BusEvent.BUS_EVENT_RELOAD_USERCENTER_DATA:
                reloadDatas();
                break;

        }
    }

    private void initDatas() {
        imageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        if (UserManager.getInstance().isLogin()) {
            loadDatas();
        }
    }

    private void initViews() {
        //先添加缓存数据
        updateUI();
    }

    private void updateUI() {
        updateUserInfoUI();
        updateUserAvatorUI();
    }

    public void reloadDatas() {
        if (UserManager.getInstance().isLogin()) {
            loadDatas();
        } else {
            updateUI();
        }
    }

    private void loadDatas() {
        loadUserProfile();
        loadUserInfo();
    }

    private void loadUserProfile() {
        NetworkSender.getUserPolicy(new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json == null) {
                    //失败
                    loadProfoleFailed();
                }
                try {
                    updateUserProfile(new JSONObject(json.toString()));
                    updateUserAvatorUI();
                } catch (Exception e) {
                    e.printStackTrace();
                    //失败
                    loadProfoleFailed();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                //失败
                loadProfoleFailed();
            }
        });
    }

    private void loadProfoleFailed() {
        //MiscUtil.toast(R.string.load_user_info_failed);
    }

    private void updateUserProfile(JSONObject jsonObject) {
        updateUserAvator(jsonObject.optString(Constants.AVATOR));
    }

    private void updateUserAvator(String avatorUrl) {
        if (!TextUtils.isEmpty(avatorUrl)) {
            UserManager.getInstance().setAvatorUrl(avatorUrl);
        }
    }

    private void updateUserAvatorUI() {
        if (UserManager.getInstance().isLogin()) {
            String string = UserManager.getInstance().getAvatorUrl();
            if (!TextUtils.isEmpty(string)) {
                imageLoader.get(string != null ? string : "", ImageLoader.getImageListener(ivAvatar, R.drawable.default_avatar, R.drawable.default_avatar));
            }
        } else {
            //ivAvatar.setImageResource(R.drawable.default_avatar);
        }

    }

    private void loadUserInfo() {
        NetworkSender.getStuInfo(new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json == null) {
                    //失败
                    loadInfoFailed();
                }
                UserListResult userListResult = JsonUtil.parseStringData(json.toString(), UserListResult.class);
                if (userListResult != null && userListResult.getResults() != null && userListResult.getResults().get(0) != null) {
                    Log.i("UserFragment", "UserFragment:" + json.toString());
                    updateUserInfo(userListResult.getResults().get(0));
                    updateUserInfoUI();
                    return;
                }
                loadInfoFailed();
            }

            @Override
            public void onFailed(VolleyError error) {
                loadInfoFailed();
            }
        });
    }

    private void loadInfoFailed() {
        MiscUtil.toast(R.string.load_user_info_failed);
    }

    private void updateUserInfo(User user) {
        updateStuName(user.getStudent_name());
        updateSchool(user.getStudent_school_name());
    }

    private void updateSchool(String school) {
        if (!TextUtils.isEmpty(school)) {
            UserManager.getInstance().setSchool(school);
        }
    }

    private void updateStuName(String name) {
        if (!TextUtils.isEmpty(name)) {
            UserManager.getInstance().setStuName(name);
        }
    }

    private void updateUserInfoUI() {
        if (UserManager.getInstance().isLogin()) {
            tvUserName.setText(UserManager.getInstance().getStuName());
            tvStuName.setText(UserManager.getInstance().getStuName());
            tvUserCity.setText(UserManager.getInstance().getCity());
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvUserName.setText("点击登录");
            tvStuName.setText("");
            tvUserCity.setText("");
            btnLogout.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.iv_user_avatar)
    public void OnClickUserAvatar(View view) {
        if (checkLogin() == false) return;
        ArrayList<BaseEntity> datas = new ArrayList<>();
        datas.add(new BaseEntity(1L, "拍照"));
        datas.add(new BaseEntity(2L, "相册"));
        SingleChoiceDialog dailog = SingleChoiceDialog.newInstance(0, 0, datas);
        dailog.setOnSingleChoiceClickListener(new SingleChoiceDialog.OnSingleChoiceClickListener() {
            @Override
            public void onChoiceClick(View view, BaseEntity entity) {
                if (entity.getId() == 1L) {
                    getPhotoFromCamera();
                    //
                } else if (entity.getId() == 2L) {
                    getPhotoFromGallay();
                }
            }
        });
        dailog.show(getFragmentManager(), SingleChoiceDialog.class.getName());
    }


    private void getPhotoFromCamera() {
        //检测权限
        List<String> permStrings = PermissionUtil.checkPermission(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA});

        if (permStrings == null) {
            Toast.makeText(getContext(), "权限设置错误!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (permStrings.size() == 0) {
            takePhoto();
        } else {
            //请求权限
            PermissionUtil.requestPermissions(this, permStrings, PERMISSIONS_REQUEST_CAMERA);
        }
    }

    //拍照
    private void takePhoto() {
        String cachePath = ImageUtil.getAppDir("cache");
        if (cachePath != null) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //String outFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/malaonline";
                File dir = new File(cachePath);
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String timeStamp = format.format(new Date());
                String imageFileName = timeStamp + ".png";

                File image = new File(dir, imageFileName);
                strAvatorLocPath = image.getAbsolutePath();
                //strAvatorLocPath = outFilePath + "/" + System.currentTimeMillis() + ".png";
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                this.startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), "没有找到储存目录", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    //从相册获取照片
    private void getPhotoFromGallay() {
        //检测权限
        List<String> permStrings = PermissionUtil.checkPermission(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});

        if (permStrings == null) {
            Toast.makeText(getContext(), "权限设置错误!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (permStrings.size() == 0) {
            openSysGallay();
        } else {
            //请求权限
            PermissionUtil.requestPermissions(this, permStrings, PERMISSIONS_REQUEST_GALLAY);
        }
    }

    private void openSysGallay() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                permissionsResultCamera(grantResults);
                break;
            }
            case PERMISSIONS_REQUEST_GALLAY:
                permissionsResultGallay(grantResults);
                break;
        }
    }

    private void permissionsResultGallay(int[] grantResults) {
        //如果请求被取消，那么 result 数组将为空
        boolean res = PermissionUtil.permissionsResult(grantResults);
        if (res) {
            // 已经获取对应权限
            getPhotoFromCamera();
        } else {
            // 未获取到授权，取消需要该权限的方法
            Toast.makeText(getContext(), "缺少拍照相关权限", Toast.LENGTH_SHORT).show();
        }
    }

    private void permissionsResultCamera(int[] grantResults) {
        //如果请求被取消，那么 result 数组将为空
        boolean res = PermissionUtil.permissionsResult(grantResults);
        if (res) {
            // 已经获取对应权限
            getPhotoFromCamera();
        } else {
            // 未获取到授权，取消需要该权限的方法
            Toast.makeText(getContext(), "缺少读取系统相册相关权限", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rl_user_name)
    public void OnClickUserName(View view) {
        if (checkLogin() == false) return;
        Intent intent = new Intent(getActivity(), ModifyUserNameActivity.class);
        intent.putExtra(ModifyUserNameActivity.EXTRA_USER_NAME, UserManager.getInstance().getStuName());
        startActivityForResult(intent, ModifyUserNameActivity.RESULT_CODE_NAME);

    }

    @OnClick(R.id.rl_user_school)
    public void OnClickUserSchool(View view) {
        if (checkLogin() == false) return;
        Intent intent = new Intent(getActivity(), ModifyUserSchoolActivity.class);
        intent.putExtra(ModifyUserSchoolActivity.EXTRA_USER_GRADE, UserManager.getInstance().getGradeId());
        intent.putExtra(ModifyUserSchoolActivity.EXTRA_USER_SCHOOL, UserManager.getInstance().getSchool());
        startActivity(intent);
    }

    @OnClick(R.id.rl_user_city)
    public void OnClickUserCity(View view) {
        if (checkLogin() == false) return;
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        ArrayList<BaseEntity> datas = new ArrayList<>();
        initCityDatas(datas);
        RadioDailog dailog = RadioDailog.newInstance(width, height, "选择城市", datas);
        dailog.setOnOkClickListener(new RadioDailog.OnOkClickListener() {
            @Override
            public void onOkClick(View view, BaseEntity entity) {
                if (entity != null) {
                    /*if (userCity == null || userCity.getId() != entity.getId()) {
                        userCity = entity;
                        tvUserCity.setText(userCity.getName() != null ? userCity.getName() : "");
                    }*/
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
    public void OnClickUserSchoolShip(View view) {
        if (checkLogin() == false) return;
        Intent intent = new Intent(getContext(), CouponActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_about_mala)
    public void OnClickAboutMala(View view) {
        Intent intent = new Intent(getContext(), AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_logout)
    public void OnClickLogout(View view) {
        //清除本地登录信息
        UserManager.getInstance().logout();
        //更新UI
        updateUI();
        //跳转到登录页面
        AuthUtils.redirectLoginActivity(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ModifyUserNameActivity.RESULT_CODE_NAME) {
            //String userName = data.getStringExtra(ModifyUserNameActivity.EXTRA_USER_NAME);
            //tvStuName.setText(userName);
            //tvUserName.setText(userName);
            updateUI();
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE:
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

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
        if (path != null && !path.isEmpty()) {
            int width = getResources().getDimensionPixelSize(R.dimen.avatar_width);
            int height = getResources().getDimensionPixelSize(R.dimen.avatar_height);
            bmpAvatar = ImageUtil.decodeSampledBitmapFromFile(path, 2 * width, 2 * height, ImageCache.getInstance(MalaApplication.getInstance()));
            //ivAvatar.setImageBitmap(bitmap);
            String cachePath = ImageUtil.getAppDir("cache");
            if (cachePath != null) {
                strAvatorLocPath = ImageUtil.saveBitmap(cachePath, "avatar.png", bmpAvatar);
                if (strAvatorLocPath != null) {
                    uploadFile();
                } else {
                    Toast.makeText(getContext(), "文件读写错误", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void uploadFile() {
        DialogUtil.startCircularProcessDialog(getContext(), "正在上传...", false, false);

        NetworkSender.setUserAvatar(strAvatorLocPath, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json == null || json.toString().isEmpty()) {
                    setAvatarFailed(-1);
                    return;
                }
                try {
                    JSONObject jo = new JSONObject(json.toString());
                    if (jo != null && jo.optBoolean(Constants.DONE, false)) {
                        Log.i("UserFragment", "Set user avator succeed : " + json.toString());
                        setAvatarSucceeded();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setAvatarFailed(-1);
            }

            @Override
            public void onFailed(VolleyError error) {
                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 409) {
                    setAvatarFailed(error.networkResponse.statusCode);
                    return;
                }
                setAvatarFailed(-1);
            }
        });
    }

    private void setAvatarSucceeded() {
        if (bmpAvatar != null) {
            ivAvatar.setImageBitmap(bmpAvatar);
        }
        MiscUtil.toast(R.string.usercenter_set_avator_succeed);
        DialogUtil.stopProcessDialog();
    }

    private void setAvatarFailed(int errorCode) {
        if (errorCode == 409) {
            MiscUtil.toast(R.string.usercenter_set_avator_failed_no_permission);
        } else {
            MiscUtil.toast(R.string.usercenter_set_avator_failed);
        }
        DialogUtil.stopProcessDialog();
    }

    private boolean checkLogin() {
        if (UserManager.getInstance().isLogin()) {
            return true;
        } else {
            AuthUtils.redirectLoginActivity(getContext());
            return false;
        }
    }

}
