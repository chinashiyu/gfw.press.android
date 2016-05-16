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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.sql.Timestamp;

/**
 * GFW.Press Android 客户端APN代理
 *
 * @author chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 */
public class ProxyAPN extends Thread {

    public static final Uri APN_LIST = Uri.parse("content://telephony/carriers"); //APN数据表

    private Context context = null;

    private int port = -1; //端口

    private int _port = -1; //上一个端口

    /**
     * 构造方法
     *
     * @param context ApplicationContext
     */
    public ProxyAPN(Context context) {

        this.context = context;

    }

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
     * 设置代理端口
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
     * 打开代理
     *
     * @param port
     */
    public synchronized void openProxy(int port) {

        setPort(port);

    }

    /**
     * 关闭代理
     */
    public synchronized void closeProxy() {

        setPort(0);

    }

    /**
     * 设置代理
     */
    private synchronized void setProxy() {

        if (port == _port) {

            return;

        }

        log("APN发生了变化:");

        log("旧代理端口 _port: " + _port);

        log("新代理端口   port: " + port);

        _port = port;

        ContentValues values = new ContentValues();

        values.put("proxy", (port > 0) ? "127.0.0.1" : "");

        values.put("port", (port > 0) ? String.valueOf(port) : "");

        try {

            context.getContentResolver().update(APN_LIST, values, "current=1 and type like 'default%'", null);

        } catch (Exception ex) {

        }

        //Cursor c = context.getContentResolver().query(APN_LIST, null, "current=1 and type like 'default%'", null, null);

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

}

