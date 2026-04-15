# Removing `MyAppGlideModule` from `@d11/react-native-fast-image`

If you’re using Glide within your application using an `AppGlideModule` then you’ll need to prevent the inclusion of the `AppGlideModule` in this package.

To accomplish this you can add this to your `android/build.gradle`:

```gradle
project.ext {
    excludeAppGlideModule = true
}
```

If you can't make changes to the `android` folder because you are using [Continuous Native Generation](https://docs.expo.dev/workflow/continuous-native-generation/), place [this file](../plugins/withExcludeAppGlideModule.js) at `./plugins/withExcludeAppGlideModule.js` and add `"./plugins/withExcludeAppGlideModule.js"` to the `plugins` section of your `app.json`. Then, run `npx expo prebuild --clean` to regenerate the `android` folder.
