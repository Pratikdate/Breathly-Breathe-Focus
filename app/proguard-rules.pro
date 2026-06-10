# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/shanacoder/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
# http://developer.android.com/guide/developing/tools/proguard.html

# Add any custom rules here that might be needed for your project.
# For example, if you're using Room or other libraries that use reflection.

-keep class com.shanacoder.breathly.data.** { *; }
-keep class com.shanacoder.breathly.ui.theme.** { *; }
