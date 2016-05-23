package com.rdshoep.android.ui.fragments;
/*
 * @description
 *   Please write the RulerViewFragment module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/17/2016)
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rdshoep.android.R;
import com.rdshoep.android.view.RulerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RulerViewFragment extends Fragment implements RulerView.OnValueChangedListener {

    @BindView(R.id.rulerView)
    RulerView rulerView;
    @BindView(android.R.id.text1)
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ruler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rulerView.setListener(this);
    }

    @Override
    public void onValueChanged(float value, boolean isFinalValue) {
        textView.setText(String.format("%f, %b", value, isFinalValue));
    }
}
