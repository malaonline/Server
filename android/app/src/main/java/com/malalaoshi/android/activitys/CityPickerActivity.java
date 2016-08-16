package com.malalaoshi.android.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.malalaoshi.android.core.base.BaseTitleActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.City;
import com.malalaoshi.android.fragments.CityPickerFragment;


/**
 * Created by kang on 16/8/16.
 */
public class CityPickerActivity extends BaseTitleActivity implements CityPickerFragment.OnCityClick {

    public static int RESULT_CODE_CITY = 1000;
    public static String EXTRA_CITY = "city";

    public static void openForResult(Activity activity,int requestCode) {
        Intent intent = new Intent(activity, CityPickerActivity.class);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CityPickerFragment cityPickerFragment = null;
        if(savedInstanceState==null){
            cityPickerFragment = (CityPickerFragment) Fragment.instantiate(this, CityPickerFragment.class.getName(), getIntent().getExtras());
            replaceFragment(cityPickerFragment);
        }else{
            cityPickerFragment = (CityPickerFragment) getSupportFragmentManager().getFragments().get(0);
        }
        cityPickerFragment.setOnCityClick(this);
    }

    @Override
    protected String getStatName() {
        return "选择城市";
    }

    @Override
    public void onCityClick(City city) {
        if (city!=null){
            Long cityId = UserManager.getInstance().getCityId();
            if (null==cityId||cityId.longValue()!=city.getId().longValue()){
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CITY, city);
                setResult(RESULT_CODE_CITY, intent);
                //修改用户当前城市
                UserManager.getInstance().setCityId(city.getId());
                UserManager.getInstance().setCity(city.getName());
            }
            finish();
        }

    }
}
