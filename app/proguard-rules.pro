# Add project specific ProGuard / R8 rules here.

# Preserve crash report stack trace line numbers while hiding raw file paths
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve RadioType and RadioMode enum constant names used in UI spinners and preferences
-keep enum com.Runner.CQMiau.radios.** { *; }

# Keep USB Serial & Apache Commons Net callback/driver entry points safe from warnings
-dontwarn org.apache.commons.net.**
-dontwarn com.felhr.**
-keep class com.felhr.usbserial.** { *; }
