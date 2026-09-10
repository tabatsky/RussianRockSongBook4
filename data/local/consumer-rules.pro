##############################################
# Gson: JSON resource model deserialized from raw assets
# (see JsonResourceLoader.loadNext() -> Gson().fromJson(..., SongBookJsonResourceModel::class.java))
##############################################

# Gson matches JSON keys to field names via reflection, and needs the
# generic Signature attribute to know that `songbook` is a
# List<SongJsonResourceModel> and not a raw List<LinkedTreeMap>.
-keepattributes Signature
-keepattributes *Annotation*

-keep class jatx.russianrocksongbook.database.dbinit.jsonresourcemodel.** { *; }

##############################################
# Room: defensive keep for the entity/DAO/DB contract.
# room-runtime already ships its own consumer rules, but the entity is
# `internal` and referenced by generated *_Impl code, so keep it explicitly
# to be safe against aggressive full-mode optimization/renaming.
##############################################
-keep class jatx.russianrocksongbook.database.db.entities.** { *; }
-keep interface jatx.russianrocksongbook.database.db.dao.** { *; }
-keep class jatx.russianrocksongbook.database.db.AppDatabase { *; }
-keep class jatx.russianrocksongbook.database.db.AppDatabase$* { *; }