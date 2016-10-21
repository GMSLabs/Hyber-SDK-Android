## Square Picasso specific rules ##
## https://square.github.io/picasso/ ##

-dontwarn com.squareup.okhttp.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# Retrofit 2.X
## https://square.github.io/retrofit/ ##
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

# RxAndroid
-dontwarn rx.internal.util.unsafe.**

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-keep class com.hyber.** { *; }

-keep class com.facebook.stetho.** { *; }

-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
