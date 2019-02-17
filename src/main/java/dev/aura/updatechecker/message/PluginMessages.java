package dev.aura.updatechecker.message;

import dev.aura.lib.messagestranslator.Message;
import dev.aura.updatechecker.AuraUpdateChecker;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

@RequiredArgsConstructor
public enum PluginMessages implements Message {
  // Notifications
  NOTIFICATION_UPDATE_AVAILABLE_PADDING("updateAvailablePadding"),
  NOTIFICATION_UPDATE_AVAILABLE_TITLE("updateAvailableTitle"),
  NOTIFICATION_UPDATE_AVAILABLE("updateAvailable"),
  NOTIFICATION_UPDATE_AVAILABLE_CURRENT("updateAvailableCurrent"),
  NOTIFICATION_UPDATE_AVAILABLE_RECOMMENDED("updateAvailableRecommended"),
  NOTIFICATION_UPDATE_AVAILABLE_LATEST("updateAvailableLatest");

  @Getter private final String stringPath;

  public Text getMessage() {
    return getMessage(null);
  }

  public Text getMessage(Map<String, String> replacements) {
    final String message = getMessageRaw(replacements);

    return TextSerializers.FORMATTING_CODE.deserialize(message);
  }

  public String getMessageRaw() {
    return getMessageRaw(null);
  }

  public String getMessageRaw(Map<String, String> replacements) {
    return AuraUpdateChecker.getTranslator().translateWithFallback(this, replacements);
  }
}
