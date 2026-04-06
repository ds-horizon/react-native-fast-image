# GitHub Issues Summary

Repository: [dream-horizon-org/react-native-fast-image](https://github.com/dream-horizon-org/react-native-fast-image)
Total Open Issues: 44
Last Updated: 2026-04-07

---

## Latest Issues (Last 2 Months)

| # | Title | Date | Category |
|---|---|---|---|
| [#383](https://github.com/dream-horizon-org/react-native-fast-image/issues/383) | Async image preparation before setting FastImage source | Mar 27, 2026 | Question |
| [#382](https://github.com/dream-horizon-org/react-native-fast-image/issues/382) | Coil support (replace Glide) | Mar 22, 2026 | Feature Request |
| [#379](https://github.com/dream-horizon-org/react-native-fast-image/issues/379) | iOS build fails with `DISABLE_SVG=1` | Mar 04, 2026 | Bug/Build |
| [#378](https://github.com/dream-horizon-org/react-native-fast-image/issues/378) | Android: Duplicate class `com.caverock.androidsvg` | Feb 27, 2026 | Bug/Build |
| [#377](https://github.com/dream-horizon-org/react-native-fast-image/issues/377) | Android build time performance issue | Feb 20, 2026 | Bug/Build |
| [#376](https://github.com/dream-horizon-org/react-native-fast-image/issues/376) | `onLayout` not working on Android Release build | Feb 19, 2026 | Bug |
| [#375](https://github.com/dream-horizon-org/react-native-fast-image/issues/375) | Image fails on some ColorOS devices (DNS/IPv6) | Feb 17, 2026 | Bug |
| [#374](https://github.com/dream-horizon-org/react-native-fast-image/issues/374) | iOS crash in WebP decode path on 8.13.0 | Feb 12, 2026 | Crash |
| [#372](https://github.com/dream-horizon-org/react-native-fast-image/issues/372) | `libdav1d` v1.2.0 known CVEs | Feb 04, 2026 | Security |
| [#371](https://github.com/dream-horizon-org/react-native-fast-image/issues/371) | Thumbnail not reflecting for edited gallery images | Feb 03, 2026 | Bug |

---

## Bugs & Crashes

| # | Issue | Platform |
|---|---|---|
| [#374](https://github.com/dream-horizon-org/react-native-fast-image/issues/374) | iOS crash in WebP decode path (SDImageWebPCoder/libwebp) on 8.13.0 | iOS |
| [#375](https://github.com/dream-horizon-org/react-native-fast-image/issues/375) | Image fails to load on some ColorOS devices (possible DNS/IPv6 issue) | Android |
| [#360](https://github.com/dream-horizon-org/react-native-fast-image/issues/360) | `content://` URI causes crash / fails to load | Android |
| [#350](https://github.com/dream-horizon-org/react-native-fast-image/issues/350) | OutOfMemoryError when displaying GIFs | Android |
| [#376](https://github.com/dream-horizon-org/react-native-fast-image/issues/376) | `onLayout` not working in Release builds | Android |
| [#367](https://github.com/dream-horizon-org/react-native-fast-image/issues/367) | Cached images sometimes don't fire `onLoadEnd`/`onLoad`, only `onLoadStart` | Android |
| [#371](https://github.com/dream-horizon-org/react-native-fast-image/issues/371) | Thumbnail not reflecting for edited images from gallery | iOS/Android |
| [#331](https://github.com/dream-horizon-org/react-native-fast-image/issues/331) | `topFastImageProgress` → KERN_INVALID_ADDRESS at 0x30 | iOS |
| [#326](https://github.com/dream-horizon-org/react-native-fast-image/issues/326) | Placeholder while image loads not visible | iOS |
| [#208](https://github.com/dream-horizon-org/react-native-fast-image/issues/208) | Tint color applied to transparent pixels | Both |
| [#243](https://github.com/dream-horizon-org/react-native-fast-image/issues/243) | Per-request HTTP headers not supported | Both |

---

## Build Issues

| # | Issue |
|---|---|
| [#379](https://github.com/dream-horizon-org/react-native-fast-image/issues/379) | iOS build fails with `DISABLE_SVG=1` — `SDWebImageSVGCoder/SDImageSVGCoder.h` not found |
| [#378](https://github.com/dream-horizon-org/react-native-fast-image/issues/378) | Android: Duplicate class `com.caverock.androidsvg` (JAR vs AAR conflict) |
| [#377](https://github.com/dream-horizon-org/react-native-fast-image/issues/377) | Android build time performance issue |
| [#201](https://github.com/dream-horizon-org/react-native-fast-image/issues/201) | Brownfield RN project: task configuration failures (packageDebugResources etc.) |
| [#266](https://github.com/dream-horizon-org/react-native-fast-image/issues/266) | Kotlin not configured in the project |

---

## Security

| # | Issue |
|---|---|
| [#372](https://github.com/dream-horizon-org/react-native-fast-image/issues/372) | `libdav1d` v1.2.0 (via `libavif`) contains known CVEs |

---

## Feature Requests

| # | Feature |
|---|---|
| [#257](https://github.com/dream-horizon-org/react-native-fast-image/issues/257) | Callback for preload completion |
| [#256](https://github.com/dream-horizon-org/react-native-fast-image/issues/256) | Force reload of images (bypass cache) |
| [#255](https://github.com/dream-horizon-org/react-native-fast-image/issues/255) | Image placeholder support |
| [#344](https://github.com/dream-horizon-org/react-native-fast-image/issues/344) | Partial/selective cache clearing |
| [#339](https://github.com/dream-horizon-org/react-native-fast-image/issues/339) | Cache refresh when URL unchanged but content changed |
| [#363](https://github.com/dream-horizon-org/react-native-fast-image/issues/363) | Add cache info (hit/miss) to `onLoad` event |
| [#303](https://github.com/dream-horizon-org/react-native-fast-image/issues/303) | `Image.getSize` and `Image.getSizeWithHeaders` method support |
| [#294](https://github.com/dream-horizon-org/react-native-fast-image/issues/294) | Align props with RN `Image` component |
| [#242](https://github.com/dream-horizon-org/react-native-fast-image/issues/242) | API to check if an image is cached |
| [#238](https://github.com/dream-horizon-org/react-native-fast-image/issues/238) | `getDiskCacheSize` / `getMemoryCacheSize` APIs *(PR submitted)* |
| [#239](https://github.com/dream-horizon-org/react-native-fast-image/issues/239) | Retrieve local file URI from disk cache |
| [#234](https://github.com/dream-horizon-org/react-native-fast-image/issues/234) | Set `diskCacheSize` limit |
| [#244](https://github.com/dream-horizon-org/react-native-fast-image/issues/244) | Progressive JPEG support |
| [#236](https://github.com/dream-horizon-org/react-native-fast-image/issues/236) | `sources` array for multiple resolutions (like `srcset`) |
| [#240](https://github.com/dream-horizon-org/react-native-fast-image/issues/240) | Web platform support |
| [#158](https://github.com/dream-horizon-org/react-native-fast-image/issues/158) | Custom cache key |
| [#205](https://github.com/dream-horizon-org/react-native-fast-image/issues/205) | Blur radius support *(PR submitted)* |
| [#382](https://github.com/dream-horizon-org/react-native-fast-image/issues/382) | Coil (Kotlin-native) library support instead of Glide |
| [#383](https://github.com/dream-horizon-org/react-native-fast-image/issues/383) | Async image preparation before setting source |

---

## Code Quality / Project Housekeeping

| # | Issue |
|---|---|
| [#272](https://github.com/dream-horizon-org/react-native-fast-image/issues/272) | Unit tests are missing |
| [#268](https://github.com/dream-horizon-org/react-native-fast-image/issues/268) | Kotlin migration task (like RN's #50513 effort) |
| [#267](https://github.com/dream-horizon-org/react-native-fast-image/issues/267) | `clearDiskCache` should run on a background thread |
| [#265](https://github.com/dream-horizon-org/react-native-fast-image/issues/265) | ProGuard rules should use `consumeProguardFiles` |
| [#348](https://github.com/dream-horizon-org/react-native-fast-image/issues/348) | Disallow certain imports for consistency |
| [#347](https://github.com/dream-horizon-org/react-native-fast-image/issues/347) | Prettier should run on PR CI checks |

---

## Key Takeaways

- **Most urgent**: iOS WebP crash (#374) on current release v8.13.0, `content://` URI crash (#360), and `libdav1d` security vulnerability (#372).
- **Most requested feature**: Cache introspection APIs — check if cached, get cache size, force reload — with 5+ related issues (#242, #238, #239, #234, #344).
- **Build pain points**: SVG disable path is broken on both platforms (#379, #378); Android duplicate class conflict is a recurring problem.
- **Android event reliability**: `onLoad`/`onLoadEnd` not firing for cached images (#367) is a notable regression.
- **Good first issues**: Kotlin migration (#268), background thread for `clearDiskCache` (#267), ProGuard (#265), unit tests (#272).
