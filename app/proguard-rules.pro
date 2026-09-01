# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class com.mohammedalkamali.hesabatalwaraqa.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.mohammedalkamali.hesabatalwaraqa.model.**$$serializer { *; }
-keepclassmembers class com.mohammedalkamali.hesabatalwaraqa.model.** {
    *** Companion;
}
