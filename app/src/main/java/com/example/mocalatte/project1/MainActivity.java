package com.example.mocalatte.project1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_logout = (Button)findViewById(R.id.button_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        onClickLogout();
                    }
                });
            }
        });

        Button btn_unlink = (Button)findViewById(R.id.button_unlink);
        btn_unlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUnlink();
            }
        });

    }

    // 로그아웃
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                GlobalApplication.redirectLoginActivity(MainActivity.this);
            }
        });
    }

    // 탈퇴
    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        GlobalApplication.redirectLoginActivity(MainActivity.this);
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        //redirectSignupActivity();
                                        GlobalApplication.redirectLoginActivity(MainActivity.this);
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        GlobalApplication.redirectLoginActivity(MainActivity.this);
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

}
