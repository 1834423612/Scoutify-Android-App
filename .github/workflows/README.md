# GitHub Actions CI/CD 工作流程说明

## 工作流程概述

此工作流程实现了 Android 应用的自动化 CI/CD 流程，满足以下要求：
- ✅ 仅在 master 分支有提交时触发
- ✅ 运行代码质量检查（Lint）和测试，确保程序没有运行错误
- ✅ 自动打包生成 APK 文件
- ✅ 上传 APK 作为构建产物，保留 30 天

## 工作流程详情

### 触发条件
```yaml
on:
  push:
    branches:
      - master
```
工作流程只在代码推送到 `master` 分支时触发。

### 构建步骤

1. **检出代码** - 从仓库拉取最新代码
2. **设置 JDK 21** - 配置 Java 开发环境（使用 Temurin 发行版）
3. **授予 gradlew 执行权限** - 确保 Gradle 包装器可以执行
4. **运行 Lint 检查** - 检查代码质量和潜在问题
5. **运行测试** - 执行单元测试，确保代码正确性
6. **构建 Debug APK** - 编译并打包 Android 应用
7. **上传 APK** - 将生成的 APK 文件上传为构建产物

### 错误处理

如果任何步骤失败（Lint 检查、测试或构建），工作流程会停止执行，不会继续打包 APK。这确保只有通过所有检查的代码才会被打包。

### 使用方法

#### 触发工作流程
将代码推送到 master 分支：
```bash
git push origin master
```

#### 下载构建的 APK
1. 访问 GitHub 仓库的 "Actions" 标签页
2. 选择相应的工作流程运行
3. 在 "Artifacts" 部分下载 `app-debug` APK 文件

### 配置说明

- **JDK 版本**: 21（与项目配置匹配）
- **Gradle 缓存**: 启用，加快构建速度
- **APK 保留期限**: 30 天
- **构建类型**: Debug APK

### 自定义配置

如需修改工作流程，可编辑 `.github/workflows/android-build.yml` 文件：

- 修改触发分支：更改 `branches` 列表
- 构建 Release APK：将 `assembleDebug` 改为 `assembleRelease`
- 调整 APK 保留期限：修改 `retention-days` 值
- 添加其他构建步骤：在 `steps` 中添加新步骤

### 依赖项

此工作流程使用以下 GitHub Actions：
- `actions/checkout@v4` - 检出代码
- `actions/setup-java@v4` - 设置 Java 环境
- `actions/upload-artifact@v4` - 上传构建产物

### 注意事项

1. 确保项目的 `gradlew` 文件已提交到仓库
2. 确保 `gradle/wrapper/gradle-wrapper.jar` 已提交
3. 如果需要签名的 Release APK，需要配置签名密钥
4. 首次运行可能需要较长时间下载依赖项

### 状态徽章

可以在 README 中添加工作流程状态徽章：
```markdown
![Android CI/CD](https://github.com/1834423612/Scoutify-Android-App/workflows/Android%20CI%2FCD/badge.svg)
```
