package com.example.jesus.appcliente.interfaces;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Usuario;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via user/password.
 */
public class LoginActivity extends AppCompatActivity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText userView;
    private EditText passwordView;
    private CheckBox recordarCheckBox;

    //local vars
    private String usuario, password;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean recordar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        userView = (EditText) findViewById(R.id.user);
        passwordView = (EditText) findViewById(R.id.password);

        Button botonEntrar = (Button) findViewById(R.id.login_button);
        botonEntrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        recordarCheckBox = (CheckBox)findViewById(R.id.recordarCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        recordar = loginPreferences.getBoolean("saveLogin", false);
        if (recordar) {
            userView.setText(loginPreferences.getString("username", ""));
            passwordView.setText(loginPreferences.getString("password", ""));
            recordarCheckBox.setChecked(true);
        }

    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        userView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        usuario = userView.getText().toString();
        password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }else if (!isPasswordValid(password)){
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(usuario)) {
            userView.setError(getString(R.string.error_field_required));
            focusView = userView;
            cancel = true;
        } else if (!isUserValid(usuario)) {
            userView.setError(getString(R.string.error_invalid_usuario));
            focusView = userView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            if (recordarCheckBox.isChecked()) {
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", usuario);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.apply();
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.apply();
            }

            mAuthTask = new UserLoginTask(usuario, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUserValid(String usuario) {
        return usuario.length() < 150;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, String> {

        private Usuario usuario = new Usuario();

        UserLoginTask(String usuario, String password) {
            this.usuario.setUsername(usuario);
            this.usuario.setPassword(password);
        }

        @Override
        protected String doInBackground(Void... params) {

            StringBuffer response = new StringBuffer();
            Gson gson= new Gson();
            String usuarioJson = gson.toJson(usuario);


            HttpURLConnection httpConnection = null;
            try{
                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api-token-auth/");

                httpConnection = (HttpURLConnection) url.openConnection();

                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.connect();


                //Enviando Request
                OutputStream outputStream = httpConnection.getOutputStream();
                outputStream.write(usuarioJson.getBytes());
                outputStream.flush();

                if (httpConnection.getResponseCode() != 200){
                    return ("Fallo : código de error HTTP: " + httpConnection.getResponseCode());
                }

                //Recibiendo el resultado
                InputStream is = httpConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));

                String line;
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();

            }catch (MalformedURLException e) {
                e.printStackTrace();
                return "MalformedURLException";

            } catch (IOException e) {
                e.printStackTrace();
                return ""+httpConnection.getErrorStream ();
            }finally {

                if(httpConnection != null) {
                    httpConnection.disconnect();
                }
            }

        }

        @Override
        protected void onPostExecute(final String resultado) {
            mAuthTask = null;
            //showProgress(false);

            Log.d("POSTEXE", resultado);
            try{
                JSONObject jsonObject = new JSONObject(resultado);
                String token = jsonObject.getString("token");
                Log.d("TOKEN", token);

                //se almacena el token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("auth_token", token);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }
            catch ( org.json.JSONException e){
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.loginLinerLayout), "Nombre de usuario y contraseña incorrecta", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //Toast.makeText(LoginActivity.this,"Nombre de usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }
}

