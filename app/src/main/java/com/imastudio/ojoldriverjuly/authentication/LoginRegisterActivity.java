package com.imastudio.ojoldriverjuly.authentication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imastudio.ojoldriverjuly.MainActivity;
import com.imastudio.ojoldriverjuly.R;
import com.imastudio.ojoldriverjuly.helper.HeroHelper;
import com.imastudio.ojoldriverjuly.helper.SessionManager;
import com.imastudio.ojoldriverjuly.model.DataLogin;
import com.imastudio.ojoldriverjuly.model.ResponseLoginRegis;
import com.imastudio.ojoldriverjuly.network.InitRetrofit;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginRegisterActivity extends AppCompatActivity {

    @BindView(R.id.txt_rider_app)
    TextView txtRiderApp;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.rootlayout)
    RelativeLayout rootlayout;
    private SessionManager manager;
    private DataLogin dataLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginregister);
        ButterKnife.bind(this);
 manager = new SessionManager(this);
        //permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        110);


            }
            return;
        }


    }

    @OnClick({R.id.btnSignIn, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn:
                login();
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }

    private void login() {
        //untuk menampilkan popup ketika button diklik
        final AlertDialog builderlogin = new AlertDialog.Builder(this).
                setTitle("Login").
                setMessage(getString(R.string.messagelogin)).
                //layanan untuk menampilkan view menjadi popup
                        setPositiveButton("login", null).
                        setNegativeButton("cancel", null)
                .create();
        LayoutInflater inflater = getLayoutInflater();
        View formLogin = inflater.inflate(R.layout.layout_login, null, false);
        final ViewHolderLogin holderLogin = new ViewHolderLogin(formLogin);
        builderlogin.setCancelable(true);
        builderlogin.setView(formLogin);
        builderlogin.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonPositive = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = holderLogin.edtEmail.getText().toString();
                        String password = holderLogin.edtPassword.getText().toString();
                        if (TextUtils.isEmpty(email)) {
                            holderLogin.edtEmail.setError(getText(R.string.requireemail));
                        } else if (TextUtils.isEmpty(password)) {
                            holderLogin.edtPassword.setError(getText(R.string.requirepassword));
                        } else {
                            prosesLogin(email, password,builderlogin);
                        }
                    }
                });
                Button buttonNegative = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        builderlogin.setCancelable(false);
                    builderlogin.dismiss();
                    }
                });
            }
        });
        builderlogin.show();
    }

    private void prosesLogin(String email, String password, final AlertDialog builderlogin) {
        //tampilan loading
        final ProgressDialog progressDialog =
                ProgressDialog.show(this, "loading,,", "proses login");

        final String device = HeroHelper.getDeviceUUID(this);
        InitRetrofit.getInstance().loginUser(device, password,email).enqueue(new Callback<ResponseLoginRegis>() {
            //ketika response dari api berhasil menampilkan json
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                if (result.equals("true")) {
                    dataLogin = response.body().getData();
                    //set data to sharedpreference
                    String token = response.body().getToken();
                    String iduser = dataLogin.getIdUser();
                    manager.createLoginSession(token);
                    manager.setIduser(iduser);
                    manager.setDevice(device);
                    //perpindahan halaman
                    finish();
                    startActivity(new Intent(LoginRegisterActivity.this, MainActivity.class));
                    progressDialog.dismiss();
                    builderlogin.dismiss();
                    Toast.makeText(LoginRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    //untuk menghilangkan dialog
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginRegisterActivity.this,msg, Toast.LENGTH_SHORT).show();

                }
            }

            //gagal menampilkan data json
            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
                Toast.makeText(LoginRegisterActivity.this, "error:" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void register() {
        //untuk menampilkan popup ketika button diklik
        final AlertDialog builderRegis = new AlertDialog.Builder(this).
                setTitle("Register").
                setMessage(getString(R.string.messageregister)).
                //layanan untuk menampilkan view menjadi popup
                        setPositiveButton("register", null).
                        setNegativeButton("cancel", null)
                .create();
        LayoutInflater inflater = getLayoutInflater();
        View formRegister = inflater.inflate(R.layout.layout_register, null, false);
        final ViewHolderRegister holderRegister = new ViewHolderRegister(formRegister);
        builderRegis.setView(formRegister);
        builderRegis.setCancelable(true);
        builderRegis.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonPositive = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = holderRegister.edtEmail.getText().toString();
                        String nama = holderRegister.edtName.getText().toString();
                        String password = holderRegister.edtPassword.getText().toString();
                        String phone = holderRegister.edtPhone.getText().toString();
                        if (TextUtils.isEmpty(email)) {
                            holderRegister.edtEmail.setError(getText(R.string.requireemail));
                        } else if (TextUtils.isEmpty(nama)) {
                            holderRegister.edtName.setError(getText(R.string.requirename));
                        } else if (TextUtils.isEmpty(password)) {
                            holderRegister.edtPassword.setError(getText(R.string.requirepassword));
                        } else if (TextUtils.isEmpty(phone)) {
                            holderRegister.edtPhone.setError(getText(R.string.requirephone));
                        } else {
                            prosesRegister(email, password, nama, phone, builderRegis);
                        }
                    }
                });
                Button buttonNegative = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        builderRegis.setCancelable(false);
                    builderRegis.dismiss();
                    }
                });
            }
        });
        builderRegis.show();

    }

    private void prosesRegister(String email, String password, String nama, String phone, final AlertDialog builderRegis) {
        //tampilan loading
        final ProgressDialog progressDialog =
                ProgressDialog.show(this, "loading,,", "proses register");
        InitRetrofit.getInstance().registerUser(nama, phone, email, password).enqueue(new Callback<ResponseLoginRegis>() {
            //ketika response dari api berhasil menampilkan json
            @Override
            public void onResponse(Call<ResponseLoginRegis> call, Response<ResponseLoginRegis> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                builderRegis.dismiss();
                if (result == "true") {
                    //untuk menghilangkan progressdialog
                    progressDialog.dismiss();
                    Toast.makeText(LoginRegisterActivity.this, "anda berhasil register", Toast.LENGTH_SHORT).show();
                    //untuk menghilangkan dialog
                } else {
                    progressDialog.dismiss();
                }
            }

            //gagal menampilkan data json
            @Override
            public void onFailure(Call<ResponseLoginRegis> call, Throwable t) {
                Toast.makeText(LoginRegisterActivity.this, "error:" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    static
    class ViewHolderRegister {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;
        @BindView(R.id.edtName)
        MaterialEditText edtName;
        @BindView(R.id.edtPhone)
        MaterialEditText edtPhone;

        ViewHolderRegister(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static
    class ViewHolderLogin {
        @BindView(R.id.edtEmail)
        MaterialEditText edtEmail;
        @BindView(R.id.edtPassword)
        MaterialEditText edtPassword;

        ViewHolderLogin(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onBackPressed() {
             AlertDialog.Builder keluar = new AlertDialog.Builder(this);
                     keluar.setTitle("keluar");
                     keluar.setMessage("apakah anda yakin keluar");
                     keluar.setIcon(R.mipmap.ic_launcher_round);
                     keluar.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                         //keluar aplikasi
                             System.exit(0);
                             moveTaskToBack(true);
                         }
                     });

                     //untuk posisi dikiri
                     keluar.setNegativeButton("no", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                         }
                     });
                     keluar.show();
    }
}
