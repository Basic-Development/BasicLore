package com.vestriamc.basiclore.listeners;

import com.vestriamc.basiclore.BasicLore;
import com.vestriamc.basiclore.utils.Messages;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public final class LoreItemPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onBlockPlaceEvent(@NotNull final BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (pdc.has(BasicLore.loreKey) || pdc.has(NamespacedKey.fromString("vestriaguilds:track"))
                || pdc.has(NamespacedKey.fromString("items-gen:dust"))) {
            event.getPlayer().sendMessage(Messages.cannotPlaceLoreItems());
            event.setCancelled(true);
        }
    }
}
