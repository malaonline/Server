package com.malalaoshi.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOGIN_API_PATH = "/api/token-auth/";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_login, container, false);
        String phoneNo = MalaApplication.getInstance().getPhoneNo();
        if (phoneNo!=null && !phoneNo.isEmpty()) {
            EditText tPhone = (EditText)v.findViewById(R.id.loginPhone);
            tPhone.setText(phoneNo);
        }
        Button loginButton = (Button)v.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                EditText tPhone = (EditText)v.findViewById(R.id.loginPhone);
                EditText tPassword = (EditText)v.findViewById(R.id.loginPassword);
                String phone = tPhone.getText().toString();
                String password = tPassword.getText().toString();
                if (phone.isEmpty()) {
                    Toast.makeText(LoginFragment.this.getActivity(), "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(LoginFragment.this.getActivity(), "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Toast.makeText(LoginFragment.this.getActivity(), phone+":"+password, Toast.LENGTH_SHORT).show();
                btn.setEnabled(false);
                ((Button)btn).setText("正在登录");
                new LoginTask().execute(phone, password);
            }
        });
        Button registerButton = (Button)v.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                EditText tPhone = (EditText)v.findViewById(R.id.registerPhone);
                EditText tPassword = (EditText)v.findViewById(R.id.registerPassword);
                EditText tPassword2 = (EditText)v.findViewById(R.id.registerPasswordConfirm);
                String phone = tPhone.getText().toString();
                String password = tPassword.getText().toString();
                String password2 = tPassword2.getText().toString();
                if (phone.isEmpty()) {
                    Toast.makeText(LoginFragment.this.getActivity(), "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty() || !password.equals(password2)) {
                    Toast.makeText(LoginFragment.this.getActivity(), "密码输入错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(LoginFragment.this.getActivity(), phone+":"+password, Toast.LENGTH_SHORT).show();
            }
        });
        // switch login & register
        final ViewFlipper viewFlipper = (ViewFlipper) v.findViewById(R.id.login_flipper);
        TextView gotoRegister = (TextView) v.findViewById(R.id.gotoRegister);
        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                //显示注册panel
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(LoginFragment.this.getActivity(), R.anim.push_left_in));
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(LoginFragment.this.getActivity(), R.anim.push_left_out));
                viewFlipper.showNext();
            }
        });
        TextView gotoLogin = (TextView) v.findViewById(R.id.gotoLogin);
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                //显示登录panel
                viewFlipper.setInAnimation(AnimationUtils.loadAnimation(LoginFragment.this.getActivity(), R.anim.push_right_in));
                viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(LoginFragment.this.getActivity(), R.anim.push_right_out));
                viewFlipper.showPrevious();
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void postLoginTask() {
        Button loginButton = (Button)this.getView().findViewById(R.id.loginButton);
        loginButton.setEnabled(true);
        loginButton.setText(this.getString(R.string.title_activity_login));
    }

    private class LoginTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                final String phone = params[0];
                String password = params[1];
                String url = MalaApplication.getInstance().getMalaHost()+LOGIN_API_PATH;
                RequestQueue requestQueue = MalaApplication.getHttpRequestQueue();
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", phone);
                jsonParam.put("password", password);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                    Request.Method.POST, url, jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token = null;
                                if (response.has("token")) {
                                    token = response.getString("token");
                                }
                                if (token != null && !token.isEmpty()) {
                                    onLoginSuccess(token, phone);
                                }
                            } catch (Exception e) {
                                Log.e(LoginFragment.class.getName(), e.getMessage(), e);
                            }
                            postLoginTask();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                            Toast.makeText(LoginFragment.this.getActivity(), "登录失败："+error.getMessage(), Toast.LENGTH_LONG).show();
                            postLoginTask();
                        }
                    });
                requestQueue.add(jsObjRequest);
                return phone;
            } catch (Exception e) {
                Log.e(LoginFragment.class.getName(), e.getMessage(), e);
            }
            return null;
        }

        private void onLoginSuccess(String token, String phoneNo) {
            Toast.makeText(LoginFragment.this.getActivity(), "登录成功", Toast.LENGTH_LONG).show();
            MalaApplication.getInstance().setToken(token);
            MalaApplication.getInstance().setPhoneNo(phoneNo);
            MalaApplication.getInstance().setIsLogin(true);
            FragmentManager fragmentManager = getFragmentManager();
            MainFragment mainFragment = new MainFragment();
            fragmentManager.beginTransaction().replace(R.id.content_layout, mainFragment).commit();
        }
    }
}
