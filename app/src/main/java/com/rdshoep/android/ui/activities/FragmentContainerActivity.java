package com.rdshoep.android.ui.activities;
/*
 * @description
 *   Please write the FragmentContainerActivity module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/5/2016)
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.rdshoep.android.R;

public class FragmentContainerActivity extends AppCompatActivity {

    static final String ARGS_FRAGMENT_CLASS = "fragmentClass";
    static final String ARGS_FRAGMENT_EXTRAS = "fragmentExtras";

    static final String ARGS_FRAGMENT_TITLE = "title";

    static final String SAVED_INSTANCE_FRAGMENT_TAG = "savedInstanceFragmentTag";


    public static Bundle generateBundles(Bundle bundle, String fragmentClassName, Bundle extras, String title) {
        if (bundle == null) bundle = new Bundle();

        bundle.putString(ARGS_FRAGMENT_CLASS, fragmentClassName);
        if (extras != null) {
            bundle.putBundle(ARGS_FRAGMENT_EXTRAS, extras);
        }

        bundle.putString(ARGS_FRAGMENT_TITLE, title);

        return bundle;
    }

    public static void startActivityForResult(Activity activity, int requestCode, String title
            , String fragmentClassName, Bundle extras) {
        Intent intent = new Intent(activity, FragmentContainerActivity.class);

        intent.putExtras(generateBundles(null, fragmentClassName, extras, title));

        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Fragment fragment, int requestCode, String title
            , String fragmentClassName, Bundle extras) {
        Intent intent = new Intent(fragment.getActivity(), FragmentContainerActivity.class);

        intent.putExtras(generateBundles(null, fragmentClassName, extras, title));

        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 实例化的Fragment对应的tag，用户重建后获取句柄
     */
    String fragmentTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        Intent intent = getIntent();
        String title = intent.getStringExtra(ARGS_FRAGMENT_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        Fragment fragment = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_FRAGMENT_TAG)) {
            fragmentTag = savedInstanceState.getString(SAVED_INSTANCE_FRAGMENT_TAG);
            fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        }

        if (fragment == null) {
            String fragmentClass = intent.getStringExtra(ARGS_FRAGMENT_CLASS);
            Bundle bundle = intent.getBundleExtra(ARGS_FRAGMENT_EXTRAS);
            fragment = Fragment.instantiate(this, fragmentClass, bundle);

            fragmentTag = fragmentClass + System.currentTimeMillis();
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(android.R.id.widget_frame, fragment, fragmentTag);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_INSTANCE_FRAGMENT_TAG, fragmentTag);

        super.onSaveInstanceState(outState);
    }
}
