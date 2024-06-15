enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")

include(":core:utils")
include(":core:internet")
include(":core:compose")
include(":core:background")

include(":data:db")
include(":data:models")
include(":data:parsing")

include(":features:viewer")
include(":features:shikimori")
include(":features:chapters")
include(":features:library")
include(":features:categories")
include(":features:statistic")
include(":features:storage")
include(":features:settings")
include(":features:schedule")
include(":features:catalog")
include(":features:catalogs:allhen")
include(":features:accounts")

include(":sample:testshikimori")
include(":sample:test_compose")
include(":ksp")
