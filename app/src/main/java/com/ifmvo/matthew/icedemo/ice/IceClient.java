package com.ifmvo.matthew.icedemo.ice;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.Serializable;

import Demo.Callback_Hello_getSomething;
import Demo.Callback_Hello_getUsers;
import Demo.HelloPrx;
import Demo.HelloPrxHelper;
import Ice.LocalException;

/**
 * ICE 网络请求 Client
 * Created by 陈铭卓 on 17-1-4.
 */
public class IceClient {

    /**
     * host
     */
    private static final String HOST = "CeresRequest:default -h 192.168.11.248 -p 10000";
    /**
     * 超时时间
     */
    private static final int TIMEOUT = 3000;
    /**
     * --------------------- 这里开始正式的接口请求 ---------------------------
     */
    /**
     * 定义的借口就可以这样写
     * @param username 参数
     * @param password 参数
     * @param callback 回调
     */
    public static void getUsers(String username, String password, final Callback callback){
        if (requestPre(callback) != OK) return ;

        helloPrx.begin_getUsers(username, password, new Callback_Hello_getUsers() {
            @Override
            public void response(int ret, String message) {
                handleSuccess(message, callback);
            }

            @Override
            public void exception(LocalException e) {
                handleException(callback, e);
            }
        });
    }

    public static void getSomething(int id, final Callback callback){
        if (requestPre(callback) != OK) return ;

        helloPrx.begin_getSomething(id, new Callback_Hello_getSomething() {
            @Override
            public void response(int ret, String message) {
                handleSuccess(message, callback);
            }

            @Override
            public void exception(LocalException e) {
                handleException(callback, e);
            }
        });
    }


    //1
    //2
    //3
    //...接口实现...类似上面两个方法的使用







    /**
     * ---------------------初始化 需在 Application 调用--------------------
     *
     */
    private static Context mContext;
    private static Ice.Communicator ic;
    private static Ice.ObjectPrx base;
    private static HelloPrx helloPrx;

    public static void init (Context context){
        mContext = context;
        /**
         * 自定义一些配置
         */
//        Ice.InitializationData initData = new Ice.InitializationData();
//
//        initData.dispatcher = new Ice.Dispatcher() {
//            @Override
//            public void
//            dispatch(Runnable runnable, Ice.Connection connection) {
////                _uiHandler.post(runnable);
//            }
//        };
////
//        initData.properties = Ice.Util.createProperties();
//        initData.properties.setProperty("Ice.Trace.Network", "3");
//        initData.properties.setProperty("IceSSL.Trace.Security", "3");
//        initData.properties.setProperty("IceSSL.KeystoreType", "BKS");
//        initData.properties.setProperty("IceSSL.TruststoreType", "BKS");
//        initData.properties.setProperty("IceSSL.Password", "password");
//        initData.properties.setProperty("Ice.InitPlugins", "0");
//        initData.properties.setProperty("Ice.Plugin.IceSSL", "IceSSL.PluginFactory");
//
//        // SDK versions < 21 only support TLSv1 with SSLEngine.
//        if(Build.VERSION.SDK_INT < 21){ //5.0
//            initData.properties.setProperty("IceSSL.Protocols", "tls1_0");
//        }
//
//        ic = Ice.Util.initialize(initData);
//
        ic = Ice.Util.initialize();
        /**
         * 自定义添加证书
         */
//        IceSSL.Plugin plugin = (IceSSL.Plugin)ic.getPluginManager().getPlugin("IceSSL");
        //
        // Be sure to pass the same input stream to the SSL plug-in for
        // both the keystore and the truststore. This makes startup a
        // little faster since the plugin will not initialize
        // two keystores.
        //

//        InputStream certs = mContext.getResources().openRawResource(R.raw.client);
//        plugin.setKeystoreStream(certs);
//        plugin.setTruststoreStream(certs);
//        ic.getPluginManager().initializePlugins();

        base = ic.stringToProxy(HOST);
        //设置网络超时
        base = base.ice_invocationTimeout(TIMEOUT);
    }

    /**
     * ------------------关闭ice---------------------
     */
    public static void closeIce(){
        if(ic != null) {
            try {
                ic.destroy();
            }
            catch(Ice.LocalException ex) {
            }
        }
    }

    /**
     * ------------------发送接收消息并做出相应处理---------------------
     */

    //handler.what 成功 失败
    private static final int SUCCESS = 0;
    private static final int FAILED = 1;
    private static final int BEFORE = 2;

    //Handler.data 传递
    private static final String CALLBACK = "callback";
    private static final String MESSAGE = "message";
    private static final String PARSER = "parser";
    private static final String ERR_MSG = "errorMsg";
    private static final String EXCEPTION = "exception";

    static Handler handler = new Handler(Looper.myLooper()){
        @Override
        public  void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            IceClient.Callback callback = (IceClient.Callback) bundle.getSerializable(CALLBACK);

            if (msg.what == SUCCESS){
                String message = bundle.getString(MESSAGE);

                //可以在此添加Parser(返回数据的解析器)
                callback.onSuccess(message);

            }
            else if (msg.what == FAILED){
                String errorMsg = bundle.getString(ERR_MSG);
                Exception e = (Exception) bundle.getSerializable(EXCEPTION);

                String msgStr;

                if (!TextUtils.isEmpty(errorMsg)){
                    msgStr = errorMsg;
                }else{
                    msgStr = "服务器链接异常";
                }
                callback.onFailure(msgStr);
            }
            else if (msg.what == BEFORE){
                callback.onStart();
            }
        }
    };

    /**
     * ------------所有请求前的处理请求前必须调这个方法------------------------
     *
     */
    private static final int OK = 1;
    private static final int NO = 0;

    private static int requestPre(Callback callback){
        Message msg = new Message();
        msg.what = BEFORE;
        Bundle bundle = new Bundle();
        bundle.putSerializable(CALLBACK, callback);
        msg.setData(bundle);
        handler.sendMessage(msg);
        try {
            if (base == null){
                return NO;
            }else {
                // 过滤一下
                if (helloPrx == null){
                    helloPrx = HelloPrxHelper.uncheckedCast(base);
                    //如果还是null，就是服务器挂了
                    if (helloPrx == null){
                        handleException(callback, "连接服务器异常");
                        return NO;
                    }else return OK;
                } else return OK;
            }
        }catch (Exception e){
            handleException(callback, e);
            return NO;
        }
    }

    /**
     * --------------------------处理异常情况---------------------------
     */
    private static void handleException(Callback callback, String errorMsg){
        handleException(callback, null, errorMsg);
    }

    private static void handleException(Callback callback, Exception e){
        handleException(callback, e, null);
    }

    private static void handleException(final Callback callback, final Exception e, final String errorMsg){
        Message msg = new Message();
        msg.what = FAILED;
        Bundle bundle = new Bundle();
        bundle.putSerializable(CALLBACK, callback);
        bundle.putString(ERR_MSG, errorMsg);
        bundle.putSerializable(EXCEPTION, e);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    /**
     *  --------------------Handler 处理返回结果------------------------------
     */

    private static void handleSuccess(final String message, final Callback callback){

        Message msg = new Message();
        msg.what = SUCCESS;
        Bundle bundle = new Bundle();
        bundle.putSerializable(CALLBACK, callback);
        bundle.putString(MESSAGE, message);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    /**
     *  ----------------------- 自定义请求回调 ----------------------------------------------
     */
    public static abstract class Callback<T> implements Serializable{

        public Callback() {
        }

        public abstract void onStart();

        public abstract void onFailure(String msg);

        public abstract void onSuccess(T result);

    }
}