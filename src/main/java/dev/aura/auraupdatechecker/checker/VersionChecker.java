package dev.aura.auraupdatechecker.checker;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;

import dev.aura.auraupdatechecker.AuraUpdateChecker;
import dev.aura.auraupdatechecker.util.PluginContainerUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VersionChecker {
    private final Collection<PluginContainer> availablePlugins;
    private List<PluginContainer> checkablePlugins;

    public void checkForPluginAvailability() {
        final Logger logger = AuraUpdateChecker.getLogger();

        logger.debug("Start checking plugins for availability on Ore Repository...");

        if (checkablePlugins != null) {
            logger.info("Already checked plugins for availability!");

            return;
        }

        checkablePlugins = availablePlugins.stream().filter(plugin -> {
            boolean isOnOre = OreAPI.isOnOre(plugin);

            if (isOnOre) {
                logger.debug(
                        "Plugin " + PluginContainerUtil.getPluginString(plugin) + " is available on Ore Repository.");
            }

            return isOnOre;
        }).collect(Collectors.toList());

        logger.debug("Finished checking plugins for availability on Ore Repository!");
        logger.debug(checkablePlugins.size() + " plugins available for update checks!");
    }
}
