package com.malalaoshi.android.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.RadioAdapter;
import com.malalaoshi.android.entity.BaseEntity;
import com.malalaoshi.android.view.ExpandedHeightGridView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/1/25.
 */
public class RadioDailog extends DialogFragment implements AdapterView.OnItemClickListener {
    public static String ARGS_DIALOG_TITLE = "title";
    public static String ARGS_DIALOG_DATAS = "datas";
    public static String ARGS_DIALOG_WIDTH = "width";
    public static String ARGS_DIALOG_HEIGHT = "height";

    private int width;
    private int height;
    private String titleText;
    private ArrayList<BaseEntity> datas = new ArrayList<>();

    @Bind(R.id.tv_dialog_title)
    protected TextView tvDialogTitle;

    @Bind(R.id.listview_dialog)
    ExpandedHeightGridView listView;

    private RadioAdapter radioAdapter;

    private BaseEntity selectedEntity;

    private OnOkClickListener okClickListener;

    public static RadioDailog newInstance(int width, int height, String title, ArrayList<BaseEntity> list) {
        RadioDailog f = new RadioDailog();
        Bundle args = new Bundle();
        args.putInt(ARGS_DIALOG_WIDTH, width);
        args.putInt(ARGS_DIALOG_HEIGHT, height);
        args.putString(ARGS_DIALOG_TITLE, title);
        args.putParcelableArrayList(ARGS_DIALOG_DATAS,list);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleText = getArguments().getString(ARGS_DIALOG_TITLE);
        datas.clear();
        ArrayList<BaseEntity> list = getArguments().getParcelableArrayList(ARGS_DIALOG_DATAS);
        if (list!=null){
            datas.addAll(0,list);
        }
        width = getArguments().getInt(ARGS_DIALOG_WIDTH, 400);
        height = getArguments().getInt(ARGS_DIALOG_HEIGHT, 500);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_radio_list, container, false);
        ButterKnife.bind(this, view);
        tvDialogTitle.setText(titleText);
        radioAdapter = new RadioAdapter(getContext(),datas);
        listView.setAdapter(radioAdapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }

    @OnClick(R.id.tv_ok)
    public void onClickOk(View view){
        if (okClickListener!=null){
            okClickListener.onOkClick(view,selectedEntity);
        }
        dismiss();
    }

    public void setOnOkClickListener(OnOkClickListener okClickListener){
        this.okClickListener = okClickListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (selectedEntity!=datas.get(position)){
            if (selectedEntity!=null){
                selectedEntity.setIsChecked(false);
            }
            selectedEntity = datas.get(position);
            selectedEntity.setIsChecked(true);
            radioAdapter.notifyDataSetChanged();
        }
    }

    public interface OnOkClickListener{
        void onOkClick(View view,BaseEntity entity);
    }
}
