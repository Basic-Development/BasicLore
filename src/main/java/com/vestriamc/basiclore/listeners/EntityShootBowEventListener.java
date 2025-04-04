package com.vestriamc.basiclore.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.vestriamc.basiclore.BasicLore;
import com.vestriamc.basiclore.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class EntityShootBowEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityShootBowEvent(final EntityShootBowEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        @Nullable ItemStack projectileItem = event.getConsumable();

        if (projectileItem == null) {
            return;
        }

        ItemMeta meta = projectileItem.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (pdc.has(BasicLore.loreKey)) {
            player.sendMessage(Messages.cannotFireLoreArrows());
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLaunchProjectileEvent(final PlayerLaunchProjectileEvent event) {

        Player player = event.getPlayer();

        ItemStack projectileItem = event.getItemStack();

        ItemMeta meta = projectileItem.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (pdc.has(BasicLore.loreKey)) {
            player.sendMessage(Messages.cannotFireLoreArrows());
            event.setShouldConsume(false);
            event.setCancelled(true);
        }

    }
}
