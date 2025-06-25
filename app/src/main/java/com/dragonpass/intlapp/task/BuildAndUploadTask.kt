package com.dragonpass.intlapp.task

import com.dragonpass.intlapp.params.UploadZealotParams
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class BuildAndUploadTask : BaseTask() {
    @get:Input
    abstract val apkFile: Property<RegularFile>
    @get:Input
    abstract val variantName: Property<String>

    @TaskAction
    fun uploadToPGY() {
        val apk = apkFile.get().asFile
        if (!apk.exists()) {
            throw GradleException("The compiled APK file to upload does not exist!")
        }
        println("Final upload apk path: ${apk.absolutePath}")
        val params = UploadZealotParams.getConfig(targetProject!!)
        uploadZealot(
            params.apiKey,
            params.appName,
            params.zealotHost,
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