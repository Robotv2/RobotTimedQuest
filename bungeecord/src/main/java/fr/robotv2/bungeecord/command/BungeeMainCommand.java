package fr.robotv2.bungeecord.command;

import fr.robotv2.bungeecord.RTQBungeePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.Locale;

public class BungeeMainCommand extends Command {

    private final RTQBungeePlugin plugin;

    public BungeeMainCommand(RTQBungeePlugin plugin) {
        super("robottimedquest-bungee", null, "rtq-bungee");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "This server is using RobotTimedQuest-Bungee with version " + plugin.getDescription().getVersion() + ".");
            return;
        }

        final String sub = args[0].toLowerCase(Locale.ROOT);
        if(!sender.hasPermission("robottimedquest.command." + sub)) {
            sender.sendMessage(ChatColor.RED + "You don't have the required permission to use this command.");
            return;
        }

        switch (sub) {
            case "reload": {
                plugin.onReload();
                sender.sendMessage(ChatColor.GREEN + "The plugin has been reloaded successfully.");
                break;
            }
        }
    }
}
