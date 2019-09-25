package com.liwg.plugin.autoregister

import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoRegisterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.configurations.all { configuration ->
            if (name != "implementation" && name != "compile") {
                return
            }
//            configuration.dependencies.add(project.dependencies.create("com.android.support.test.uiautomator:uiautomator-v18:2.1.2"))
        }
        //注册扩展参数，可以gradle中自己配置
        project.extensions.create("AutoRegister", EventBusAutoRegisterExtension)
        //注册一个Transform
        def transform = new AutoRegisterTransfomer(project)
        project.android.registerTransform(transform)
    }
}

class EventBusAutoRegisterExtension {
    // 根据这个字符串做包名截取拿到类名
    String pkgSuffix = "classes"
}