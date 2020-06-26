# Add project specific ProGuard rules here.
# You can control the set init applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.init.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#列出未混淆的类和成员
-printseeds proguard/seeds.txt
#列出从 apk 中删除的代码
-printusage proguard/unused.txt
#混淆前后的映射
-printmapping proguard/mapping.txt

#原生接口不被混淆
-keepclasseswithmembers,includedescriptorclasses class * {
    native <methods>;
}

-keepclassmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclassmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持activity名不被混淆
-keep class * extends com.ashwashing.pro.ui.activity.BaseActivity


-keep class * implements android.os.Parcelable


# okhttp
-dontwarn okio.**

# bean
#-keep class com.ashwashing.pro.api.bean.* # 自定义数据模型的bean目录

# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8

# okhttp
-dontwarn okio.**

# bean
-keep class com.ashwashing.pro.api.bean.**{*;} # 自定义数据模型的bean目录

#okhttp
-dontwarn kotlin.coroutines.**

