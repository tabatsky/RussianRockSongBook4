##############################################
# Gson / Retrofit: model classes used for JSON (de)serialization
##############################################

# Keep generic signatures - Gson needs them at runtime to figure out the
# actual type argument of parameterized fields (e.g. List<CloudSongApiModel>).
# Without this, R8 strips them and Gson deserializes list/map elements as
# raw LinkedTreeMap instead of your model class -> ClassCastException.
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod

# Keep field names (Gson matches JSON keys to field names via reflection)
# and the no-arg-less constructor Gson calls via Unsafe for these DTOs.
-keep class jatx.russianrocksongbook.networking.apimodels.** { *; }
-keep class jatx.russianrocksongbook.networking.converters.** { *; }
-keep class jatx.russianrocksongbook.domain.repository.cloud.result.** { *; }
-keep class jatx.russianrocksongbook.domain.models.cloud.** { *; }
-keep class jatx.russianrocksongbook.domain.models.appcrash.** { *; }
-keep class jatx.russianrocksongbook.domain.models.warning.** { *; }

# Retrofit interface used with reflection-based dynamic proxy
-keep interface jatx.russianrocksongbook.networking.songbookapi.SongBookAPI { *; }

-dontwarn org.codehaus.mojo.animal_sniffer.AnnotationClass
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*