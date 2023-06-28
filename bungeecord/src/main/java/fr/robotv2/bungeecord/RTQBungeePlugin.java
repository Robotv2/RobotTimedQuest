package fr.robotv2.bungeecord;

import fr.robotv2.common.ResetService;
import net.md_5.bungee.api.plugin.Plugin;

public class RTQBungeePlugin extends Plugin {

    @Override
    public void onEnable() {
        ResetService service = new ResetService();
    }

    @Override
    public void onDisable() {
    }

    public void onReload() {

    }

}
