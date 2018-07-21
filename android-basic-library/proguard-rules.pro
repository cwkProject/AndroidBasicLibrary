# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Software\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 自己的库
-keepclassmembernames class * extends org.cwk.android.library.work.WorkModel {
    protected ** onTaskUri();
}
-keepclassmembernames class * extends org.cwk.android.library.architecture.preferences.PersistenceConfigModel {*;}

# okhttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# rxjava2
-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }

# 屏蔽日志
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int d(...);
}
-assumenosideeffects class org.cwk.android.library.util.LogUtil {
    public static void v(...);
}
