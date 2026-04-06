# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is `@d11/react-native-fast-image`, a Dream11 fork of react-native-fast-image optimized for the New React Native Architecture (TurboModules + Fabric Renderer). It is a drop-in replacement for React Native's `Image` component using SDWebImage (iOS) and Glide (Android).

## Commands

```bash
# Build
yarn build              # Clean + TypeScript compilation

# Lint
yarn lint               # ESLint on src/**/*.{js,jsx,ts,tsx}

# Type check
yarn type-check         # tsc --noEmit

# Test
yarn test               # Jest with coverage
yarn test --testNamePattern="<name>"   # Run a single test by name
yarn test src/index.test.tsx           # Run a specific test file

# Example app
yarn example android
yarn example ios
```

## Architecture

### JS/TS Layer (`src/`)

**`src/index.tsx`** — Main component. At runtime it detects the RN architecture and selects the appropriate native bridge:

```ts
// Fabric vs. old arch
const FastImageView = isFabricEnabled
    ? require('./FastImageViewNativeComponent').default
    : requireNativeComponent('FastImageView')

// TurboModule vs. legacy NativeModules
const FastImageViewModule = isTurboModuleEnabled
    ? require('./NativeFastImageViewModule').default
    : NativeModules.FastImageViewModule
```

**`src/FastImageViewNativeComponent.ts`** — Codegen spec for Fabric Renderer (new arch).

**`src/NativeFastImageViewModule.ts`** — TurboModule spec for `preload`, `clearMemoryCache`, `clearDiskCache`.

### Native iOS (`ios/FastImage/`)

- `FFFastImageView.mm` — Core `UIImageView` subclass; manages SDWebImage request lifecycle and fires events.
- `FFFastImageViewManager.mm` — View manager; bridges JS props to native.
- `FFFastImageViewComponentView.mm` — Fabric Renderer support (new arch only).
- `FFFastImageViewModule.mm` — Native module (`preload`, cache clearing).

Dependencies (via CocoaPods): SDWebImage 5.21.0, SDWebImageWebPCoder, SDWebImageAVIFCoder, SDWebImageSVGCoder (optional).

### Native Android (`android/src/`)

- `main/java/com/dylanvann/fastimage/FastImageViewWithUrl.java` — Core `AppCompatImageView` subclass; manages Glide requests and events.
- `newarch/` and `oldarch/` source sets — Separate `FastImageViewManager.java` implementations for TurboModule/Fabric vs. legacy bridge.
- `FastImageViewModuleImplementation.java` — Shared native module logic.
- `FastImageGlideModule.java` — Glide App Module; registers SVG/AVIF/WebP decoders and can be disabled via `excludeAppGlideModule=true` in `gradle.properties`.

Dependencies: Glide 4.16.0 with AVIF, WebP, and optional SVG support.

### Dual-Architecture Build Pattern

Both Android source sets (`newarch/` and `oldarch/`) define a `FastImageViewManager.java` with the same class name. The active build variant is selected by Gradle based on whether `newArchEnabled=true` is set. The same pattern applies for iOS: `FFFastImageViewComponentView` is compiled only for Fabric.

SVG support is optional:
- iOS: exclude `SDWebImageSVGCoder` subspec
- Android: set `DISABLE_SVG=1` env var or `disableSvg=true` in `gradle.properties`

### Props Flow

Headers are transformed from `{key: value}` object to `[{name, value}]` array before being passed to native (required for codegen type safety). On Android, `defaultSource` is converted to a URI string; on iOS, it's passed as a number (asset reference).

## Key Configuration Files

- `jest.config.cjs` — Jest preset `react-native`, coverage collected from `src/`
- `tsconfig.json` — Strict TypeScript
- `babel.config.cjs` — Includes `@babel/plugin-transform-class-properties` for private fields
- `RNFastImage.podspec` — iOS pod dependencies
- `android/build.gradle` — Gradle config with conditional `svg`/`newarch`/`oldarch` source sets
