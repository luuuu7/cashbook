package com.terry.account.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.terry.account.bean.AccountBean;
import com.terry.account.bean.PayBean;
import com.terry.account.bean.TypeBean;
import com.terry.account.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库的操作封装类
 * 将数据库的数据库封装成具体的javabean
 * 供外部使用
 * @author sjy
 *
 */
public class DBProvider {
	private Context mContext;

	private DBAdapter mDbAdapter = null;

	private static DBProvider instance = null;

	private DBProvider(Context context) {
		this.mContext = context;
		this.mDbAdapter = new DBAdapter(mContext);

	}

	public static synchronized DBProvider getInstance(Context context) {
		if (instance == null)
			instance = new DBProvider(context);
		return instance;
	}

	public void emptyAllAccountFromDB() {
		mDbAdapter.deleteAllAccount();
	}
	
	public void emptyAllUserFromDB() {
		mDbAdapter.deleteAllUser();
	}
	
	public void insertAccount(AccountBean info) {
		if (info == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("user", info.getUserName());
		cv.put("money", info.getMoney());
		cv.put("time", info.getTime());
		cv.put("type", info.getType());
		cv.put("accountType", info.getAccountType());
		cv.put("fromType", info.getFromType());
		cv.put("mark", info.getMark());
		mDbAdapter.insertAccount2DB(cv);
	}
	
	public void updateAccount(AccountBean newInfo,String oldId) {
		if (newInfo == null||TextUtils.isEmpty(oldId))
			return;
		ContentValues cv = new ContentValues();
		cv.put("id", newInfo.getId());
		cv.put("user", newInfo.getUserName());
		cv.put("money", newInfo.getMoney());
		cv.put("time", newInfo.getTime());
		cv.put("type", newInfo.getType());
		cv.put("accountType", newInfo.getAccountType());
		cv.put("fromType", newInfo.getFromType());
		cv.put("mark", newInfo.getMark());
		mDbAdapter.updateAccountInDB(cv,oldId);
	}
	
	public void delSingleAccount(AccountBean info) {
		if (info == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("id", info.getId());
		cv.put("user", info.getUserName());
		cv.put("money", info.getMoney());
		cv.put("time", info.getTime());
		cv.put("type", info.getType());
		cv.put("accountType", info.getAccountType());
		cv.put("fromType", info.getFromType());
		mDbAdapter.deleteSingleAccount(info.getId());
	}
	

	public List<AccountBean> getAllAccountFromDB() {
		List<AccountBean> infos = new ArrayList<AccountBean>();
		Cursor data = mDbAdapter.queryAccountFromDB();
		if (data != null) {
			try {
				while (data.moveToNext()) {
					AccountBean item = new AccountBean();
					item.setId(data.getString(data.getColumnIndex("id")));
					item.setUserName(data.getString(data.getColumnIndex("user")));
					item.setMoney(data.getString(data.getColumnIndex("money")));
					item.setTime(data.getString(data.getColumnIndex("time")));
					item.setType(data.getString(data.getColumnIndex("type")));
					item.setMark(data.getString(data.getColumnIndex("mark")));
					item.setFromType(data.getString(data.getColumnIndex("fromType")));
					item.setAccountType(data.getString(data.getColumnIndex("accountType")));
					infos.add(item);
				}
			} catch (Exception e){

			} finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}

	//精确查找
	public List<AccountBean> getAccountFromDB(String user,String time,String accountType,String zcType) {
		List<AccountBean> infos = new ArrayList<AccountBean>();
		Cursor data = mDbAdapter.queryAccountFromDB(user,time,accountType,zcType);
		if (data != null) {
			try {
				while (data.moveToNext()) {
					AccountBean item = new AccountBean();
					item.setId(data.getString(data.getColumnIndex("id")));
					item.setUserName(data.getString(data.getColumnIndex("user")));
					item.setMoney(data.getString(data.getColumnIndex("money")));
					item.setTime(data.getString(data.getColumnIndex("time")));
					item.setType(data.getString(data.getColumnIndex("type")));
					item.setMark(data.getString(data.getColumnIndex("mark")));
					item.setFromType(data.getString(data.getColumnIndex("fromType")));
					item.setAccountType(data.getString(data.getColumnIndex("accountType")));
					infos.add(item);
				}
			}catch (Exception e){

			} finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}


	//模糊查找
	public List<AccountBean> getAccountFromDBVague(String user,String minTime,String maxTime,String accountType,String zcType) {
		List<AccountBean> infos = new ArrayList<AccountBean>();
		Cursor data = mDbAdapter.queryAccountFromDBVague(user,minTime,maxTime,accountType,zcType);
		if (data != null) {
			try {
				while (data.moveToNext()) {
					AccountBean item = new AccountBean();
					item.setId(data.getString(data.getColumnIndex("id")));
					item.setUserName(data.getString(data.getColumnIndex("user")));
					item.setMoney(data.getString(data.getColumnIndex("money")));
					item.setTime(data.getString(data.getColumnIndex("time")));
					item.setType(data.getString(data.getColumnIndex("type")));
					item.setMark(data.getString(data.getColumnIndex("mark")));
					item.setFromType(data.getString(data.getColumnIndex("fromType")));
					item.setAccountType(data.getString(data.getColumnIndex("accountType")));
					infos.add(item);
				}
			}catch (Exception e){

			}  finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}

	public boolean insertUser(UserBean info) {
		if (info == null)
			return false;
		ContentValues cv = new ContentValues();
		cv.put("name", info.getName());
		cv.put("pwd", info.getPwd());
		cv.put("dayMax", 200);
		cv.put("monthMax", 2000);
		cv.put("yearMax", 20000);
		return mDbAdapter.insertUser2DB(cv);
	}

	public void delSingleUser(UserBean info) {
		if (info == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("user", info.getName());
		mDbAdapter.deleteSingleUser(info.getName());
	}
	
	/**
	 * 获取其他账号
	 */
	public List<UserBean> getUserFromDB() {
		List<UserBean> infos = new ArrayList<UserBean>();
		Cursor data = mDbAdapter.queryUserFromDB();
		if (data != null) {
			try {
				while (data.moveToNext()) {
					UserBean item = new UserBean();
					item.setName(data.getString(data.getColumnIndex("name")));
					item.setPwd(data.getString(data.getColumnIndex("pwd")));
					infos.add(item);
				}
			} finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}



	public UserBean getSingleUserFromDB(String name ,String pwd) {
		UserBean item = new UserBean();
		Cursor data = mDbAdapter.queryUserFromDB(name,pwd);
		if (data != null) {
			try {
				while (data.moveToNext()) {
					item.setName(data.getString(data.getColumnIndex("name")));
					item.setPwd(data.getString(data.getColumnIndex("pwd")));
					/*
					 * 将预算限额保存为Int类型，不可输入保存小数，降低用户体验度
					 * */
					/*item.setDayMax(data.getInt(data.getColumnIndex("dayMax")));
					item.setMonthMax(data.getInt(data.getColumnIndex("monthMax")));
					item.setYearMax(data.getInt(data.getColumnIndex("yearMax")));*/
					/*
					* （修改）将预算限额保存为Float类型，可输入保存小数，提高用户体验度（userBean中也需要修改相关参数类型）
					* */
					item.setDayMax(data.getFloat(data.getColumnIndex("dayMax")));
					item.setMonthMax(data.getFloat(data.getColumnIndex("monthMax")));
					item.setYearMax(data.getFloat(data.getColumnIndex("yearMax")));
					break;
				}
			} finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return item;
	}

	/**
	 * 获取账号是否存在，用以验证登录
	 */
	public boolean isUserExist(String name ,String pwd) {
		boolean isExist = false;
		Cursor data = mDbAdapter.queryUserFromDB(name,pwd);
		if (data != null) {
			try {
				while (data.moveToNext()) {
					isExist = true;
					break;
				}
			} finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return isExist;
	}

	public boolean updateUserPwd(String name, String pwd) {
		if (TextUtils.isEmpty(name)||TextUtils.isEmpty(pwd))
			return false;
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("pwd", pwd);
		return mDbAdapter.updateUserInDB(cv,name);
	}

	public boolean updateUserInfo(String name, Float dayLimit, Float monthLimit, Float yearLimit) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("dayMax", dayLimit);
		cv.put("monthMax", monthLimit);
		cv.put("yearMax", yearLimit);
		return mDbAdapter.updateUserInDB(cv,name);
	}




	public void insertTypeBean(TypeBean info) {
		if (info == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("user", info.getUserName());
		cv.put("name", info.getName());
		cv.put("type", info.getType());
		mDbAdapter.insertZichan2DB(cv);
	}

	public void delSingleTypeBean(TypeBean info) {
		if (info == null)
			return;
		mDbAdapter.deleteSingleZichan(info.getUserName(),info.getName());
	}

	/**
	 * 获取所有资产成员
	 */
	public List<TypeBean> getTypeBeanFromDB(String name ,String type) {
		List<TypeBean> infos = new ArrayList<TypeBean>();
		Cursor data = mDbAdapter.queryZichanFromDB(name,type);
		if (data != null) {
			try {
				while (data.moveToNext()) {
					TypeBean item = new TypeBean();
					item.setUserName(data.getString(data.getColumnIndex("user")));
					item.setName(data.getString(data.getColumnIndex("name")));
					item.setType(data.getString(data.getColumnIndex("type")));
					item.setId(data.getString(data.getColumnIndex("id")));
					infos.add(item);
				}
			} finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}

	public void updateTypeBean(String id,TypeBean typeBean) {
		if (TextUtils.isEmpty(id)||typeBean == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("id", typeBean.getId());
		cv.put("name", typeBean.getName());
		cv.put("type", typeBean.getType());
		cv.put("user", typeBean.getUserName());
		mDbAdapter.updateSingleZichanInDB(cv,id);
	}



	public void insertPayBean(PayBean info) {
		if (info == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("user", info.getUserName());
		cv.put("name", info.getName());
		mDbAdapter.insertPay2DB(cv);
	}

	public void delSinglePayBean(PayBean info) {
		if (info == null)
			return;
		mDbAdapter.deleteSinglePay(info.getName());
	}

	/**
	 * 获取所有支付成员
	 */
	public List<PayBean> getPayBeanFromDB() {
		List<PayBean> infos = new ArrayList<>();
		Cursor data = mDbAdapter.queryPayFromDB();
		if (data != null) {
			try {
				while (data.moveToNext()) {
					PayBean item = new PayBean();
					item.setId(data.getString(data.getColumnIndex("id")));
					item.setUserName(data.getString(data.getColumnIndex("user")));
					item.setName(data.getString(data.getColumnIndex("name")));
					infos.add(item);
				}
			} finally {
				data.close();
				data = null;
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}

	public void updatePayBean(String id,PayBean payBean) {
		if (TextUtils.isEmpty(id)||payBean == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("name", payBean.getName());
		cv.put("user", payBean.getUserName());
		mDbAdapter.updateSinglePayInDB(cv,id);
	}

}
