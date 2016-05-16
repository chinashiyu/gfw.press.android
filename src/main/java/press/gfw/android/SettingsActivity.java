/**
 * GFW.Press
 * Copyright (C) 2016  chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package press.gfw.android;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;

import java.sql.Timestamp;
import java.util.List;

import press.gfw.Client;

/**
 * GFW.Press Android 客户端组件
 *
 * @author chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 */
public class SettingsActivity extends CompatActivity {

    private ProxyAPN proxyAPN = null;

    private ProxyWIFI proxyWIFI = null;

    private Client client = null;

    private String proxyPort = "";

    /**
     * 配置监听器
     */
    private Preference.OnPreferenceChangeListener valueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String key = preference.getKey();

            String stringValue = value.toString();

            switch (key) {

                case "text_password":

                    String stars = "***********************************";

                    preference.setSummary(stars.substring(0, (stringValue.length() > stars.length() ? stars.length() : stringValue.length())));

                    break;

                default:

                    preference.setSummary(stringValue);

                    break;

            }

            return true;

        }

    };

    /**
     * 开关监听器
     */
    private Preference.OnPreferenceChangeListener switchListener = new Preference.OnPreferenceChangeListener() {

        @SuppressWarnings("deprecation")
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if ((boolean) newValue) {

                log("开关打开");

                start();

            } else {

                log("开关关闭");

                stop();

            }

            return true;

        }

    };

    /**
     * 停止运行
     */
    private void stop() {

        closeProxy();

        if (client != null && !client.isKill()) {

            client.kill();

        }

    }

    /**
     * 关闭全局代理
     */
    private void closeProxy() {

        if (proxyAPN == null) {

            proxyAPN = new ProxyAPN(getApplicationContext());

        }

        proxyAPN.closeProxy();

        if (proxyWIFI == null) {

            proxyWIFI = new ProxyWIFI(getApplicationContext());

        }

        proxyWIFI.closeProxy();

        log("已关闭全局代理");

    }

    /**
     * 打印信息
     *
     * @param o 打印对象
     */
    @SuppressWarnings("unused")
    private void log(Object o) {

        String time = (new Timestamp(System.currentTimeMillis())).toString().substring(0, 19);

        System.out.println("[" + time + "] " + o.toString());

    }

    /**
     * 打开全局代理
     */
    private void openProxy() {

        if (proxyPort == null || !proxyPort.matches("\\d+")) {

            return;

        }

        if (proxyAPN == null) {

            proxyAPN = new ProxyAPN(getApplicationContext());

        }

        proxyAPN.openProxy(Integer.valueOf(proxyPort));

        if (proxyWIFI == null) {

            proxyWIFI = new ProxyWIFI(getApplicationContext());

        }

        proxyWIFI.openProxy(Integer.valueOf(proxyPort));

        log("已打开全局代理");

    }

    /**
     * 开始运行
     */
    @SuppressWarnings("deprecation")
    private void start() {

        String serverHost = getValue(findPreference("text_server"));

        String serverPort = getValue(findPreference("text_port"));

        String password = getValue(findPreference("text_password"));

        proxyPort = getValue(findPreference("text_listen_port"));

        log("配置信息：");

        log("serverHost: " + serverHost);

        log("serverPort: " + serverPort);

        log("password: " + password);

        log("proxyPort: " + proxyPort);

        openProxy();

        if (client != null && !client.isKill()) {

            if (serverHost.equals(client.getServerHost()) && serverPort.equals(String.valueOf(client.getServerPort())) && password.equals(client.getPassword()) && proxyPort.equals(String.valueOf(client.getListenPort()))) {

                return;

            } else {

                client.kill();

            }

        }

        client = new Client(serverHost, serverPort, password, proxyPort);

        client.start();


    }

    /**
     * 设置开关监听器
     *
     * @param preference Preference
     */
    private void setSwitchListener(Preference preference) {

        preference.setOnPreferenceChangeListener(switchListener);

        switchListener.onPreferenceChange(preference, ((SwitchPreference) preference).isChecked());

    }

    /**
     * 设置配置监听器
     *
     * @param preference Preference
     */
    private void setValueListener(Preference preference) {

        preference.setOnPreferenceChangeListener(valueListener);

        valueListener.onPreferenceChange(preference, getValue(preference));

    }

    /**
     * 获取配置信息
     *
     * @param preference Preference
     * @return 值
     */
    private String getValue(Preference preference) {

        return PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");

    }

    /**
     * @param savedInstanceState InstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setupActionBar();

    }

    private void setupActionBar() {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            // actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);

        }

    }

    /**
     * @param savedInstanceState InstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

        setListener();

    }

    /**
     * 设置开关和配置监听器
     */
    @SuppressWarnings("deprecation")
    private void setListener() {

        addPreferencesFromResource(R.xml.pref_general);

        setValueListener(findPreference("text_server"));
        setValueListener(findPreference("text_port"));
        setValueListener(findPreference("text_password"));
        setValueListener(findPreference("text_listen_port"));

        setSwitchListener(findPreference("switch"));

    }


    /**
     * @param target Target
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {

        loadHeadersFromResource(R.xml.pref_headers, target);

    }


}
