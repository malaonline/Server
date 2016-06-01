-verbose
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }


# bufferknife, taken from http://jakewharton.github.io/butterknife/index.html#proguard
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#JPush
#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

#-libraryjars libs/jpush-android-2.1.0.jar
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }

# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.JsonObject { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}
#这句非常重要，主要是滤掉 com.bgb.scan.model包下的所有.class文件不进行混淆编译
-keep class com.bgb.scan.model.** {*;}

#entity和result
-keep class com.malalaoshi.android.entity.** {*;}
-keep class com.malalaoshi.android.result.** {*;}

-dontwarn com.pingplusplus.**
-keep class com.pingplusplus.** {*;}

-dontwarn  com.alipay.**
-keep class com.alipay.** {*;}

-dontwarn  com.ta.utdid2.**
-keep class com.ta.utdid2.** {*;}

-keep class com.ut.device.** {*;}

-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}

-dontwarn  com.baidu.paysdk.**
-keep class com.baidu.paysdk.** {*;}

-dontwarn  com.baidu.android.pay.**
-keep class com.baidu.android.pay.** {*;}

-dontwarn  com.unionpay.**
-keep class com.unionpay.** {*;}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry


#okhttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn okio.**

#eventbus
-keepclassmembers class ** {
    public void onEvent*(**);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keep class com.fasterxml.jackson.** {*;}

#entity和result
-keep class com.malalaoshi.android.entity.** {*;}
-keep class com.malalaoshi.android.result.** {*;}

# 保持UI类不被混淆
-keep class android.** {*; }
-keep public class * extends android.view
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.pm
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep class * implements java.io.Serializable { *;}


# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

-keep class com.facebook.imagepipeline.gif.** { *; }
-keep class com.facebook.imagepipeline.webp.** { *; }
-dontwarn com.facebook.**

-keep public class com.squareup.okhttp.OkUrlFactory
-keep public class com.squareup.okhttp.OkHttpClient
-dontwarn com.squareup.picasso.**