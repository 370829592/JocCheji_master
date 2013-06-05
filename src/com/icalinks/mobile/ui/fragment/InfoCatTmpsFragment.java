package com.icalinks.mobile.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.common.BluetoothChatService;
import com.icalinks.common.TmpsHelper;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.activity.AbsSubActivity;

public class InfoCatTmpsFragment extends BaseFragment {
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// 标志类型
	public static final String FLAG_NORMAL = "正常";
	public static final String FLAG_SLOW_LEAK = "慢漏";
	public static final String FLAG_QUICK_LEAK = "快漏";
	public static final String FLAG_INSTALL = "学习";

	// 电压标志
	private static final String DIANYA_FIRST = "大于2.7V";
	private static final String DIANYA_SECOND = "2.5-2.7V";
	private static final String DIANYA_THIRD = "2.3-2.5V";
	private static final String DIANYA_FOURTH = "小于2.3V";

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private View mContentView;

	private ListView mListView;

	private ArrayAdapter<String> mDeviceAdapter;

	private BluetoothChatService mChatService;
	private TmpsHelper myHelper;

	// 四个轮胎图片
	private ImageView mLeftTopImg, mLeftBottomImg, mRightTopImg,
			mRightBottomImg;

	// 四个显示轮胎状态的textView
	private TextView mLeftTopTv, mLeftBottomTv, mRightTopTv, mRightBottomTv;

	private AbsSubActivity mActivity;
	
	public InfoCatTmpsFragment(int resId) {
		super(resId);
		// TODO Auto-generated constructor stub
	}

	public InfoCatTmpsFragment() {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mActivity = (AbsSubActivity) getActivitySafe();
		mContentView = inflater.inflate(R.layout.info_tmps, null);
		myHelper = new TmpsHelper(getActivitySafe());
		mChatService = new BluetoothChatService(getActivitySafe(), mHandler);
		if (!myHelper.haveBluetooth()) {
			getActivitySafe().finish();
		} else if (!myHelper.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			getActivitySafe().startActivityForResult(enableIntent,
					TmpsHelper.REQUEST_ENABLE_BT);
		}

		setupView();

		

		return mContentView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle(mActivity.getString(R.string.doc_tmps));
		setListener();
		setupData();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unRegister();
		if (mChatService != null)
			mChatService.stop();
	}

	private void setupView() {

		// 初始化4个轮胎图片
		mLeftTopImg = (ImageView) mContentView.findViewById(R.id.info_tmps_1);
		mRightTopImg = (ImageView) mContentView.findViewById(R.id.info_tmps_2);
		mLeftBottomImg = (ImageView) mContentView
				.findViewById(R.id.info_tmps_3);
		mRightBottomImg = (ImageView) mContentView
				.findViewById(R.id.info_tmps_4);

		// 初始化4个显示轮胎信息的文本框
		mLeftTopTv = (TextView) mContentView.findViewById(R.id.info_tmps_5);
		mRightTopTv = (TextView) mContentView.findViewById(R.id.info_tmps_6);
		mLeftBottomTv = (TextView) mContentView.findViewById(R.id.info_tmps_7);
		mRightBottomTv = (TextView) mContentView.findViewById(R.id.info_tmps_8);

		// 初始化listview
		mListView = (ListView) mContentView
				.findViewById(R.id.info_tmps_listview);
		mListView.setVisibility(View.VISIBLE);
		mDeviceAdapter = new ArrayAdapter<String>(getActivitySafe(),
				R.layout.info_tmps_listview_item);
		mListView.setAdapter(mDeviceAdapter);

	}

	private void setupData() {
		if (mChatService.getState() == mChatService.STATE_NONE) {
			if (myHelper.getPairedDevice() != null)
				mDeviceAdapter.add(myHelper.getPairedDevice());
			// Log.i("info", "deviceAddress:"+myHelper.getPairedDevice());
			register();
			myHelper.scanBluetoothDevices();
		}
		// GlobalApplication.getApplication().getPublicCarrierActivity()
		// .showActionBarProcess();
	}

	private void setListener() {
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.goback();
			}
		});
		// mActionBarButton.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// updateView();
		// }
		// });
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				myHelper.stopScan();
				// unRegister();
				String address = ((TextView) arg1).getText().toString();
				BluetoothDevice device = myHelper.getDevice(address);
				if (device == null) {
					Toast.makeText(getActivitySafe(), "无效的设备地址",
							Toast.LENGTH_SHORT).show();
				} else {
					mChatService.connect(device);
					Toast.makeText(getActivitySafe(), "正在连接到设备",
							Toast.LENGTH_SHORT).show();
					if (mChatService != null) {
						// Only if the state is STATE_NONE, do we know that we
						// haven't started already
						if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
							// Start the Bluetooth chat services
							mChatService.start();
						}
					}
				}
				mListView.setVisibility(View.GONE);
			}
		});
	}

	private void updateView() {
		if (data != null) {
			if (data.flag.equals(FLAG_NORMAL)) {
				mLeftTopImg.setImageResource(R.drawable.info_temps_top_left_g);
			} else {
				mLeftTopImg.setImageResource(R.drawable.info_temps_top_left_r);
			}
			mLeftTopTv.setText(data.flag + "\n" + data.yali + "\n" + data.temp
					+ "\n" + data.dianya);

			if (data.flag.equals(FLAG_NORMAL)) {
				mLeftBottomImg
						.setImageResource(R.drawable.info_temps_bottom_left_g);
			} else {
				mLeftBottomImg
						.setImageResource(R.drawable.info_temps_bottom_left_r);
			}
			mLeftBottomTv.setText(data.flag + "\n" + data.yali + "\n"
					+ data.temp + "\n" + data.dianya);

			if (data.flag.equals(FLAG_NORMAL)) {
				mRightTopImg
						.setImageResource(R.drawable.info_temps_top_right_g);
			} else {
				mRightTopImg
						.setImageResource(R.drawable.info_temps_top_right_r);
			}
			mRightTopTv.setText(data.flag + "\n" + data.yali + "\n" + data.temp
					+ "\n" + data.dianya);

			if (data.flag.equals(FLAG_NORMAL)) {
				mRightBottomImg
						.setImageResource(R.drawable.info_temps_bottom_right_g);
			} else {
				mRightBottomImg
						.setImageResource(R.drawable.info_temps_bottom_right_r);
			}
			mRightBottomTv.setText(data.flag + "\n" + data.yali + "\n"
					+ data.temp + "\n" + data.dianya);

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case TmpsHelper.REQUEST_ENABLE_BT:
			if (resultCode == getActivitySafe().RESULT_OK) {
				// 扫描蓝牙设备
				myHelper.scanBluetoothDevices();

			}
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device != null
						&& device.getBondState() != BluetoothDevice.BOND_BONDED) {
					if (device.getName() != null
							&& device.getName().equals("JETSON TPMS")) {
						// 搜索到了指定蓝牙设备
						mDeviceAdapter.add(device.getAddress());
					}
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// 搜索结束
				if (mChatService.getState() != BluetoothChatService.STATE_CONNECTING) {
					// GlobalApplication.getApplication()
					// .getPublicCarrierActivity().hideActionBarProcess();
				} else {
					// GlobalApplication.getApplication()
					// .getPublicCarrierActivity().showActionBarProcess();
				}

			}
		}

	};

	// 注册搜索蓝牙接收�?
	private void register() {
		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		getActivitySafe().registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		getActivitySafe().registerReceiver(mReceiver, filter);

	}

	// 取消注册
	private void unRegister() {
		getActivitySafe().unregisterReceiver(mReceiver);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				// if(D) Log.i(BluetoothChatService.TAG,
				// "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					// mTitle.setText(R.string.title_connected_to);
					// mTitle.append(mConnectedDeviceName);
					// mConversationArrayAdapter.clear();

					break;
				case BluetoothChatService.STATE_CONNECTING:

					// mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					// mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				// String writeMessage = new String(writeBuf);
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				// String readMessage = new String(readBuf, 0, msg.arg1);
				String readMessage = bytesToHexString(readBuf, msg.arg1);
				// mActionBarButton.setVisibility(View.VISIBLE);
				// GlobalApplication.getApplication().getPublicCarrierActivity()
				// .hideActionBarProcess();
				Log.i("info", readMessage);
				procTmpsData(readMessage);

				// mConversationArrayAdapter.add(mConnectedDeviceName+":  " +
				// readMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				// mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				// Toast.makeText(getApplicationContext(), "Connected to "
				// + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getActivitySafe(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				// GlobalApplication.getApplication().getPublicCarrierActivity()
				// .hideActionBarProcess();
				break;
			}

		}
	};

	private String bytesToHexString(byte[] src, int size) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	// 解析数据并且更新界面
	private void procTmpsData(String msg) {
		if (msg.length() >= 14) {
			data = new TmpsData();
			data.id = msg.substring(0, 6);
			data.yali = Integer.parseInt(msg.substring(6, 10), 16) + "KP";
			data.temp = Integer.parseInt(msg.substring(10, 12), 16) + "℃";
			int i = Integer.parseInt(msg.substring(12, 14), 16);
			// String s = appendZero(i);
			procStatus(i, data);

			Log.i("info", data.yali + ":" + data.temp);
		}
	}

	private void procStatus(int i, TmpsData data) {
		String s = Integer.toBinaryString(i);
		String s2 = s;
		if (s.length() < 8) {
			for (int j = 0; j < 8 - s.length(); j++) {
				s2 = "0" + s2;
			}
		}
		// -------------------------------------------------
		String s3 = s2.substring(6, 8);
		if (s3.equals("00")) {
			data.flag = FLAG_NORMAL;
		} else if (s3.equals("01")) {
			data.flag = FLAG_SLOW_LEAK;
		} else if (s3.equals("10")) {
			data.flag = FLAG_QUICK_LEAK;
		} else if (s3.equals("11")) {
			data.flag = FLAG_INSTALL;
		}
		// -------------------------------------------------
		String s4 = s2.substring(4, 6);
		if (s4.equals("00")) {
			data.dianya = DIANYA_FIRST;
		} else if (s4.equals("01")) {
			data.dianya = DIANYA_SECOND;
		} else if (s4.equals("10")) {
			data.dianya = DIANYA_THIRD;
		} else if (s4.equals("11")) {
			data.dianya = DIANYA_FOURTH;
		}
	}

	private TmpsData data;

	class TmpsData {
		String id;
		String yali;
		String temp;
		String flag;// 快漏还是慢漏
		String dianya;// 传感器电压标志
	}

}
