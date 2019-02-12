package dev.aura.updatechecker.checker;

import dev.aura.updatechecker.AuraUpdateChecker;
import dev.aura.updatechecker.util.PluginContainerUtil;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

@RequiredArgsConstructor
public class VersionChecker {
  private final Collection<PluginContainer> availablePlugins;

  private List<PluginContainer> checkablePlugins = null;
  private final List<Task> scheduledTasks = new LinkedList<>();
  private final AtomicBoolean active = new AtomicBoolean(false);

  public void start() {
    active.set(true);

    // Starting new task to discover checkable plugins
    Task task =
        startTask(
            Task.builder()
                .execute(this::checkForPluginAvailability)
                .delay(5, TimeUnit.SECONDS)
                .async()
                .name(AuraUpdateChecker.ID + "-availablity-check"));

    if (task != null) {
      scheduledTasks.add(task);

      AuraUpdateChecker.getLogger().debug("Started task \"" + task.getName() + '"');
    }
  }

  public void stop() {
    active.set(false);

    scheduledTasks.forEach(Task::cancel);
  }

  public void checkForPluginAvailability(Task self) {
    final Logger logger = AuraUpdateChecker.getLogger();

    logger.debug("Start checking plugins for availability on Ore Repository...");

    if (checkablePlugins != null) {
      logger.info("Already checked plugins for availability!");

      return;
    }

    OreAPI.resetErrorCounter();

    checkablePlugins =
        availablePlugins
            .parallelStream()
            .filter(
                plugin -> {
                  final String pluginName = PluginContainerUtil.getPluginString(plugin);
                  logger.trace(
                      "Started checking if plugin "
                          + pluginName
                          + " is available on Ore Repository.");

                  final boolean isOnOre = OreAPI.isOnOre(plugin);

                  if (isOnOre) {
                    logger.debug("Plugin " + pluginName + " is available on Ore Repository.");
                  } else {
                    logger.trace("Plugin " + pluginName + " is NOT available on Ore Repository.");
                  }

                  return isOnOre;
                })
            .collect(Collectors.toList());

    if (OreAPI.getErrorCounter() >= availablePlugins.size()) {
      logger.warn(
          "It appears that your internet connection is down or not working properly, because all HTTPS requests failed.");
      logger.info("If it is working again, run \"/uc reload\", to reenable update checking.");
    }

    logger.debug("Finished checking plugins for availability on Ore Repository!");
    logger.debug(checkablePlugins.size() + " plugins available for update checks!");

    scheduledTasks.remove(self);
  }

  @Nullable
  private Task startTask(Task.Builder taskBuilder) {
    if (!active.get()) {
      return null;
    }

    return taskBuilder.submit(AuraUpdateChecker.getInstance());
  }
}
