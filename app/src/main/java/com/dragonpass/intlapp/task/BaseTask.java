package com.dragonpass.intlapp.task;


import com.android.build.gradle.api.BaseVariant;
import com.dragonpass.intlapp.model.ZealotUploadResultEntity;
import com.google.gson.Gson;

import com.dragonpass.intlapp.PluginConstants;
import com.dragonpass.intlapp.PluginUtils;
import com.dragonpass.intlapp.helper.CmdHelper;
import com.dragonpass.intlapp.helper.FileIOUtils;
import com.dragonpass.intlapp.helper.HttpHelper;
import com.dragonpass.intlapp.helper.SendMsgHelper;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

public class BaseTask extends DefaultTask {

    protected BaseVariant mVariant;
    protected Project mTargetProject;

    public void init(BaseVariant variant, Project project) {
        this.mVariant = variant;
        this.mTargetProject = project;
        setDescription(PluginConstants.TASK_DES);
        setGroup(PluginConstants.TASK_GROUP_NAME);
    }

    public void uploadZealot(String apiKey, String appName, String zealotHost, int installType, String buildPassword, String uploadChannelKey, String buildUpdateDescription, String buildUpdateDescriptionFile, int buildInstallDate, String buildChannelShortcut, File apkFile) {
        //builder
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        bodyBuilder.addFormDataPart("token", apiKey);
        bodyBuilder.addFormDataPart("channel_key", uploadChannelKey);
        bodyBuilder.addFormDataPart("changelog", buildUpdateDescription);
        System.out.println("upload zealot ---");
        Request request = getRequestBuilder()
                .url(String.format("%s/api/apps/upload", zealotHost))
                .post(bodyBuilder.build())
                .build();
        try {
            Response response = HttpHelper.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String result = response.body().string();
                System.out.println("upload zealot --- getCOSToken result: " + result);
                if (!PluginUtils.isEmpty(result)) {
                    ZealotUploadResultEntity zealotUploadResultEntity = new Gson().fromJson(result, ZealotUploadResultEntity.class);
                    if (zealotUploadResultEntity == null) {
                        System.out.println("upload zealot ---- error while result is null");
                        return;
                    }
                    String apkDownloadUrl = zealotUploadResultEntity.getRelease_url();
                    System.out.println("上传成功，应用链接: " + apkDownloadUrl);
                    String gitLog = CmdHelper.checkGetGitParamsWithLog(mTargetProject);
                    SendMsgHelper.sendMsgToDingDing(mVariant, mTargetProject, zealotUploadResultEntity, gitLog, buildPassword);
                    SendMsgHelper.sendMsgToFeishu(mVariant, mTargetProject, zealotUploadResultEntity, gitLog, buildPassword);
                    try {
                        File file = new File(buildUpdateDescriptionFile);
                        FileIOUtils.writeFileFromString(file, "");
                    } catch (Exception e) {
                    }
                    SendMsgHelper.sendMsgToWeiXinGroup(mVariant, mTargetProject, zealotUploadResultEntity, gitLog, buildPassword, buildUpdateDescription);
                }
            } else {
                System.out.println("upload zealot ---- request getCOSToken call failed");
            }
            System.out.println("******************* upload zealot: finish *******************");
        } catch (Exception e) {
            System.out.println("upload zealot ---- request failed " + e);
        }
    }


    private Request.Builder getRequestBuilder() {
        return new Request.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Charset", "UTF-8");
    }

}