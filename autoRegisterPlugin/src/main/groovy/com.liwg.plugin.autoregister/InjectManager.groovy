package com.liwg.plugin.autoregister

import com.liwg.plugin.autoregister.inject.ActivityInject
import com.liwg.plugin.autoregister.inject.BaseInject
import com.liwg.plugin.autoregister.inject.FragmentInject
import com.liwg.plugin.autoregister.inject.ViewInject
import javassist.CtClass

class InjectManager {
    static List<BaseInject> injectList = new ArrayList<>()
    static {
        injectList.add(new ActivityInject())
        injectList.add(new FragmentInject())
        injectList.add(new ViewInject())
    }


    static void inject(CtClass ctClass, String path) {
        if (isActivity(ctClass)) {
            //判断当前类是否是Activity，主要通过父类进行判断
            injectList.get(0).inject(ctClass)
        } else if (isFragment(ctClass)) {
            //判断当前类是否是Fragment，主要通过父类进行判断
            injectList.get(1).inject(ctClass)
        } else if (isView(ctClass)) {
            //判断当前类是否是View，主要通过父类进行判断
            injectList.get(2).inject(ctClass)
        }
        // 代码注入完成之后回写代码
        ctClass.writeFile(path)
    }


    static boolean isFragment(CtClass ctClass) {
        CtClass superClass = ctClass.getSuperclass()
        if (superClass == null) {
            return false
        }
        CtClass fragmentClass = superClass.getClassPool().get("android.app.Fragment")
        CtClass supportFragmentClass = superClass.getClassPool().get("android.support.v4.app.Fragment")
        while (superClass != fragmentClass && superClass != supportFragmentClass) {
            if (superClass.getPackageName().startsWith("java.")) {
                return false
            }
            superClass = superClass.getSuperclass()
        }
        return true
    }

    static boolean isActivity(CtClass ctClass) {
        CtClass superClass = ctClass.getSuperclass()
        if (superClass == null) {
            return false
        }
        CtClass activityClass = superClass.getClassPool().get("android.app.Activity")
        CtClass appCompatActivityClass = superClass.getClassPool().get("android.support.v7.app.AppCompatActivity")
        CtClass fragmentActivityClass = superClass.getClassPool().get("android.support.v4.app.FragmentActivity")
        while (superClass != activityClass && superClass != fragmentActivityClass && superClass != appCompatActivityClass) {
            if (superClass.getPackageName().startsWith("java.")) {
                return false
            }
            superClass = superClass.getSuperclass()
        }
        return true
    }

    static boolean isView(CtClass ctClass) {
        CtClass superClass = ctClass.getSuperclass()
        if (superClass == null) {
            return false
        }
        CtClass viewClass = superClass.getClassPool().get("android.view.View")
        while (superClass != viewClass) {
            if (superClass.getPackageName().startsWith("java.")) {
                return false
            }
            superClass = superClass.getSuperclass()
        }
        return true
    }

    static String getRegisterMethodBody() {
        StringBuilder sb = new StringBuilder()
        sb.append("if(!EventBus.getDefault().isRegistered(this)){")
        sb.append("EventBus.getDefault().register(this);}")
        return sb.toString()
    }

    static String getUnRegisterMethodBody() {
        StringBuilder sb = new StringBuilder()
        sb.append("if(EventBus.getDefault().isRegistered(this)){")
        sb.append("EventBus.getDefault().unregister(this);}")
        return sb.toString()
    }

}
