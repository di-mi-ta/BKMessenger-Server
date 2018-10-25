javac -classpath .\json-simple-1.1.1.jar;.\mysql-connector-java-5.1.45.jar -d .\bin .\src\database\ChatData.java
javac -classpath .\src;.\json-simple-1.1.1.jar;.\mysql-connector-java-5.1.45.jar -d .\bin .\src\bkmessprotocol\BKMessProtocolServer.java
javac -d .\bin .\src\bkmessprotocol\BKMessProtocolClient.java
javac -classpath .\src -d .\bin .\src\client\Client.java
javac -classpath .\src;.\json-simple-1.1.1.jar;.\mysql-connector-java-5.1.45.jar -d .\bin .\src\server\Server.java