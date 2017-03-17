# Demo of ICE For Android

### 调用接口姿势:
实现传统 Callback 回调方式
```
IceClient.getUsers("ifmvo", "123456", new IceClient.Callback<String>() {
            @Override
            public void onStart() {
                // showLoading();

            }

            @Override
            public void onFailure(String msg) {
                //closeLoading();
                //showErrorMsg();
                tvInfo.setText("链接异常信息" + msg);
            }

            @Override
            public void onSuccess(String result) {

                //closeLoading();
                //showResult();
                tvInfo.setText("返回成功信息" + result);
            }
        });
```
### 添加接口方式:
//IceClient.java
```
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
```
### 最终解释权归本方所有
哈哈哈哈O(∩_∩)O哈哈~