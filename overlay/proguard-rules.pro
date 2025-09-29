-dontoptimize
-dontshrink
-repackageclasses

# Don't warn about missing kotlinx.serialization classes
-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.descriptors.SerialDescriptor
-dontwarn kotlinx.serialization.encoding.CompositeEncoder
-dontwarn kotlinx.serialization.internal.SerializationConstructorMarker
