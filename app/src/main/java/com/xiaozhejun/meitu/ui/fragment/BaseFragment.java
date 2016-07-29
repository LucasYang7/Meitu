package com.xiaozhejun.meitu.ui.fragment;

import android.support.v4.app.Fragment;

import rx.Subscription;

/**
 * BaseFragment作为展示各种分类图片的fragment的父类
 * Created by yangzhe on 16-7-27.
 */
public abstract class BaseFragment extends Fragment{
    protected Subscription subscription;   // 所有实现了BaseFragment的子Fragment都会继承这个subscription

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
    }

    // 解除Subscriber与Observable之间的订阅关系
    protected void unsubscribe(){
        if(subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }
}
