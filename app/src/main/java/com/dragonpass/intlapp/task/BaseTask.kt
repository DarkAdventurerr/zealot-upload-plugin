package com.dragonpass.intlapp.task

import com.dragonpass.intlapp.PluginConstants
import com.dragonpass.intlapp.PluginUtils
import com.dragonpass.intlapp.helper.CmdHelper
import com.dragonpass.intlapp.helper.FileIOUtils
import com.dragonpass.intlapp.helper.HttpHelper
import com.dragonpass.intlapp.helper.SendMsgHelper
import com.dragonpass.intlapp.model.ZealotUploadResultEntity
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import java.io.File

open class BaseTask : DefaultTask() {
    var targetProject: Project? = null

    fun init(project: Project?) {
        this.targetProject = project
        description = PluginConstants.TASK_DES
        group = PluginConstants.TASK_GROUP_NAME
    }

    fun uploadZealot(
        apiKey: String?,
        appName: String?,
        zealotHost: String?,
        installType: Int,
        buildPassword: String?,
        uploadChannelKey: String?,
        buildUpdateDescription: String?,
        buildUpdateDescriptionFile: String?,
        buildInstallDate: Int,
        buildChannelShortcut: String?,
        apkFile: File?
    ) {
        val password = buildPassword ?: ""
        val updateDescription = buildUpdateDescription ?: ""
        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("token", apiKey ?: "")
            .addFormDataPart("channel_key", uploadChannelKey ?: "")
            .addFormDataPart("changelog", updateDescription)
            .addFormDataPart("password", password)
        if (apkFile != null) {
            bodyBuilder.addFormDataPart(
                "file", apkFile.name,
                RequestBody.create(MediaType.parse("application/octet-stream"), apkFile)
            )
        }
        println("upload zealot ---\nhost:$zealotHost\nfile:${apkFile?.absolutePath}")
        val request = getRequestBuilder()
            .url("$zealotHost/api/apps/upload")
            .post(bodyBuilder.build())
            .build()
        try {
            val response = HttpHelper.getOkHttpClient().newCall(request).execute()
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!.string()
                println("upload zealot --- result: $result")
                if (!PluginUtils.isEmpty(result)) {
                    val zealotUploadResultEntity = Gson().fromJson(result, ZealotUploadResultEntity::class.java)
                    if (zealotUploadResultEntity == null) {
                        println("upload zealot ---- error while result is null")
                        return
                    }
                    val apkDownloadUrl = zealotUploadResultEntity.release_url
                    println("上传成功，应用链接: $apkDownloadUrl")
                    val gitLog = CmdHelper.checkGetGitParamsWithLog(targetProject!!)
                    SendMsgHelper.sendMsgToDingDing(targetProject!!, zealotUploadResultEntity, gitLog, password)
                    SendMsgHelper.sendMsgToFeishu(targetProject!!, zealotUploadResultEntity, gitLog, password)
                    try {
                        val file = File(buildUpdateDescriptionFile)
                        FileIOUtils.writeFileFromString(file, "")
                    } catch (_: Exception) {}
                    SendMsgHelper.sendMsgToWeiXinGroup(targetProject!!, zealotUploadResultEntity, gitLog, password, updateDescription)
                }
            } else {
                println("upload zealot ---- upload error:${response.message()}")
            }
            println("******************* upload zealot: finish *******************")
        } catch (e: Exception) {
            println("upload zealot ---- request failed $e")
        }
    }

    private fun getRequestBuilder(): Request.Builder =
        Request.Builder()
            .addHeader("Connection", "Keep-Alive")
            .addHeader("Charset", "UTF-8")
} 