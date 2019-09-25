package com.liwg.plugin.autoregister

import com.android.build.api.transform.*
import com.google.common.collect.Sets
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AnnotationsAttribute
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.lang.annotation.Annotation
import java.util.regex.Pattern

class AutoRegisterTransfomer extends Transform {
    Project project
    ClassPool mPool

    AutoRegisterTransfomer(Project project) {
        // 构造函数，我们将Project保存下来备用
        this.project = project
    }

    @Override
    String getName() {
        // 设置我们自定义的Transform对应的Task名称
        return "EventBusAutoRegister"
    }


    @Override
    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型这样确保其他类型的文件不会传入
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Sets.immutableEnumSet(QualifiedContent.DefaultContentType.CLASSES)
    }


    @Override
// 指定Transform的作用范围
    Set<QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
                QualifiedContent.Scope.SUB_PROJECTS, QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        mPool = new ClassPool()
        mPool.appendSystemPath()

        inputs.each { TransformInput input ->
            //目录遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //往类中注入代码
                inject(directoryInput.file.getAbsolutePath(), project.AutoRegister.pkgSuffix, project)
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                //将 input 的目录复制到 output 指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //jar文件遍历
            input.jarInputs.each { JarInput jarInput ->
                //往类中注入代码
                inject(jarInput.file.getAbsolutePath(), project.AutoRegister.pkgSuffix, project)

                //重命名输出文件（同目录 copyFile 会冲突）
                def jarName = jarInput.name
                def md5Name = jarInput.file.hashCode()
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }

    private void inject(String path, String packageName, Project project) {
        mPool.appendClassPath(path)
        mPool.appendClassPath(project.android.bootClasspath[0].toString())
        // 导入需要操作的包
        mPool.importPackage("org.greenrobot.eventbus")
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse {
                File file ->
                    String filePath = file.absolutePath
                    //过滤系统文件
                    if (filePath.endsWith(".class") && !filePath.contains('R$')
                            && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
                        int index = filePath.indexOf(packageName) + project.AutoRegister.pkgSuffix.length() + 1
                        int end = filePath.length() - 6 // .class = 6
                        // 截取类名
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        CtClass ctClass = mPool.getOrNull(className)
                        if (ctClass == null) {
                            println("不支持的类：" + className)
                            return
                        }
                        mPool.importPackage(ctClass.getPackageName())
                        mPool.importPackage(ctClass.getSuperclass().getPackageName())
                        // 如果类冻结了需要解冻结否则无法操作
                        if (ctClass.isFrozen())
                            ctClass.defrost()
                        boolean isInject = false
                        //遍历类中的所有方法
                        for (CtMethod method : ctClass.getDeclaredMethods()) {
                            try {
                                // 获取方法上面的注解
                                def annoString = method.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag).toString()
                                // 判断是否包含指定的注解
                                if (annoString!=null&&annoString.contains("@org.greenrobot.eventbus.Subscribe")) {
                                    isInject = true
                                    break
                                }
                            } catch (Throwable e) {
                                e.printStackTrace()
                            }
                        }
                        if (isInject) {
                            // 如果包含指定的注解，在当前类中注入初始化代码
                            InjectManager.inject(ctClass,path)
                        }
                        ctClass.freeze()
                    }
            }
        }
    }

}