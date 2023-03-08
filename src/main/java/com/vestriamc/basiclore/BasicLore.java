package com.vestriamc.basiclore;

import com.vestriamc.basiclore.commands.LoreCommand;
import com.vestriamc.basiclore.commandTabCompletion.LoreCommandCompletion;
import com.vestriamc.basiclore.listeners.DisallowPlacingLoreItems;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class BasicLore extends JavaPlugin {

    public static MiniMessage miniMessage;
    public static Logger logger;

    public static NamespacedKey loreKey;

    @Override
    public void onEnable() {
        miniMessage = MiniMessage.miniMessage();
        logger = this.getLogger();

        loreKey = new NamespacedKey(this, "lore_key");

        PluginCommand command = getCommand("lore");
        if (command != null) {
            command.setExecutor(new LoreCommand());
            command.setTabCompleter(new LoreCommandCompletion());
        }

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new DisallowPlacingLoreItems(), this);

    }

}
