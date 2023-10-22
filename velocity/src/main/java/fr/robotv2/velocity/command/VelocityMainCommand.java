package fr.robotv2.velocity.command;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.robotv2.velocity.RTQVelocityPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Locale;

public class VelocityMainCommand implements SimpleCommand {

    @Inject
    private ProxyServer server;

    private final RTQVelocityPlugin plugin;

    public VelocityMainCommand(RTQVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {

        final CommandSource source = invocation.source();
        final String[] args = invocation.arguments();

        if(args.length == 0) {
            final String version = server.getPluginManager().getPlugin(RTQVelocityPlugin.PLUGIN_ID)
                    .flatMap(pluginContainer -> pluginContainer.getDescription().getVersion())
                    .orElse("UNKNOWN");
            source.sendMessage(Component.text("This server is using RobotTimedQuest-Velocity with version " + version + ".").color(NamedTextColor.GREEN));
            return;
        }


        final String sub = args[0].toLowerCase(Locale.ROOT);

        if(!source.hasPermission("robottimedquest.command." + sub)) {
            source.sendMessage(Component.text("You don't have the required permission to use this command.").color(NamedTextColor.RED));
            return;
        }

        switch (sub) {
            case "reload": {
                plugin.onReload();
                source.sendMessage(Component.text("The plugin has been reloaded successfully.").color(NamedTextColor.GREEN));
                break;
            }
        }
    }

    @Override
    public List<String> suggest(final Invocation invocation) {
        return ImmutableList.of("reload");
    }
}
