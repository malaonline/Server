package com.malalaoshi.android.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.SingleChoiceAdapter;
import com.malalaoshi.android.entity.BaseEntity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/1/26.
 */
public class SingleChoiceDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    public static String ARGS_DIALOG_DATAS = "datas";
    public static String ARGS_DIALOG_WIDTH = "width";
    public static String ARGS_DIALOG_HEIGHT = "height";

    private int width;
    private int height;

    @Bind(R.id.listview_single)
    ListView listviewSingle;

    private OnSingleChoiceClickListener singleChoiceClickListener;

    private ArrayList<BaseEntity> datas = new ArrayList<>();

    public static SingleChoiceDialog newInstance(int width, int height, ArrayList<BaseEntity> list) {
        SingleChoiceDialog f = new SingleChoiceDialog();
        Bundle args = new Bundle();
        args.putInt(ARGS_DIALOG_WIDTH, width);
        args.putInt(ARGS_DIALOG_HEIGHT, height);
        args.putParcelableArrayList(ARGS_DIALOG_DATAS,list);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.dialog_single_choice, null, false);
        ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SingleChoiceAdapter singleChoiceAdapter = new SingleChoiceAdapter(getContext(),datas);
        listviewSingle.setAdapter(singleChoiceAdapter);
        listviewSingle.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BaseEntity baseEntity = datas.get(position);
        if (singleChoiceClickListener!=null){
            singleChoiceClickListener.onChoiceClick(view,baseEntity);
        }
        dismiss();
    }

    public void setOnSingleChoiceClickListener(OnSingleChoiceClickListener singleChoiceClickListener){
        this.singleChoiceClickListener = singleChoiceClickListener;
    }
    public interface OnSingleChoiceClickListener{
        void onChoiceClick(View view,BaseEntity entity);
    }
}
