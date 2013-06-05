package com.icalinks.mobile;

import com.umeng.update.UpdateResponse;

public interface UpgradeListener {
	public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo);
	// switch (updateStatus) {
	// case 0: // has update
	// mUpdateResponse = updateInfo;
	// mHandler.sendMessage(mHandler
	// .obtainMessage(WHAT_HAS_UPDATE));
	// break;
	// case 1: // has no update
	// // Toast.makeText(MoreAboutActivity.this, "没有更新",
	// // Toast.LENGTH_SHORT).show();
	// mHandler.sendMessage(mHandler
	// .obtainMessage(WHAT_NON_UPDATE));
	// break;
	// case 2: // none wifi
	// // Toast.makeText(MoreAboutActivity.this,
	// // "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
	// break;
	// case 3: // time out
	// Toast.makeText(MoreAboutActivity.this, "获取更新超时，请检查网络连接！",
	// Toast.LENGTH_SHORT).show();
	// break;
	// }
	// }
	// }
}
