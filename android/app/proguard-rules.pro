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

