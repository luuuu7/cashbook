package com.terry.account.util;

import android.os.Environment;

public final class C {

	public static final class SP {

		public static final String MONTH_MAX = "month_max";

		// 记住密码
		public static final String remember_login = "shared_remember_login";
		// 记住密码：账号
		public static final String account = "shared_key_accout";
		// 记住密码：密码
		public static final String pwd = "shared_key_pwd";
		//闹钟时间
		public static final String ALARM_TIME = "alarm_time";

	}

	public static final class COME_TYPE {

		//收入
		public static final String ACCOUNT = "accounttype";

		//收入
		public static final String IN = "income";
		//支出
		public static final String OUT = "outcome";

	}


	public static final class INTENT_TYPE {
		//数据修改模式(int) 0:增加 1:删除 2:修改 3：查看
		public static final String DATA_DATATYPE = "data_datatype";
		//更新的对象信息
		public static final String DATA_OBJ = "data_obj";
		//附带的信息
		public static final String DATA_OBJ_EXTRA = "data_obj_extra";

	}

	public static final class NORMAL {

		public static final String PIC_PATH_DIR = Environment.getExternalStorageDirectory()
				+ "/GD/Camera/";// 图片存储路径
	}

	public static final class AVFILE_NAME {
		public static final String PIC = "Pic";
	}




}
