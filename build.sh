protoc -I=protobuf --java_out=src/main/java protobuf/Entities.proto
mvn clean install
