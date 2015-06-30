package com.jjlink.jieyun.njuwlan.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjlink.jieyun.R;
import com.jjlink.jieyun.njuwlan.entity.UpInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zlu on 15-3-18.
 */
public class UpdateUtil {
    private Context context;
    // 文件分隔符
    private static final String FILE_SEPARATOR = "/";
    // 外存sdcard存放路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + FILE_SEPARATOR + "autoupdate" + FILE_SEPARATOR;
    // 更新应用版本标记
    private static final int UPDARE_TOKEN = 0x29;
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 0x31;

    // 下载应用存放全路径
    private String FILE_NAME = FILE_PATH + "jieyun.apk";
    private String message = "有新版本发布，建议您更新！";
    private String spec = "http://p.nju.edu.cn:8080/app/update.xml";
    private String cancelMessage = "当前版本是最新版本";
    // 下载应用的对话框
    private Dialog dialog;
    // 下载应用的进度条
    private ProgressBar progressBar;
    // 进度条的当前刻度值
    private int curProgress;
    // 用户是否取消下载
    private boolean isCancel;

    public UpdateUtil(Context context) {
        this.context = context;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDARE_TOKEN:
                    progressBar.setProgress(curProgress);
                    break;

                case INSTALL_TOKEN:
                    installApp();
                    break;
            }
        }
    };

    /**
     * 检查是否需要更新程序
     *
     * @throws Exception
     */
    public UpInfo checkVersion() {
        UpInfo upInfo = new UpInfo();
        URL url;
        try {
            url = new URL(spec);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            upInfo = ParseXmlUtils.parseXml(connection.getInputStream());
            if (upInfo.getVersion().equals(VersionUtil.getCurVersion(context))) {
                //Toast.makeText(context, cancelMessage, Toast.LENGTH_SHORT).show();
            } else {
                showUpdateDialog();
            }
        } catch (Exception e) {
            Log.d("检查更新:", "连不上服务器");
        }
        return upInfo;
    }


    private void installApp() {
        File appFile = new File(FILE_NAME);
        if (!appFile.exists()) {
            return;
        }
        //跳转到新版本app安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 显示更新对话框
     *
     * @param
     */
    private void showUpdateDialog() {

        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_my_alert_dialog, null);
        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.setContentView(layout);

        TextView dialog_msg = (TextView) layout.findViewById(R.id.dialog_msg);
        dialog_msg.setText(message);
        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.dialog_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(), "cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


    }

    /**
     * 下载进度对话框
     */
    private void showDownloadDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.progressbar, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("软件版本更新");
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isCancel = true;
            }
        });
        dialog = builder.create();
        dialog.show();
        downlaodApp();
    }

    /**
     * 下载新版本app
     */
    private void downlaodApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                InputStream in = null;
                FileOutputStream out = null;
                HttpURLConnection conn = null;

                try {
                    UpInfo upInfo = checkVersion();
                    url = new URL(upInfo.getUrl());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    long fileLength = conn.getContentLength();
                    in = conn.getInputStream();
                    File filePath = new File(FILE_PATH);
                    if (!filePath.exists()) {
                        filePath.mkdir();
                    }
                    out = new FileOutputStream(new File(FILE_NAME));
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    long readedLenght = 01;
                    while ((len = in.read(buffer)) != -1) {
                        //用户点击取消按钮，下载中断
                        if (isCancel) {
                            break;
                        }
                        out.write(buffer, 0, len);
                        readedLenght += len;
                        curProgress = (int) (((float) readedLenght / fileLength) * 100);
                        handler.sendEmptyMessage(UPDARE_TOKEN);
                        if (readedLenght >= fileLength) {
                            dialog.dismiss();
                            handler.sendEmptyMessage(INSTALL_TOKEN);
                            break;
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    Log.i("ex:", e.getMessage());
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }


}
