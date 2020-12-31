package com.terry.account.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.terry.account.R;


/**
 * 关于页面
 * @author sjy
 *
 */
public class ChartNullFragment extends Fragment {

//	private static final String TAG = AboutFragment.class.getSimpleName();

	private View rootView;

	private static ChartNullFragment instance;

	public static ChartNullFragment getInstance() {
		if (instance == null) instance = new ChartNullFragment();
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_about, container, false);
		TextView textView = rootView.findViewById(R.id.tx_aboutinfo);
		textView.setText(R.string.chart_null_hint);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

}
