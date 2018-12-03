package com.example.mocalatte.project1.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.network.JoinServiceThread;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SignUpActivity extends Activity {
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    protected void showSignup() {
        Log.e("showSignup", "showSignupshowSignup");
        setContentView(R.layout.activity_sign_up);
        //final ExtraUserPropertyLayout extraUserPropertyLayout = findViewById(R.id.extra_user_property);
        Button signupButton = findViewById(R.id.buttonSignup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 샘플 테스트 properties.. 이 SignUp 액티비티에서 실제로 앱 운영에 필요한 회원 정보를 입력받아서 HashMap에 저장한 뒤에
                // 카카오의 signup api를 사용하여 등록해주면 카카오 앱 관리페이지에서 사용자 목록 및 프로퍼티를 확인 할 수있음ㅋ
                Map<String, String> properties = new HashMap<String, String>();
                properties.put("nickname", "leo");
                properties.put("age", "33");
                requestSignUp(properties);
            }
        });
    }

    private void requestSignUp(final Map<String, String> properties) {
        UserManagement.getInstance().requestSignup(new ApiResponseCallback<Long>() {
            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onSuccess(Long result) {
                requestMe();
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                final String message = "UsermgmtResponseCallback : failure : " + errorResult;
                com.kakao.util.helper.log.Logger.w(message);
                //KakaoToast.makeToast(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }
        }, properties);
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void requestMe() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                int result = errorResult.getErrorCode();
                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                    //KakaoToast.makeToast(getApplicationContext(), getString(R.string.error_message_for_service_unavailable), Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignUpActivity.this, "Service is unavailable...", Toast.LENGTH_SHORT).show();
                } else {
                    GlobalApplication.redirectLoginActivity(SignUpActivity.this);
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Logger.e("onSessionClosed");
                GlobalApplication.redirectLoginActivity(SignUpActivity.this);
            }

            @Override
            public void onSuccess(MeV2Response result) {
                /*if (result.hasSignedUp() == OptionalBoolean.FALSE) {
                    showSignup();
                } else {
                    GlobalApplication.redirectMainActivity(SignUpActivity.this);
                }*/
                long meresultid = result.getId();

                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                long storedid = sp.getLong("id", -1);
                String storedpushtoken = sp.getString("token", null);

                // 한번도 로그인한 적이 없거나 로컬에 저장내역이 카카오api 정보와 다를 경우..
                if(storedid == -1  || storedid != meresultid){
                    // 앱 설치한지 금방이라 푸시 토큰이 아직 생성되지 않은 경우..
                    if(storedpushtoken == null){
                        Toast.makeText(SignUpActivity.this, "푸시 기능을 위한 토큰이 아직 발급되지 않았습니다.\n잠시후에 다시 시도해보세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        try {
                            // 서버에 정보 등록 요청..
                            JoinServiceThread joinServiceThread = new JoinServiceThread(SignUpActivity.this, meresultid, "kakao", storedpushtoken);
                            JoinServiceThread.Repo repo = joinServiceThread.execute().get();

                            // 서버에 저장 완료했거나 이미 저장된 유저..
                            if( repo.getMsg().equals("success")
                                    || ( repo.getMsg().equals("fail") && repo.getReason().equals("notfoundrecord or data not changed") )
                                    )
                            {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putLong("id", meresultid);
                                editor.commit();

                                GlobalApplication.redirectMainActivity(SignUpActivity.this);
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    GlobalApplication.redirectMainActivity(SignUpActivity.this);
                }
            }
        });
    }
}
