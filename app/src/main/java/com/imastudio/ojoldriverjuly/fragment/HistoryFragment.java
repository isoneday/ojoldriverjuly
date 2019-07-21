package com.imastudio.ojoldriverjuly.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imastudio.ojoldriverjuly.R;
import com.imastudio.ojoldriverjuly.adapter.CustomRecycler;
import com.imastudio.ojoldriverjuly.helper.HeroHelper;
import com.imastudio.ojoldriverjuly.helper.SessionManager;
import com.imastudio.ojoldriverjuly.model.DataHistory;
import com.imastudio.ojoldriverjuly.model.ResponseHistory;
import com.imastudio.ojoldriverjuly.network.InitRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class HistoryFragment extends Fragment {


    RecyclerView recyclerview;
    private SessionManager manager;
        int kodeHistory;
    public static List<DataHistory> dataHistoryRequest;
    public static List<DataHistory> dataHistoryProses;
    public static List<DataHistory> dataHistoryComplete;

    public HistoryFragment(int i) {
        kodeHistory=i;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_proses, container, false);
      recyclerview = v.findViewById(R.id.recyclerview);
        manager = new SessionManager(getActivity());
        getDataHistory();
        return v;
    }

    private void getDataHistory() {
        String token = manager.getToken();
        String iduser = manager.getIdUser();
        String device = HeroHelper.getDeviceUUID(getActivity());


        if (kodeHistory ==1){
            InitRetrofit.getInstance().getHistoryRequest().enqueue(new Callback<ResponseHistory>() {
                @Override
                public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                    String result= response.body().getResult();
                    String msg=response.body().getMsg();
                if (result.equals("true")){
                    dataHistoryRequest = response.body().getData();
                    CustomRecycler adapter =new CustomRecycler(dataHistoryRequest,getActivity(),kodeHistory);
                    recyclerview.setAdapter(adapter);
                    recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                }
                }

                @Override
                public void onFailure(Call<ResponseHistory> call, Throwable t) {

                }
            });
        }else if(kodeHistory==2){

            InitRetrofit.getInstance().getHistoryProses(iduser,device,token).enqueue(new Callback<ResponseHistory>() {
                @Override
                public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        dataHistoryProses =  response.body().getData();
                        CustomRecycler adapter = new CustomRecycler(dataHistoryProses,getActivity(),kodeHistory);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistory> call, Throwable t) {

                }
            });
        }else if (kodeHistory==4){
            InitRetrofit.getInstance().getHistoryComplete(iduser,device,token).enqueue(new Callback<ResponseHistory>() {
                @Override
                public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                    String result = response.body().getResult();
                    String msg = response.body().getMsg();
                    if (result.equals("true")) {
                        dataHistoryComplete =   response.body().getData();
                        CustomRecycler adapter = new CustomRecycler(dataHistoryComplete,getActivity(), kodeHistory);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }

                @Override
                public void onFailure(Call<ResponseHistory> call, Throwable t) {

                }
            });
        }else{
            InitRetrofit.getInstance().getHistoryRequest().enqueue(new Callback<ResponseHistory>() {
                @Override
                public void onResponse(Call<ResponseHistory> call, Response<ResponseHistory> response) {
                    String result= response.body().getResult();
                    String msg=response.body().getMsg();
                    if (result.equals("true")){
                        dataHistoryRequest = response.body().getData();
                        CustomRecycler adapter =new CustomRecycler(dataHistoryRequest,getActivity(),kodeHistory);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseHistory> call, Throwable t) {

                }
            });
        }
    }
}
