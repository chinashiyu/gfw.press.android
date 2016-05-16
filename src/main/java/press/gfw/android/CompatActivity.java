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

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * GFW.Press Android 客户端组件
 *
 * @author chinashiyu ( chinashiyu@gfw.press ; http://gfw.press )
 */
public abstract class CompatActivity extends PreferenceActivity {

    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getDelegate().installViewFactory();

        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

        getDelegate().onPostCreate(savedInstanceState);

    }

    public ActionBar getSupportActionBar() {

        return getDelegate().getSupportActionBar();

    }

    @NonNull
    @Override
    public MenuInflater getMenuInflater() {

        return getDelegate().getMenuInflater();

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {

        getDelegate().setContentView(layoutResID);

    }

    @Override
    public void setContentView(View view) {

        getDelegate().setContentView(view);

    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {

        getDelegate().setContentView(view, params);

    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {

        getDelegate().addContentView(view, params);

    }

    @Override
    protected void onPostResume() {

        super.onPostResume();

        getDelegate().onPostResume();

    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {

        super.onTitleChanged(title, color);

        getDelegate().setTitle(title);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        getDelegate().onConfigurationChanged(newConfig);

    }

    @Override
    protected void onStop() {

        super.onStop();

        getDelegate().onStop();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        getDelegate().onDestroy();

    }

    public void invalidateOptionsMenu() {

        getDelegate().invalidateOptionsMenu();

    }

    private AppCompatDelegate getDelegate() {

        if (mDelegate == null) {

            mDelegate = AppCompatDelegate.create(this, null);

        }

        return mDelegate;

    }

}
