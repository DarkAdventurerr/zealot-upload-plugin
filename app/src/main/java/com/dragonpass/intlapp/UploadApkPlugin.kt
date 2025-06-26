package com.dragonpass.intlapp

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.model.ClassField
import com.dragonpass.intlapp.PluginUtils.isEmpty
import com.dragonpass.intlapp.params.GitLogParams
import com.dragonpass.intlapp.params.SendDingParams
import com.dragonpass.intlapp.params.SendFeishuParams
import com.dragonpass.intlapp.params.SendWeixinGroupParams
import com.dragonpass.intlapp.params.UploadZealotParams
import com.dragonpass.intlapp.task.BuildAndUploadTask
import com.dragonpass.intlapp.task.OnlyUploadTask
import org.gradle.api.DomainObjectSet
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import java.util.Locale


class UploadApkPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val uploadParams = project.extensions.create(
            PluginConstants.UPLOAD_PARAMS_NAME,
            UploadZealotParams::class.java
        )
        createParams(project)
        dependsOnOnlyUploadTask(uploadParams, project)
        UploadZealotParams.getConfig(project)
        SendWeixinGroupParams.getWeixinGroupConfig(project)
        val androidComponents =
            project.extensions.findByType(AndroidComponentsExtension::class.java)

        androidComponents?.onVariants { variant ->
            // **** 关键改动在这里：类型改为 Provider<Directory> ****
            val apkFolderProvider: Provider<Directory> = variant.artifacts.get(SingleArtifact.APK)
            val variantName =
                variant.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            project.tasks.register(
                PluginConstants.TASK_EXTENSION_NAME + variantName,
                BuildAndUploadTask::class.java
            ) { task ->
                // 使用 map 从 Provider<Directory> 中提取 RegularFile
                task.apkFile.set(
                    apkFolderProvider.map { apkDir ->
                        // apkDir 是一个 Directory 对象，代表包含 APK 的目录
                        // 需要找到目录中的实际 .apk 文件

                        // 这是一个健壮的查找方法：遍历目录查找唯一的 .apk 文件
                        val apksInDir = apkDir.asFile.listFiles { file -> file.extension == "apk" }

                        if (apksInDir.isNullOrEmpty()) {
                            throw GradleException("No APK found in ${apkDir.asFile.absolutePath} for variant ${variant.name}. Make sure an APK is built.")
                        }
                        if (apksInDir.size > 1) {
                            // 可选：如果找到多个 APK，记录警告或抛出错误
                            project.logger.warn("Multiple APKs found in ${apkDir.asFile.absolutePath} for variant ${variant.name}. Using the first one found: ${apksInDir[0].name}")
                        }
                        // 返回找到的第一个 APK 文件作为 RegularFile
                        project.layout.projectDirectory.file(apksInDir[0].absolutePath)
                    }
                )

                task.variantName.set(variantName)
                task.targetProject = project
            }
        }
        project.afterEvaluate { project1 ->
            val appExtension :AppExtension?= project1.extensions.findByName(PluginConstants.ANDROID_EXTENSION_NAME) as AppExtension?
            appExtension?.let {
                printBuildConfigFields(project1,uploadParams.buildTypeName, appExtension)
                val appVariants: DomainObjectSet<ApplicationVariant> = appExtension.applicationVariants
                appVariants.forEach {
                    if (it.buildType != null) {
                        dependsOnTask(it, uploadParams, project1, appExtension)
                    }
                }
            }
        }
    }

    private fun dependsOnTask(
        applicationVariant: ApplicationVariant,
        uploadParams: UploadZealotParams,
        project1: Project,
        appExtension: AppExtension
    ) {
        val variantName: String = getVariantName(applicationVariant, uploadParams)
        //创建我们，上传的task任务
        val uploadTask = (project1.tasks.findByName(PluginConstants.TASK_EXTENSION_NAME + variantName) as BuildAndUploadTask?)?:project1.tasks
            .create(
                PluginConstants.TASK_EXTENSION_NAME + variantName,
                BuildAndUploadTask::class.java
            )
        uploadTask.init(project1)

        //依赖关系 。上传依赖打包，打包依赖clean。
//        applicationVariant.getAssembleProvider().get().dependsOn(project1.getTasks().findByName("clean"));
        uploadTask.dependsOn(applicationVariant.assembleProvider.get()).doLast {
            printBuildConfigFields(
                project1,
                variantName,
                appExtension
            )
        }
    }

    private fun printBuildConfigFields(project1: Project,variantName: String, appExtension: AppExtension) {
        val byName: com.android.build.gradle.internal.dsl.BuildType? =
            appExtension.buildTypes.findByName(variantName.lowercase(Locale.getDefault()))
        if (byName != null) {
            val buildConfigFields: Map<String, ClassField> = byName.buildConfigFields
            buildConfigFields.forEach { (t, u) ->
                project1.logger.warn(
                    String.format(
                        "> [%s]:%s", t.lowercase(
                            Locale.getDefault()
                        ), u.value
                    )
                )
            }
        }
    }

    private fun getVariantName(
        applicationVariant: ApplicationVariant,
        uploadParams: UploadZealotParams
    ): String {
        var variantName: String? =
            applicationVariant.name.substring(0, 1)
                .uppercase(Locale.getDefault()) + applicationVariant.name.substring(1)
        if (isEmpty(variantName)) {
            variantName =
                if (isEmpty(uploadParams.buildTypeName)) "Release" else uploadParams.buildTypeName
        }
        return variantName.orEmpty()
    }


    private fun createParams(project: Project) {
        project.extensions.create(PluginConstants.GIT_LOG_PARAMS_NAME, GitLogParams::class.java)
        project.extensions.create(PluginConstants.DING_PARAMS_NAME, SendDingParams::class.java)
        project.extensions.create(PluginConstants.FEISHU_PARAMS_NAME, SendFeishuParams::class.java)
        project.extensions.create(
            PluginConstants.WEIXIN_GROUP_PARAMS_NAME,
            SendWeixinGroupParams::class.java
        )
    }

    private fun dependsOnOnlyUploadTask(uploadParams: UploadZealotParams, project1: Project) {
        val uploadTask = project1.tasks.create(
            PluginConstants.TASK_EXTENSION_NAME_ONLY_UPLOAD,
            OnlyUploadTask::class.java
        )
        uploadTask.init(project1)
    }
}