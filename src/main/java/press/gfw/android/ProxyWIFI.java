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

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * GFW.Press Android 客户端WIFI代理
 *
 * @author chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 */
public class ProxyWIFI extends Thread {

    private static final String PROXY_NONE = "NONE";

    private static final String PROXY_STATIC = "STATIC";

    private Context context = null;

    private int port = -1;

    private HashMap ssidMap = null;

    private WifiManager wifiManager = null;

    /**
     * @param context ApplicationContext
     */
    public ProxyWIFI(Context context) {

        this.context = context;

        ssidMap = new HashMap();

    }

    @Override
    public void run() {

        while (true) {

            if (port != -1) {

                setProxy();

            }

            try {

                sleep(3000L);

            } catch (InterruptedException ex) {

            }

        }

    }

    /**
     * 打印信息
     *
     * @param o 打印对象
     */

    private void log(Object o) {

        String time = (new Timestamp(System.currentTimeMillis())).toString().substring(0, 19);

        System.out.println("[" + time + "] " + o.toString());

    }

    /**
     * 关闭代理
     */
    public synchronized void closeProxy() {

        setPort(0);

    }

    /**
     * 打开代理
     *
     * @param port
     */
    public synchronized void openProxy(int port) {

        setPort(port);

    }

    /**
     * 设置端口
     *
     * @param port
     */
    private synchronized void setPort(int port) {

        this.port = port;

        if (getState() == State.NEW) {

            start();

        }

    }

    /**
     * 设置WIFI网络代理
     */
    private synchronized void setProxy() {

        if (wifiManager == null) {

            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (wifiManager == null) return;

        }

        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();

        if (configs == null) {

            return;

        }

        int size = configs.size();

        if (size == 0) {

            return;

        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo == null) {

            return;

        }

        String connectedSSID = wifiInfo.getSSID();

        int connectedNetworkId = wifiInfo.getNetworkId();

        //log("WIFI网络总数: " + size);

        int _size = 0; //本次配置的网络数

        for (int i = 0; i < size; i++) {

            WifiConfiguration config = configs.get(i);

            Object ssid = ssidMap.get(config.SSID);

            if (ssid != null && (int) ssid == port) {

                continue;

            }

            _size++;

            ssidMap.put(config.SSID, port);

            Class<?> configClass = config.getClass();

            try {

                Field proxySettingsField = configClass.getField("proxySettings");

                Class<?> proxySettingsEnum = Class.forName("android.net.wifi.WifiConfiguration$ProxySettings");

                Object[] constants = proxySettingsEnum.getEnumConstants(); //NONE, STATIC, UNASSIGNED

                Field linkPropertiesField = configClass.getField("linkProperties");

                Object linkPropertiesObject = linkPropertiesField.get(config);

                Class<?> linkPropertiesClass = linkPropertiesObject.getClass();

                Class<?> proxyPropertiesClass = Class.forName("android.net.ProxyProperties");

                Method setHttpProxyMethod = linkPropertiesClass.getDeclaredMethod("setHttpProxy", proxyPropertiesClass);

                if (port == 0) {

                    Object proxySetting = get(constants, PROXY_NONE);

                    proxySettingsField.set(config, proxySetting);

                    setHttpProxyMethod.invoke(linkPropertiesObject, new Object[]{null});

                } else {

                    Class<?>[] proxyPropertiesArgsClass = new Class[]{String.class, int.class, String.class};

                    Constructor<?> proxyPropertiesConstructor = proxyPropertiesClass.getConstructor(proxyPropertiesArgsClass);

                    Object[] proxyPropertiesArgs = new Object[]{"127.0.0.1", port, "127.0.0.1,localhost"}; //127.0.0.1,192.168.0.0/16,10.0.0.0/8,172.16.0.0/12

                    Object proxyProperties = proxyPropertiesConstructor.newInstance(proxyPropertiesArgs);

                    Object proxySetting = get(constants, PROXY_STATIC);

                    proxySettingsField.set(config, proxySetting);

                    setHttpProxyMethod.invoke(linkPropertiesObject, proxyProperties);

                }

                if (config.SSID.equals(connectedSSID)) {

                    log("SSID: " + config.SSID + " (已连接)");

                    connectedNetworkId = wifiManager.updateNetwork(config);

                } else {

                    log("SSID: " + config.SSID);

                    wifiManager.updateNetwork(config);

                }

            } catch (Exception ex) {

                log("设置代理服务器发生了错误");

                ex.printStackTrace();

            }

        }

        if (_size > 0) {

            log("本次配置的WIFI网络数: " + _size);

            wifiManager.saveConfiguration();

            wifiManager.setWifiEnabled(false);

            wifiManager.setWifiEnabled(true);

            if (connectedNetworkId != -1) {

                wifiManager.enableNetwork(connectedNetworkId, true);

            }

        }

    }

    /**
     * 按名字获取enum
     *
     * @param enums Enum数组
     * @param name  Enum名称
     * @return Enum对象
     */
    private Object get(Object[] enums, String name) {

        for (Object _enum : enums) {

            if (_enum.toString().contains(name)) {

                return _enum;

            }

        }

        return null;

    }

}

