# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in Android SDK tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If you use reflection or JNI examine the entire source tree.
#proguard-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

-dontobfuscate
-keepattributes Signature
-keepattributes InnerClasses
-keep class com.example.sunlightdisplaytuner.** { *; } // Adjust to your package name
-keep public class * extends androidx.lifecycle.ViewModel
-keep public class * extends androidx.lifecycle.AndroidViewModel

# Keep Jetpack Compose specific rules
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <fields>;
}
-keepclassmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}
-keepclassmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <fields>;
}
-keepclasseswithmembers public class * {
    @androidx.compose.runtime.Composable <methods>;
}
-keepclasseswithmembers public class * {
    @androidx.compose.runtime.Composable <fields>;
}
-keepclasseswithmembers public class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}
-keepclasseswithmembers public class * {
    @androidx.compose.ui.tooling.preview.Preview <fields>;
}
