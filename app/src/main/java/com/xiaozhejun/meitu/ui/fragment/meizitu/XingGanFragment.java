package com.xiaozhejun.meitu.ui.fragment.meizitu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaozhejun.meitu.R;
import com.xiaozhejun.meitu.network.Network;
import com.xiaozhejun.meitu.ui.fragment.BaseFragment;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class XingGanFragment extends BaseFragment {

    //test 测试能否显式html文件
    TextView textView;
    Observer<ResponseBody> observer = new Observer<ResponseBody>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(ResponseBody responseBody) {
            try {
                textView.setText(responseBody.string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void loadHtmlContent(){
        Network.getMeizituService()
                .getPictureType("xinggan")
                .subscribeOn(Schedulers.io())              //指定产生事件的线程
                .observeOn(AndroidSchedulers.mainThread()) //指定消费事件的线程
                .subscribe(observer);
    }
    //test

    public XingGanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_xing_gan, container, false);
        textView = (TextView)view.findViewById(R.id.textViewHelloInXingGan);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadHtmlContent();
            }
        });
        return view;
    }

}
