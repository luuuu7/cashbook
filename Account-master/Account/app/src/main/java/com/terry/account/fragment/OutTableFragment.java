package com.terry.account.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.utils.DensityUtils;
import com.terry.account.GdApp;
import com.terry.account.R;
import com.terry.account.bean.AccountBean;
import com.terry.account.bean.InTableBean;
import com.terry.account.util.C;

import java.util.ArrayList;
import java.util.List;


/**
 * drawer_table_year_out
 * @author sjy
 *
 */
public class OutTableFragment extends Fragment {

//	private static final String TAG = AboutFragment.class.getSimpleName();

	private View rootView;

	private GdApp mApp;

	private SmartTable<InTableBean> table;

	private static OutTableFragment instance;

	public static OutTableFragment getInstance() {
		if (instance == null) instance = new OutTableFragment();
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_annotation, container, false);
		FontStyle.setDefaultTextSize(DensityUtils.sp2px(getActivity(), 15));
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mApp = (GdApp) getActivity().getApplication();
		final List<InTableBean> inTableBeans = new ArrayList<>();
		for(AccountBean accountBean: mApp.mAccBeanYearColumnList){
			if(accountBean.getAccountType().equals(C.COME_TYPE.OUT)){
				inTableBeans.add(InTableBean.getInTableBean(accountBean));
			}
		}
		table = (SmartTable<InTableBean>) rootView.findViewById(R.id.table);
		table.setData(inTableBeans);
		table.getConfig().setShowTableTitle(true);
		table.getConfig().setShowXSequence(true);
		table.getConfig().setShowYSequence(true);
		table.setZoom(true, 2, 0.2f);
	}

}
