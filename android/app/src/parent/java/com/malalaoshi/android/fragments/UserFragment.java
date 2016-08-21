package com.malalaoshi.android.fragments;

import android.Manifest;
import android.app.Activity;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.AboutActivity;
import com.malalaoshi.android.activitys.CollcetionListActivity;
import com.malalaoshi.android.activitys.OrderListActivity;
import com.malalaoshi.android.api.PostAvatarApi;
import com.malalaoshi.android.api.StudentInfoApi;
import com.malalaoshi.android.comment.CommentActivity;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.usercenter.api.AddStudentNameApi;
import com.malalaoshi.android.core.usercenter.api.UserProfileApi;
import com.malalaoshi.android.core.usercenter.entity.AddStudentName;
import com.malalaoshi.android.core.usercenter.entity.UserProfile;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.dialog.SingleChoiceDialog;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.dialogs.SingleEditDialog;
import com.malalaoshi.android.entity.BaseEntity;
import com.malalaoshi.android.entity.DoneModel;
import com.malalaoshi.android.entity.User;
import com.malalaoshi.android.events.EventType;
import com.malalaoshi.android.events.NoticeEvent;
import com.malalaoshi.android.pay.coupon.CouponActivity;
import com.malalaoshi.android.result.UserListResult;
import com.malalaoshi.android.util.AuthUtils;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.ImageUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.PermissionUtil;
import com.malalaoshi.android.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by kang on 16/1/24.
 */
public class UserFragment extends BaseFragment {

    public static final int REQUEST_CODE_PICK_IMAGE = 0x03;
    public static final int REQUEST_CODE_CAPTURE_CAMEIA = 0x04;

    //拍照相关权限
    public static final int PERMISSIONS_REQUEST_CAMERA = 0x05;
    //打开相册权限
    public static final int PERMISSIONS_REQUEST_GALLAY = 0x06;

    @Bind(R.id.tv_user_name)
    protected TextView tvUserName;

    @Bind(R.id.iv_user_avatar)
    protected MalaImageView ivAvatar;

    @Bind(R.id.tv_unpaid_orders)
    protected ImageView tvUnpaidOrders;

    @Bind(R.id.tv_uncomment_course)
    protected ImageView tvUncommentCourse;

    @Bind(R.id.btn_logout)
    protected Button btnLogout;

    private String strUserName;

    private String strAvatarLocPath;

    private final BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.ACTION_LOGINED.equals(intent.getAction()) ||
                    UserManager.ACTION_LOGOUT.equals(intent.getAction())) {
                if (!UserManager.getInstance().isLogin()) {
                    setUserLoginOutInfo();
                    setUserLoginOutAvatar();
                } else {
                    loadLocalViews();
                    loadData();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        initData();
        initViews();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        IntentFilter intentFilter = new IntentFilter(UserManager.ACTION_LOGINED);
        intentFilter.addAction(UserManager.ACTION_LOGOUT);
        MalaContext.getLocalBroadcastManager().registerReceiver(loginReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        MalaContext.getLocalBroadcastManager().unregisterReceiver(loginReceiver);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_LOGOUT_SUCCESS:
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
            case BusEvent.BUS_EVENT_UPDATE_USER_NAME_SUCCESS:
            case BusEvent.BUS_EVENT_BACKGROUND_LOAD_USERCENTER_DATA:
                Log.d("UserFragment", "start loadData");
                reloadData();
                break;

        }
    }

    public void onEventMainThread(NoticeEvent event) {
        switch (event.getEventType()) {
            case EventType.BUS_EVENT_NOTICE_MESSAGE:
                if (event.getUnpayCount() != null && event.getUnpayCount() > 0) {
                    tvUnpaidOrders.setVisibility(View.VISIBLE);
                } else {
                    tvUnpaidOrders.setVisibility(View.GONE);
                }
                if (event.getUncommentCount() != null && event.getUncommentCount() > 0) {
                    tvUncommentCourse.setVisibility(View.VISIBLE);
                } else {
                    tvUncommentCourse.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void initData() {
        if (UserManager.getInstance().isLogin()) {
            loadData();
        }
    }

    private void initViews() {
        loadLocalViews();
    }

    private void loadLocalViews() {
        if (UserManager.getInstance().isLogin()) {
            setUserLoginInAvatar(UserManager.getInstance().getAvatorUrl());
            setUserLoginInInfo();
        } else {
            setUserLoginOutInfo();
            setUserLoginOutAvatar();
        }
    }

    public void reloadData() {
        if (UserManager.getInstance().isLogin()) {
            loadLocalViews();
            loadData();
        } else {
            loadLocalViews();
        }
    }

    private void loadData() {
        ApiExecutor.exec(new LoadUserProfileRequest(this));
        ApiExecutor.exec(new FetchStudentInfoRequest(this));
    }

    private void onLoadProfileFailed() {
        //MiscUtil.toast(R.string.load_user_info_failed);
    }

    private void onUpdateUserProfile(UserProfile user) {
        if (user != null) {
            if (!StringUtil.compareUrls(user.getAvatar(), UserManager.getInstance().getAvatorUrl())) {
                setUserLoginInAvatar(user.getAvatar());
            }
        }
    }

    private void onLoadUserInfoSuccess(@NonNull UserListResult result) {
        if (result.getResults() != null && result.getResults().get(0) != null) {
            updateUserInfo(result.getResults().get(0));
            setUserLoginInInfo();
        } else {
            onLoadInfoFailed();
        }
    }

    private void onLoadInfoFailed() {
        MiscUtil.toast(R.string.load_user_info_failed);
    }

    private void updateUserInfo(User user) {
        String school = user.getStudent_school_name();
        if (!TextUtils.isEmpty(school)) {
            UserManager.getInstance().setSchool(school);
        }
        String name = user.getStudent_name();
        if (!TextUtils.isEmpty(name)) {
            UserManager.getInstance().setStuName(name);
        }
    }

    @OnClick(R.id.iv_user_avatar)
    public void OnClickUserAvatar(View view) {
        if (!checkLogin()) return;
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
        List<String> permStrings = PermissionUtil.checkPermission(getContext(), new String[]{Manifest.permission
                .WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA});

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
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File dir = new File(cachePath);
                String imageFileName = System.currentTimeMillis() + ".png";
                File image = new File(dir, imageFileName);
                strAvatarLocPath = image.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                Log.e("UserFragment", "takePhoto:" + strAvatarLocPath);
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
        List<String> permStrings = PermissionUtil.checkPermission(getContext(), new String[]{Manifest.permission
                .WRITE_EXTERNAL_STORAGE});

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
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    @OnClick(R.id.tv_user_name)
    public void OnClickUserName(View view) {
        if (checkLogin() == false) return;
        DialogUtil.showSingleEditDialog(
                getFragmentManager(), tvUserName.getText().toString(),
                "取消", "保存", new SingleEditDialog.OnCloseListener() {
                    @Override
                    public void onLeftClick(EditText editText) {

                    }

                    @Override
                    public void onRightClick(EditText editText) {
                        strUserName = editText.getText().toString();
                        postModifyUserName();
                    }
                }, false, true);
    }

    private void onChangeStudentNameSuccess(AddStudentName data) {
        if (data.isDone()) {
            MiscUtil.toast(R.string.usercenter_set_student_succeed);
            updateStuName(strUserName);
            setUserLoginInInfo();
        } else {
            MiscUtil.toast(R.string.usercenter_set_student_failed);
        }
    }

    private void updateStuName(String strUserName) {
        if (!TextUtils.isEmpty(strUserName)) {
            UserManager.getInstance().setStuName(strUserName);
        }
    }

    private void postModifyUserName() {
        if (TextUtils.isEmpty(strUserName)) {
            MiscUtil.toast(R.string.usercenter_student_empty);
            return;
        }
        ApiExecutor.exec(new ModifyStudentNameRequest(this, strUserName));
    }

    @OnClick(R.id.iv_my_collection)
    public void OnClickUserCollection(View view) {
        if (checkLogin() == false) return;
        CollcetionListActivity.launch(getActivity());
    }

    @OnClick(R.id.iv_my_orders)
    public void OnClickUserOrders(View view) {
        StatReporter.clickOrders(getStatName());
        if (!checkLogin()) return;
        Intent intent = new Intent(getContext(), OrderListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.iv_my_comment)
    public void OnClickComments(View view) {
        if (checkLogin() == false) return;
        CommentActivity.launch(getActivity());
    }

    @OnClick(R.id.rl_user_schoolship)
    public void OnClickUserSchoolShip(View view) {
        StatReporter.clickScholarship(getStatName());
        if (!checkLogin()) return;
        CouponActivity.launch(getActivity(), false);
    }

    @OnClick(R.id.rl_about_mala)
    public void OnClickAboutMala(View view) {
        StatReporter.aboutMalaTeacher();
        Intent intent = new Intent(getContext(), AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_logout)
    public void OnClickLogout(View view) {
        DialogUtil.showDoubleButtonPromptDialog(getFragmentManager(), R.drawable.ic_logout, "确认退出?", "确认", "取消", new
                PromptDialog.OnCloseListener() {
                    @Override
                    public void onLeftClick() {
                        //清除本地登录信息
                        UserManager.getInstance().logout();
                        //更新UI
                        loadLocalViews();
                        //跳转到登录页面
                        //AuthUtils.redirectLoginActivity(getContext());
                        StatReporter.userLogOut();
                    }

                    @Override
                    public void onRightClick() {

                    }
                }, true, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("UserFragment", "onActivityResult:" + strAvatarLocPath);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE:
                    dealPickImage(data);
                    break;
                case REQUEST_CODE_CAPTURE_CAMEIA:
                    postUserAvator(strAvatarLocPath);
                    break;
            }
        }
    }

    private void dealPickImage(Intent data) {
        Cursor cursor = null;
        try {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            if (EmptyUtils.isEmpty(picturePath)) {
                MiscUtil.toast("照片选取失败!");
                return;
            }
            postUserAvator(picturePath);
        } catch (Exception e) {
            MiscUtil.toast("照片选取失败!");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void postUserAvator(String path) {
        if (path != null && !path.isEmpty()) {
            int width = getResources().getDimensionPixelSize(R.dimen.avatar_width);
            int height = getResources().getDimensionPixelSize(R.dimen.avatar_height);
            Bitmap bmpAvatar = ImageUtil.decodeSampledBitmapFromFile(path, 2 * width, 2 * height);
            String cachePath = ImageUtil.getAppDir("cache");
            if (cachePath != null) {
                String[] result = path.split("/");
                strAvatarLocPath = ImageUtil.saveBitmap(cachePath, result[result.length - 1], bmpAvatar);
                if (strAvatarLocPath != null) {
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
        startProcessDialog("正在上传...");
        ApiExecutor.exec(new UploadAvatar(this, strAvatarLocPath));
    }

    private void onSetAvatarSuccess(DoneModel response) {
        if (response != null && response.isDone()) {
            setUserLoginInAvatar(strAvatarLocPath);
            MiscUtil.toast(R.string.usercenter_set_avator_succeed);
            return;
        }
        onSetAvatarFailed(-1);
    }

    private void onSetAvatarFailed(int errorCode) {
        if (errorCode == 409) {
            MiscUtil.toast(R.string.usercenter_set_avator_failed_no_permission);
        } else {
            MiscUtil.toast(R.string.usercenter_set_avator_failed);
        }
    }

    private void setUserLoginInInfo() {
        tvUserName.setText(UserManager.getInstance().getStuName());
        btnLogout.setVisibility(View.VISIBLE);
    }

    private void setUserLoginOutInfo() {
        tvUserName.setText("点击登录");
        btnLogout.setVisibility(View.GONE);
    }

    private void setUserLoginInAvatar(String url) {
        ivAvatar.loadCircleImage(url, R.drawable.ic_default_teacher_avatar);
        UserManager.getInstance().setAvatorUrl(url);
    }


    private void setUserLoginOutAvatar() {
        Glide.with(this)
                .load(R.drawable.default_avatar)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(ivAvatar);
        UserManager.getInstance().setAvatorUrl("");
    }

    private boolean checkLogin() {
        if (UserManager.getInstance().isLogin()) {
            return true;
        } else {
            AuthUtils.redirectLoginActivity(getContext());
            return false;
        }
    }

    @Override
    public String getStatName() {
        return "我的页面";
    }


    private static final class LoadUserProfileRequest extends BaseApiContext<UserFragment, UserProfile> {

        public LoadUserProfileRequest(UserFragment userFragment) {
            super(userFragment);
        }

        @Override
        public UserProfile request() throws Exception {
            return new UserProfileApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull UserProfile user) {
            get().onUpdateUserProfile(user);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onLoadProfileFailed();
        }
    }

    private static final class FetchStudentInfoRequest extends BaseApiContext<UserFragment, UserListResult> {

        public FetchStudentInfoRequest(UserFragment userFragment) {
            super(userFragment);
        }

        @Override
        public UserListResult request() throws Exception {
            return new StudentInfoApi().getStudentInfo();
        }

        @Override
        public void onApiSuccess(@NonNull UserListResult response) {
            get().onLoadUserInfoSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onLoadInfoFailed();
        }
    }

    private static final class ModifyStudentNameRequest extends BaseApiContext<UserFragment, AddStudentName> {

        private String name;

        public ModifyStudentNameRequest(UserFragment userFragment, String name) {
            super(userFragment);
            this.name = name;
        }

        @Override
        public AddStudentName request() throws Exception {
            return new AddStudentNameApi().get(this.name);
        }

        @Override
        public void onApiSuccess(@NonNull AddStudentName data) {
            get().onChangeStudentNameSuccess(data);
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
        }
    }


    private static final class UploadAvatar extends BaseApiContext<UserFragment, DoneModel> {
        private String filePath;

        public UploadAvatar(UserFragment userFragment, String filePath) {
            super(userFragment);
            this.filePath = filePath;
        }

        @Override
        public DoneModel request() throws Exception {
            return new PostAvatarApi().post(filePath);
        }

        @Override
        public void onApiSuccess(@NonNull DoneModel response) {
            get().onSetAvatarSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
            get().onSetAvatarFailed(exception.hashCode());
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().stopProcessDialog();
        }
    }

}
