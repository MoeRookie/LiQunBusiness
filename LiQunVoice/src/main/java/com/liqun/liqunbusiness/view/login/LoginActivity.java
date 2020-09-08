package com.liqun.liqunbusiness.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.liqun.lib_commin_ui.base.BaseActivity;
import com.liqun.lib_network.okhttp.listener.DisposeDataListener;
import com.liqun.liqunbusiness.R;
import com.liqun.liqunbusiness.api.RequestCenter;
import com.liqun.liqunbusiness.model.login.LoginEvent;
import com.liqun.liqunbusiness.model.user.User;
import com.liqun.liqunbusiness.utils.UserManager;

import org.greenrobot.eventbus.EventBus;

public class LoginActivity extends BaseActivity
implements DisposeDataListener {
    public static void start(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCenter.login(LoginActivity.this);
            }
        });
    }

    @Override
    public void onSuccess(Object responseObj) {
        // 处理正常逻辑
        User user = (User) responseObj;
        UserManager.getInstance().saveUser(user);
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        // 登录失败逻辑
    }
}
