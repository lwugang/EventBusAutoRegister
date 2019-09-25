# com.eventbus.autoregister
### EventBus自动注册与反注册
- ##### 支持在Activity onCreate方法进行注册，在onDestroy方法中反注册
- ##### 支持在Fragment onCreate方法进行注册，在onDestroy方法中反注册
- ##### 支持在View onAttachedToWindow方法进行注册，在onDetachedFromWindow方法中反注册

### 使用方式和平常使用没有任何区别
    省去了手动注册与反注册代码
```java
public class MainActivity extends AppCompatActivity {
    @Subscribe
    public void onEvent(Object object){

    }
}
```
### 利用Gradle插件生成的代码效果
```java
public class MainActivity extends AppCompatActivity {
    public MainActivity() {
    }

    @Subscribe
    public void onEvent(Object object) {
    }

    public void onCreate(Bundle var1) {
        super.onCreate(var1);
        //自动在Activity的onCreate中生成注册代码
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    public void onDestroy() {
        super.onDestroy();
        //自动在Activity的onDestroy中生成反注册代码
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }
}
```
