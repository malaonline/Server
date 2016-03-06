package com.malalaoshi.android.course;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.base.BaseFragment;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.CoursePriceUI;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.SchoolUI;
import com.malalaoshi.android.entity.Teacher;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Course confirm fragment
 * Created by tianwei on 3/5/16.
 */
public class CourseConfirmFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    public static CourseConfirmFragment newInstance(Object[] schools, Object[] prices) {
        CourseConfirmFragment fragment = new CourseConfirmFragment();
        fragment.init(schools, prices);
        return fragment;
    }

    public CourseConfirmFragment() {
        coursePrices = new ArrayList<>();
        schoolList = new ArrayList<>();
        choiceList = new ArrayList<>();
    }

    @Bind(R.id.gv_course)
    protected GridView gridView;

    @Bind(R.id.ll_place)
    protected ListView placeListView;

    @Bind(R.id.list_choice)
    protected ListView choiceListView;

    @Bind(R.id.choice_time_view)
    protected CourseDateChoiceView choiceView;

    private final List<CoursePriceUI> coursePrices;
    private final List<SchoolUI> schoolList;
    private final List<String> choiceList;
    private PriceAdapter priceAdapter;
    private SchoolAdapter schoolAdapter;
    private ChoiceAdapter choiceAdapter;
    private Teacher teacher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_confirm, null);
        ButterKnife.bind(this, view);
        initGridView();
        initSchoolListView();
        initChoiceListView();
        return view;
    }


    private void init(Object[] schools, Object[] prices) {
        final String[] gradeList = MalaApplication.getInstance()
                .getApplicationContext().getResources().getStringArray(R.array.grade_list);
        if (schools != null) {
            for (Object school : schools) {
                SchoolUI schoolUI = new SchoolUI((School) school);
                schoolList.add(schoolUI);
            }
        }
        if (prices != null) {
            String text;
            for (Object price : prices) {
                CoursePriceUI priceUI = new CoursePriceUI((CoursePrice) price);
                text = gradeList[priceUI.getPrice().getGrade().getId().intValue()];
                text += " " + ((int) priceUI.getPrice().getPrice()) + "/小时";
                priceUI.setGradePrice(text);
                coursePrices.add(priceUI);
            }
        }
    }

    private void initChoiceListView() {
        choiceAdapter = new ChoiceAdapter(getActivity());
        choiceListView.setAdapter(choiceAdapter);
    }

    private void initGridView() {
        priceAdapter = new PriceAdapter(getActivity());
        priceAdapter.addAll(coursePrices);
        gridView.setAdapter(priceAdapter);
        gridView.setOnItemClickListener(this);
    }

    private void initSchoolListView() {
        if (schoolList.size() > 1) {
            final View footView = View.inflate(getActivity(), R.layout.listview_course_foot_view, null);
            footView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    schoolAdapter.clear();
                    schoolAdapter.addAll(schoolList);
                    schoolAdapter.notifyDataSetChanged();
                    placeListView.removeFooterView(v);
                }
            });
            placeListView.addFooterView(footView);
        }
        schoolAdapter = new SchoolAdapter(getActivity());
        if (schoolList.size() > 0) {
            schoolAdapter.add(schoolList.get(0));
        }
        placeListView.setAdapter(schoolAdapter);
        placeListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == placeListView.getId()) {
            schoolAdapter.setCheckItem(position);
        }
        if (parent.getId() == gridView.getId()) {
            priceAdapter.setCurrentItem(position);
        }
    }

    private static class PriceAdapter extends MalaBaseAdapter<CoursePriceUI> {

        private int currentItem;

        public PriceAdapter(Context context) {
            super(context);
            currentItem = -1;
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            return View.inflate(context, R.layout.view_course_price_item, null);
        }

        @Override
        protected void fillView(int position, View convertView, CoursePriceUI data) {
            TextView view = (TextView) convertView;
            if (data.isCheck()) {
                view.setTextColor(Color.WHITE);
                view.setBackgroundResource(R.drawable.bg_course_price_pressed);
            } else {
                view.setTextColor(context.getResources().getColor(R.color.theme_blue));
                view.setBackgroundResource(R.drawable.bg_course_price_normal);
            }
            ((TextView) convertView).setText(data.getGradePrice());
        }

        public void setCurrentItem(int item) {
            if (currentItem == item) {
                return;
            }
            if (currentItem >= 0 && currentItem < getCount()) {
                getList().get(currentItem).setCheck(false);
            }
            if (item >= 0 && item < getCount()) {
                getList().get(item).setCheck(true);
            }
            currentItem = item;
            notifyDataSetChanged();
        }
    }

    private static class SchoolAdapter extends MalaBaseAdapter<SchoolUI> {

        private int currentCheck;

        public SchoolAdapter(Context context) {
            super(context);
            currentCheck = -1;
        }

        public void setCheckItem(int checkItem) {
            if (currentCheck == checkItem) {
                return;
            }
            if (currentCheck >= 0 && currentCheck < getCount()) {
                getList().get(currentCheck).setCheck(false);
            }
            if (checkItem >= 0 && checkItem < getCount()) {
                getList().get(checkItem).setCheck(true);
            }
            currentCheck = checkItem;
            notifyDataSetChanged();
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            View view = View.inflate(context, R.layout.view_course_place_item, null);
            ViewHolder holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.tv_name);
            holder.addressView = (TextView) view.findViewById(R.id.tv_location);
            holder.distanceView = (TextView) view.findViewById(R.id.tv_distance);
            holder.checkView = (ImageView) view.findViewById(R.id.iv_check);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void fillView(int position, View convertView, SchoolUI data) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.nameView.setText(data.getSchool().getName());
            holder.addressView.setText(data.getSchool().getAddress());
            holder.distanceView.setText("");
            holder.checkView.setImageResource(data.isCheck() ? R.drawable.ic_check : R.drawable.ic_check_out);
        }

        public class ViewHolder {
            TextView nameView;
            TextView addressView;
            TextView distanceView;
            ImageView checkView;
        }
    }

    private static class ChoiceAdapter extends MalaBaseAdapter<String> {
        public ChoiceAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            TextView textView = new TextView(context);
            return textView;
        }

        @Override
        protected void fillView(int position, View convertView, String data) {

        }
    }
}
