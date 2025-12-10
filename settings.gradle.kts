plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "microservices"

include(
    "UserService",
    "CartService",
    "BookingService",
    "CatalogService",
    "CommentService",
    "DocumentService",
    "NotificationService",
    "PaymentService",
    "RoomService",
)