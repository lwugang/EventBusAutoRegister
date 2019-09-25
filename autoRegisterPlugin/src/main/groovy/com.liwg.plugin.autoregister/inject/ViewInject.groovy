package com.liwg.plugin.autoregister.inject

import javassist.CtClass
import javassist.CtMethod

class ViewInject extends BaseInject {
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
            if (methods[i].name == "onAttachedToWindow") {
                onCreateMethod = methods[i]
            } else if ("onDetachedFromWindow" == methods[i].name) {
                onDestroyMethod = methods[i]
            }
        }
        // 如果没有找到声明的方法，就自己创建一个方法
        if (onCreateMethod == null) {
            onCreateMethod = createMethod(ctClass, "onAttachedToWindow")
        }
        if (onDestroyMethod == null) {
            onDestroyMethod = createMethod(ctClass, "onDetachedFromWindow")
        }
        return [onCreateMethod, onDestroyMethod] as CtMethod[]
    }

    CtMethod createMethod(CtClass ctClass, String methodName) {
        def ctMethod
        if ("onDetachedFromWindow" == methodName) {
            ctMethod = CtMethod.make("public void onDetachedFromWindow(){super.onDetachedFromWindow();}", ctClass)
        } else if ("onAttachedToWindow" == methodName) {
            ctMethod = CtMethod.make("public void onAttachedToWindow(){super.onAttachedToWindow();}", ctClass)
        }
        ctClass.addMethod(ctMethod)
        return ctMethod
    }
}
