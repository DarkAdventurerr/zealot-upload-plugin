package com.dragonpass.intlapp.task

import com.dragonpass.intlapp.params.UploadZealotParams
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

abstract class BuildAndUploadTask : BaseTask() {
    // 注入 ObjectFactory：Gradle 会自动提供一个实例
    @get:Inject // 必须使用这个注解才能让Gradle注入
    abstract val objects: ObjectFactory

    // 使用 ObjectFactory 初始化 apkFile 属性
    @get:Input
    val apkFile: Property<RegularFile> = objects.fileProperty()

    // 同样，使用 ObjectFactory 初始化 variantName 属性（推荐做法）
    @get:Input
    val variantName: Property<String> = objects.property(String::class.java)

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