package com.liwg.plugin.autoregister.inject

import javassist.CtClass
import javassist.CtMethod

class ActivityInject extends BaseInject{

    /**
     * 查找 onCreate 和onDestory
     * @param ctClass
     * @return
     */
    @Override
    CtMethod[] findOnCreateAndOnDestroyMethod(CtClass ctClass) {
        def methods = ctClass.getDeclaredMethods()
        CtMethod onCreateMethod, onDestroyMethod
        for (int i = 0; i < methods.size(); i++) {
            if (methods[i].name == "onCreate") {
                onCreateMethod = methods[i]
            } else if ("onDestroy" == methods[i].name) {
                onDestroyMethod = methods[i]
            }
        }
        // 如果没有找到声明的方法，就自己创建一个方法
        if (onCreateMethod == null) {
            onCreateMethod = createMethod(ctClass, "onCreate")
        }
        if (onDestroyMethod == null) {
            onDestroyMethod = createMethod(ctClass, "onDestroy")
        }
        return [onCreateMethod, onDestroyMethod] as CtMethod[]
    }

    CtMethod createMethod(CtClass ctClass, String methodName) {
        def ctMethod
        if ("onCreate" == methodName) {
            ctMethod = CtMethod.make("public void onCreate(android.os.Bundle bundle){super.onCreate(bundle);}", ctClass)
        } else if ("onDestroy" == methodName) {
            ctMethod = CtMethod.make("public void onDestroy(){super.onDestroy();}", ctClass)
        }
        ctClass.addMethod(ctMethod)
        return ctMethod
    }

}
