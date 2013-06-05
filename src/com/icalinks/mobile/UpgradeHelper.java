package com.icalinks.mobile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.icalinks.common.HttpHelper;
import com.icalinks.common.XmlHelper;
import com.icalinks.mobile.ui.activity.MainActivityGroup;
import com.icalinks.mobile.ui.model.UpgradeInfo;

@TargetApi(Build.VERSION_CODES.FROYO)
public class UpgradeHelper {
	static String TAG = UpgradeHelper.class.getSimpleName();
	private MainActivityGroup mActivity;

	public UpgradeHelper(MainActivityGroup activity) {

		this.mActivity = activity;

	}

	public boolean isNeedUpgrade(UpgradeInfo info) {
		// 检测要不要升级
		if (info.verCode > getVersionCode()) {
			return true;
		}
		return false;
	}

	//@TargetApi(8)
	@SuppressLint("NewApi")
	public UpgradeInfo getUpgradeInfo() {
		// 1。获取升级地址
		String strUpgradeUrl = new ConfigHelper().getUpgradeUrl();

		// 2.获取升级文件
		String strXml = null;
		try {
			strXml = HttpHelper.getHttpResponse(strUpgradeUrl);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// 验证升级信息
		if (strXml == null) {
			Log.e(TAG, "升级信息为null，可能是升级地址有误！");
			return null;
		}

		// 3.解析升级信息
		try {
			Document document = XmlHelper.getDocument(strXml);
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			UpgradeInfo info = new UpgradeInfo();
			int size = nodeList.getLength();
			Node note = null;
			for (int i = 0; i < size; i++) {
				// Note note=nodeList.item(i);;

				note = nodeList.item(i);
				if (note.getNodeName().equals("AppName")) {
					try {
						info.appName = note.getFirstChild().getTextContent();
					} catch (Exception exx) {
						exx.printStackTrace();
					}
				} else if (note.getNodeName().equals("ApkSize")) {
					info.apkSize = Integer.parseInt(note.getFirstChild()
							.getTextContent().replace(",", "")
							.replace("KB", ""));
				} else if (note.getNodeName().equals("ChkCode")) {
					info.chkCode = note.getFirstChild().getTextContent();
				} else if (note.getNodeName().equals("FileUrl")) {
					info.fileUrl = note.getFirstChild().getTextContent();
				} else if (note.getNodeName().equals("PubDate")) {
					info.pubDate = note.getFirstChild().getTextContent();
				} else if (note.getNodeName().equals("Summary")) {
					info.summary = note.getFirstChild().getTextContent();
				} else if (note.getNodeName().equals("VerCode")) {
					info.verCode = Integer.parseInt(note.getFirstChild()
							.getTextContent());
				} else if (note.getNodeName().equals("VerName")) {
					info.verName = note.getFirstChild().getTextContent();
				} else if (note.getNodeName().equals("UpgType")) {
					info.upgType = Integer.parseInt(note.getFirstChild()
							.getTextContent());
				}
			}
			return info;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// 二、升级
	// 1.弹出升级提示对话框
	// 2.用户选择升级
	// 3.下载升级文件包（检验SD卡。。。）
	// 4.执行安装

	/**
	 * 获取应用版本
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager manager = mActivity.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(mActivity.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info.versionCode;
	}

	public void startUpgrade(UpgradeInfo info) { // ggz;w //
													// 在这里先看本地的文件，不存在或验证不通过时才下载//
													// ～

		File apkFile = download(info);

		if (apkFile != null) {
			// 打开文件进行安装
			openFile(apkFile);
		}
	}

	private int mProgress;

	/**
	 * 取得远程文件
	 * 
	 */
	private File download(UpgradeInfo info) {
		try {
			if (!URLUtil.isNetworkUrl(info.fileUrl)) {
			} else {
				URL url = null;
				try {
					url = new URL(info.fileUrl);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				try {
					// 建立连接
					URLConnection conn = url.openConnection();
					conn.setConnectTimeout(5000);
					conn.connect();

					// 下载文件
					InputStream is = conn.getInputStream();
					if (is == null) {
						throw new RuntimeException("stream is null");
					}
					File apkFileDir = new File("//sdcard//download");
					if (!apkFileDir.exists()) {
						apkFileDir.mkdir();
					}
					String strFillePath = "//sdcard//download//" + info.appName
							+ info.verName + ".apk";

					File apkFile = new File(strFillePath);
					Log.e("apkFile.getAbsolutePath()",
							apkFile.getAbsolutePath());

					/* 取得站存盘文件路径 */
					String currentTempFilePath = apkFile.getAbsolutePath();

					/* 将文件写入临时盘 */
					FileOutputStream fos = new FileOutputStream(apkFile);
					int bufsize = 1024;
					byte buf[] = new byte[bufsize];
					int count = 0;
					do {
						int numread = is.read(buf);
						if (numread <= 0) {
							break;
						}
						fos.write(buf, 0, numread);
						if (count % 10 == 0) {
							Thread.sleep(10);
						}
						count++;

						mActivity.updateDownloadingProgress(count); // (这里代表的是1024B即1KB)
						// mProgress = count;// (这里代表的是1024B即1KB)
						// mProgressHandler.sendMessage(mProgressHandler.obtainMessage());//更新进度条

					} while (true);

					try {
						is.close();
					} catch (Exception ex) {
						Log.e(TAG, "error: " + ex.getMessage(), ex);
					}

					return apkFile;
				} catch (Exception exx) {
					exx.printStackTrace();
					Toast.makeText(mActivity, "下载出错了！", Toast.LENGTH_SHORT)
							.show();
					// 退出程序
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/* 在手机上打开文件的method */
	private void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		String type = getMIMEType(f);
		/* 设定intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		mActivity.startActivity(intent);
	}

	/**
	 * 判断文件MimeType的method
	 * 
	 * @param f
	 * @return
	 */
	private String getMIMEType(File file) {
		String type = "";
		String fileName = file.getName();
		/* 取得扩展名 */
		String end = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length()).toLowerCase();

		/* 按扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		/* 如果无法直接打开，就跳出软件清单给使用者选择 */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}
}
