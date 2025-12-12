./gradlew :UserService:build -x test
docker build -t userservice:latest ./UserService

./gradlew :CartService:build -x test
docker build -t cartservice:latest ./CartService

./gradlew :BookingService:build -x test
docker build -t bookingservice:latest ./BookingService

./gradlew :DocumentService:build -x test
docker build -t documentservice:latest ./DocumentService

<#
./gradlew :CatalogService:build
docker build -t catalogservice:latest ./CatalogService

./gradlew :CommentService:build
docker build -t commentservice:latest ./CommentService

./gradlew :NotificationService:build
docker build -t notificationservice:latest ./NotificationService

./gradlew :PaymentService:build
docker build -t paymentservice:latest ./PaymentService

./gradlew :RoomService:build
docker build -t roomservice:latest ./RoomService
#>

docker compose up --build