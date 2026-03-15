# MoMoIPWhitelist

限制指定玩家只能从特定IP地址登录服务器的Minecraft插件。

## 功能特性

- **IP白名单**：限制指定玩家只能从特定IP地址登录
- **多IP支持**：单个玩家可以设置多个允许的IP地址
- **命令管理**：通过 `/moipwl` 或 `/momoipwhitelist` 命令管理白名单
- **Tab补全**：命令支持Tab自动补全
- **登录日志**：记录玩家登录尝试、成功登录和被拒绝的登录
- **管理员通知**：当玩家登录或被拒绝时通知拥有权限的管理员
- **配置灵活**：可以在config.yml中配置通知设置
- **数据统计**：集成bstats统计，帮助了解插件使用情况

## 命令

- `/moipwl add <player> <ip>`：添加玩家的IP白名单
- `/moipwl remove <player> <ip>`：移除玩家的IP白名单
- `/moipwl list`：显示所有在白名单中的玩家
- `/moipwl list <player>`：显示特定玩家的IP白名单
- `/moipwl reload`：重新加载配置文件

## 权限

- `momoipwhitelist.admin`：允许管理IP白名单（默认OP）
- `momoipwhitelist.notify`：允许接收登录通知（默认OP）

## 配置

配置文件位于 `plugins/MoMoIPWhitelist/config.yml`，包含以下设置：

- `notifications.enabled`：是否启用通知
- `notifications.notifyOnSuccess`：登录成功时是否通知
- `notifications.notifyOnDenial`：登录被拒绝时是否通知
- `players`：IP白名单配置

## 示例配置

```yaml
# 通知配置
notifications:
  # 是否启用通知
  enabled: true
  # 登录成功时是否通知
  notifyOnSuccess: true
  # 登录被拒绝时是否通知
  notifyOnDenial: true

# IP白名单配置
players:
  # 示例配置
  Notch:
    - 192.168.1.1
  Herobrine:
    - 192.168.1.2
```

## 日志

登录日志会记录在 `plugins/MoMoIPWhitelist/logs/login-log.txt` 文件中，包含登录时间、玩家名、IP地址、登录状态以及登录后的世界和坐标。

## 版本支持

- Minecraft 1.21.x
- Minecraft 1.20.x（可能兼容）

## 构建

使用Gradle构建：

```bash
./gradlew build
```

或使用Maven构建：

```bash
mvn clean package
```

## 统计

[![bStats](https://bstats.org/api/bukkit/12345/charts/servers.svg)](https://bstats.org/plugin/bukkit/MoMoIPWhitelist)

## 许可证

MIT License

## 作者

xyoska
