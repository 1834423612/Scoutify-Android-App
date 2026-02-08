# GitHub Actions CI/CD Workflow Documentation

## Workflow Overview

This workflow implements an automated CI/CD pipeline for the Android application with the following features:
- ✅ Triggers only on commits to the master branch
- ✅ Runs code quality checks (Lint) and tests to ensure no runtime errors
- ✅ Automatically builds and packages APK file
- ✅ Uploads APK as a build artifact (retained for 30 days)

## Workflow Details

### Trigger Conditions
```yaml
on:
  push:
    branches:
      - master
```
The workflow is triggered only when code is pushed to the `master` branch.

### Build Steps

1. **Checkout code** - Pull the latest code from the repository
2. **Set up JDK 21** - Configure Java development environment (using Temurin distribution)
3. **Grant execute permission for gradlew** - Ensure the Gradle wrapper can be executed
4. **Run Lint** - Check code quality and potential issues
5. **Run Tests** - Execute unit tests to ensure code correctness
6. **Build Debug APK** - Compile and package the Android application
7. **Upload APK** - Upload the generated APK file as a build artifact

### Error Handling

If any step fails (Lint check, tests, or build), the workflow will stop execution and will not proceed to package the APK. This ensures that only code passing all checks is packaged.

### Usage

#### Triggering the Workflow
Push code to the master branch:
```bash
git push origin master
```

#### Downloading the Built APK
1. Visit the "Actions" tab in the GitHub repository
2. Select the corresponding workflow run
3. Download the `app-debug` APK file from the "Artifacts" section

### Configuration Details

- **JDK Version**: 21 (matches project configuration)
- **Gradle Cache**: Enabled to speed up builds
- **APK Retention Period**: 30 days
- **Build Type**: Debug APK

### Customization

To modify the workflow, edit the `.github/workflows/android-build.yml` file:

- Change trigger branches: Modify the `branches` list
- Build Release APK: Change `assembleDebug` to `assembleRelease`
- Adjust APK retention period: Modify the `retention-days` value
- Add other build steps: Add new steps in the `steps` section

### Dependencies

This workflow uses the following GitHub Actions:
- `actions/checkout@v4` - Checkout code
- `actions/setup-java@v4` - Set up Java environment
- `actions/upload-artifact@v4` - Upload build artifacts

### Notes

1. Ensure the project's `gradlew` file is committed to the repository
2. Ensure `gradle/wrapper/gradle-wrapper.jar` is committed
3. If you need a signed Release APK, configure signing keys
4. First run may take longer to download dependencies

### Status Badge

You can add a workflow status badge to your README:
```markdown
![Android CI/CD](https://github.com/1834423612/Scoutify-Android-App/workflows/Android%20CI%2FCD/badge.svg)
```
