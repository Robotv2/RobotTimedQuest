package fr.robotv2.bukkit.enums;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import fr.robotv2.bukkit.util.text.ColorUtil;
import fr.robotv2.bukkit.util.text.PlaceholderUtil;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public enum Messages {

    PREFIX("prefix"),
    QUEST_LOADED_SUCCESSFULLY("quest_loaded_successfully"),
    COSMETICS_ENABLED("cosmetics_enabled"),
    COSMETICS_DISABLED("cosmetics_disabled"),
    ;

    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public String getMessage() {
        return RTQBukkitPlugin.getInstance()
                .getMessageFile()
                .getConfiguration()
                .getString(getPath());
    }

    public TranslatableMessage toTranslatableMessage() {
        final String message = getMessage();
        return new TranslatableMessage(message, message == null || message.trim().isEmpty());
    }

    public static class TranslatableMessage {

        private String message;
        private final boolean disabled;

        public TranslatableMessage(String message, boolean disabled) {
            this.message = message;
            this.disabled = disabled;
        }

        public TranslatableMessage prefix() {
            if(!disabled) {
                this.message = Objects.requireNonNull(PREFIX.getMessage()).concat(message);
            }
            return this;
        }

        public TranslatableMessage color() {
            if(!disabled) {
                this.message = ColorUtil.color(message);
            }
            return this;
        }

        public TranslatableMessage replace(CharSequence target, CharSequence replacement) {
            if(!disabled) {
                this.message = this.message.replace(target, replacement);
            }
            return this;
        }

        public <T> TranslatableMessage placeholder(PlaceholderUtil.InternalPlaceholder<T> placeholder, T value) {
            if(!disabled) {
                this.message = placeholder.parse(value, this.message);
            }
            return this;
        }

        public <A, B> TranslatableMessage relationPlaceholder(PlaceholderUtil.RelationalInternalPlaceholder<A, B> placeholder, A fst, B snd) {
            if(!disabled) {
                this.message = placeholder.parse(fst, snd, this.message);
            }
            return this;
        }

        public void send(CommandSender sender) {
            if(!disabled) {
                sender.sendMessage(message);
            }
        }
    }
}
