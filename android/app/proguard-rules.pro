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