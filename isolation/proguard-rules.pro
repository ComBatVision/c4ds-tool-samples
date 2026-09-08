# No plugin-specific R8 rules needed.
#
# Everything this plugin requires in order to load correctly inside the host — the ToolDescriptor
# and ViewModel keeps, `-repackageclasses`, the host-provided library exclusions and the -dontwarn
# for the map types the SDK exposes but does not publish — is supplied by the `vision.combat.c4.ds`
# Gradle plugin and the SDK's own consumer rules.
