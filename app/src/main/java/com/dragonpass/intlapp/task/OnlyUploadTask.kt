package com.dragonpass.intlapp.task

import com.dragonpass.intlapp.PluginUtils
import com.dragonpass.intlapp.params.UploadZealotParams
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File

class OnlyUploadTask : BaseTask() {
    @TaskAction
    fun uploadToZealot() {
        init(targetProject ?: project)
        val params = UploadZealotParams.getConfig(targetProject!!)
        if (PluginUtils.isEmpty(params.uploadApkFilePath)) {
            println("The configured APK upload path (uploadApkFilePath) is empty")
            throw GradleException("The configured APK upload path (uploadApkFilePath) is empty!")
        }
        val apkDir = File(params.uploadApkFilePath)
        if (!apkDir.exists()) {
            throw GradleException("The configured APK upload file does not exist!")
        }
        println("ApkDir path: ${apkDir.absolutePath}")
        var apk: File? = null
        if (apkDir.name.endsWith(".apk")) {
            apk = apkDir
        } else {
            apkDir.listFiles()?.let { files ->
                for (i in files.size - 1 downTo 0) {
                    val apkFile = files[i]
                    if (apkFile.exists() && apkFile.name.endsWith(".apk")) {
                        apk = apkFile
                        break
                    }
                }
            }
        }
        if (apk == null || apk?.exists()!=true) {
            throw GradleException("The configured APK upload file does not exist!")
        }
        println("Final upload apk path: ${apk?.absolutePath}")
        uploadZealot(
            params.apiKey,
            params.appName,
            params.uploadChannelKey,
            params.buildInstallType,
            params.buildPassword,
            params.uploadChannelKey,
            params.buildUpdateDescription,
            params.buildUpdateDescriptionFile,
            params.buildInstallDate,
            params.buildChannelShortcut,
            apk
        )
    }
} 