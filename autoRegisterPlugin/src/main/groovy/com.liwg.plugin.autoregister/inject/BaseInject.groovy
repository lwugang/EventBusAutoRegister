package com.liwg.plugin.autoregister.inject

import com.liwg.plugin.autoregister.InjectManager
import javassist.CtClass
import javassist.CtMethod

class BaseInject {
    /**
     * 查找 onCreate 和onDestory
     * @param ctClass
     * @return
     */
    CtMethod[] findOnCreateAndOnDestroyMethod(CtClass ctClass){
    }

    void inject(CtClass ctClass) {
        def methods = findOnCreateAndOnDestroyMethod(ctClass)
        methods[0].insertAfter(InjectManager.getRegisterMethodBody())
        methods[1].insertAfter(InjectManager.getUnRegisterMethodBody())
    }
}
