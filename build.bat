@echo off

rem 创建构建目录
if not exist "build\classes" mkdir "build\classes"

rem 编译Java文件
javac -d "build\classes" "src\main\java\com\momoiptools\IPWhitelistCommand.java" "src\main\java\com\momoiptools\MoMoIPWhitelist.java" "src\main\java\com\momoiptools\PlayerLoginListener.java"

rem 复制资源文件
if not exist "build\classes\config.yml" copy "src\main\resources\config.yml" "build\classes\config.yml"
if not exist "build\classes\plugin.yml" copy "src\main\resources\plugin.yml" "build\classes\plugin.yml"

rem 创建jar文件
jar cvf "MoMoIPWhitelist.jar" -C "build\classes" .

echo Build completed successfully!
echo Jar file created: MoMoIPWhitelist.jar
pause