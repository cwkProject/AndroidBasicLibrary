package org.cwk.android.library.model.activity;
/**
 * Created by 超悟空 on 2015/6/11.
 */

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.cwk.android.library.R;
import org.cwk.android.library.common.dialog.SimpleDialog;
import org.cwk.android.library.common.function.InputMethodController;
import org.cwk.android.library.common.function.ToolbarInitialize;
import org.cwk.android.library.global.ApplicationConfig;
import org.cwk.android.library.global.Global;
import org.cwk.android.library.global.LoginStatus;
import org.cwk.android.library.model.work.IWorkEndListener;
import org.cwk.android.library.model.work.implement.CheckLogin;

/**
 * 登录Activity模板
 *
 * @author 超悟空
 * @version 1.0 2015/6/11
 * @since 1.0
 */
public abstract class BaseLoginActivity extends AppCompatActivity {

    /**
     * 用户名编辑框
     */
    protected EditText userNameEditText = null;

    /**
     * 用户名提示框
     */
    protected TextInputLayout userNameTextInputLayout = null;

    /**
     * 密码编辑框
     */
    protected EditText passwordEditText = null;

    /**
     * 密码提示框
     */
    protected TextInputLayout passwordTextInputLayout = null;

    /**
     * 根布局
     */
    private View rootView = null;

    /**
     * 进度条
     */
    protected ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(onActivityLoginLayout());

        //noinspection ConstantConditions
        rootView = ((ViewGroup) findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);

        // 重置用户登录参数
        LoginStatus.Reset();

        // 初始化界面
        init();
    }

    /**
     * 提供登录activity布局ID
     *
     * @return activity布局文件ID，默认为{@link R.layout#activity_login}
     */
    protected int onActivityLoginLayout() {
        return R.layout.activity_login;
    }

    /**
     * 初始化界面
     */
    private void init() {
        // 初始化Toolbar
        initToolbar();
        // 初始化编辑框
        initEdit();
        // 初始化自定义登录按钮事件
        onCustomLoginButton();
        // 初始化自定义注册按钮
        onCustomRegister();
        // 自定义界面初始化
        onInitCustomView();
    }

    /**
     * 初始化标题栏
     */
    protected void initToolbar() {
        ToolbarInitialize.initToolbar(this, R.string.title_login, onSetCenterTitle(),
                onSetHasNavigation());
    }

    /**
     * 设置是否居中显示标题
     *
     * @return true表示居中，默认为false
     */
    protected boolean onSetCenterTitle() {
        return false;
    }

    /**
     * 设置是否带有返回导航
     *
     * @return true表示开启返回导航，默认为false
     */
    protected boolean onSetHasNavigation() {
        return false;
    }

    /**
     * 初始化自定义登录按钮事件，
     * 登录按钮默认为{@link R.id#login_content_layout_login_button}，
     * 按钮类型{@link AppCompatButton}
     */
    protected void onCustomLoginButton() {

        AppCompatButton button = (AppCompatButton) findViewById(R.id
                .login_content_layout_login_button);

        TypedArray typedArray = getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});

        if (button != null) {
            button.setSupportBackgroundTintList(typedArray.getColorStateList(0));
        }

        typedArray.recycle();

        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });
    }

    /**
     * 初始化自定义注册按钮
     */
    protected void onCustomRegister() {
    }

    /**
     * 自定义界面初始化
     */
    protected void onInitCustomView() {
    }

    /**
     * 初始化编辑框
     */
    protected void initEdit() {
        // 文本框初始化
        userNameEditText = (EditText) findViewById(R.id.login_content_layout_user_name_editText);
        passwordEditText = (EditText) findViewById(R.id.login_content_layout_password_editText);
        userNameTextInputLayout = (TextInputLayout) findViewById(R.id
                .login_content_layout_user_name_textInputLayout);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id
                .login_content_layout_password_textInputLayout);

        // 绑定错误提示
        onBindEditHint();

        // 尝试填充数据
        if (Global.getApplicationConfig().getUserName() != null) {
            // 填充用户
            userNameEditText.setText(Global.getApplicationConfig().getUserName());
        }
    }

    /**
     * 绑定输入框错误提示
     */
    protected void onBindEditHint() {

        // 用户名提示
        if (userNameTextInputLayout != null) {

            userNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String userName = userNameEditText.getText().toString();

                        // 检测用户名
                        checkUserName(userName);
                    }
                }
            });

            userNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String userName = s.toString();
                    if (!userName.contains(" ") && userName.length() >= 3) {
                        userNameTextInputLayout.setError(null);
                        userNameTextInputLayout.setErrorEnabled(false);
                    }
                }
            });
        }

        // 密码提示
        if (passwordTextInputLayout != null) {

            passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String password = passwordEditText.getText().toString();

                        // 检测密码
                        checkPassword(password);
                    }
                }
            });

            passwordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String password = s.toString();
                    if (password.length() >= 6) {
                        passwordTextInputLayout.setError(null);
                        passwordTextInputLayout.setErrorEnabled(false);
                    }
                }
            });
        }
    }

    /**
     * 检测密码
     *
     * @param password 密码
     *
     * @return true表示检测通过
     */
    protected boolean checkPassword(String password) {

        if (passwordTextInputLayout != null) {

            if (TextUtils.isEmpty(password)) {
                passwordTextInputLayout.setError(getString(R.string.prompt_password_null_error));
                return false;
            }

            if (password.length() < 6) {
                passwordTextInputLayout.setError(getString(R.string.prompt_password_short));
                return false;
            }
        } else {
            if (password.length() < 6) {
                if (rootView != null) {
                    Snackbar.make(rootView, R.string.prompt_password_short, Snackbar
                            .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.prompt_password_short, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }

        return true;
    }

    /**
     * 检测用户名
     *
     * @param userName 用户名
     *
     * @return true表示检测通过
     */
    protected boolean checkUserName(String userName) {

        if (userNameTextInputLayout != null) {

            if (TextUtils.isEmpty(userName)) {
                userNameTextInputLayout.setError(getString(R.string.prompt_user_name_null));
                return false;
            }

            if (userName.contains(" ")) {
                userNameTextInputLayout.setError(getString(R.string.prompt_user_name_blank));
                return false;
            }

            if (userName.length() < 3) {
                userNameTextInputLayout.setError(getString(R.string.prompt_user_name_short));
                return false;
            }
        } else {
            if (userName.contains(" ") || userName.length() < 3) {
                if (rootView != null) {
                    Snackbar.make(rootView, R.string.prompt_user_name_error, Snackbar
                            .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.prompt_user_name_error, Toast.LENGTH_SHORT)
                            .show();
                }
                return false;
            }
        }

        return true;
    }

    /**
     * 登录成功后执行，设置跳转
     *
     * @param message 成功消息
     *
     * @return 标识是否继续加载数据处理，
     * 如果返回true，则表示需要进一步加载数据，
     * 需要用户手动关闭加载进度窗口{@link #stopProgressDialog()}
     */
    protected abstract boolean onLoginSuccess(String message);

    /**
     * 登录失败后执行
     *
     * @param message 失败消息
     */
    protected void onLoginFailed(String message) {
        SimpleDialog.showDialog(this, message);
    }

    /**
     * 登录按钮点击事件
     */
    protected void onLoginClick() {

        // 获取用户名和密码
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        InputMethodController.CloseInputMethod(this);

        // 判断是否正确输入用户名和密码
        if (!checkUserName(userName) || !checkPassword(password)) {
            return;
        }

        // 打开旋转进度条
        startProgressDialog();
        // 执行登录任务
        onExecuteLogin(userName, password);
    }

    /**
     * 执行登录任务
     *
     * @param userName 用户名
     * @param password 密码
     */
    protected void onExecuteLogin(final String userName, final String password) {
        // 进行登录验证
        CheckLogin login = new CheckLogin();

        // 设置回调监听
        login.setWorkEndListener(new IWorkEndListener<String>() {
            @Override
            public void doEndWork(boolean state, String message, String data) {

                // 登录完成执行
                onLoginFinish(userName, password, state, message, data);
            }
        });

        // 执行登录任务
        login.beginExecute(userName, password);
    }

    /**
     * 登录执行结果装配，
     * 用于保存用户名密码等
     *
     * @param userName 用户名
     * @param password 密码
     * @param state    登录结果
     * @param message  响应消息
     * @param data     响应数据，推荐设计为用户id字符串
     */
    protected final void onLoginFinish(String userName, String password, boolean state, String
            message, Object data) {
        onSetResult(userName, password, state, data);

        if (state) {
            // 登录成功

            // 执行登录成功后的事件
            if (!onLoginSuccess(message)) {
                // 关闭进度条
                stopProgressDialog();
            }
        } else {
            // 关闭进度条
            stopProgressDialog();
            // 登录失败
            onLoginFailed(message);
        }
    }

    /**
     * 登录结束设置结果
     *
     * @param userName 用户名
     * @param password 密码
     * @param state    登录结果
     * @param data     响应数据，推荐设计为用户id字符串
     */
    protected void onSetResult(String userName, String password, boolean state, Object data) {
        LoginStatus loginStatus = LoginStatus.getLoginStatus();

        loginStatus.setLogin(state);
        loginStatus.setUserID((String) data);

        if (state) {
            // 登录成功

            // 保存当前设置
            ApplicationConfig config = Global.getApplicationConfig();
            config.setUserName(userName);
            config.setPassword(password);

            // 保存设置
            config.Save();
        }
    }

    /**
     * 打开进度条
     */
    protected void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // 设置提醒
            progressDialog.setMessage(getString(R.string.login_loading));
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    /**
     * 停止进度条
     */
    protected void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    /**
     * 注册按钮点击事件
     *
     * @param view 注册按钮
     */
    public void onRegisterClick(View view) {
    }
}

