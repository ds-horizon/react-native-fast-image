const plugin = require("@expo/config-plugins");

/** @type {plugin.ConfigPlugin} */
const withExcludeAppGlideModule = (config) => {
  const tab = "  "; // 2 spaces
  return plugin.withProjectBuildGradle(config, (config) => {
    if (config.modResults.language === "groovy") {
      let buildGradle = config.modResults.contents;

      if (!buildGradle.includes("excludeAppGlideModule = true")) {
        const excludeAppGlideModule = `${tab}excludeAppGlideModule = true`;
        const extBlock = /ext\s*\{/;
        if (extBlock.test(buildGradle)) {
          buildGradle = buildGradle.replace(
            extBlock,
            `ext {\n${excludeAppGlideModule}`,
          );
        } else {
          buildGradle += `\nproject.ext {\n${excludeAppGlideModule}\n}\n`;
        }
      }

      config.modResults.contents = buildGradle;
    }

    return config;
  });
};

module.exports = withExcludeAppGlideModule;
