package com.SP3x3.listeners;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.SP3x3.utils.Utils;

public class PlayerListener implements Listener {
   private static final Set<Block> PROCESSING = Collections.synchronizedSet(new HashSet<>());
   private final JavaPlugin plugin;

   public PlayerListener(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBlockBreak(BlockBreakEvent event) {
      Player player = event.getPlayer();
      ItemStack item = player.getInventory().getItemInMainHand();
      if (!event.isCancelled() && item.getType() != Material.AIR) {
         if (item.getType().toString().contains("_PICKAXE")) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
               NamespacedKey keyPico = new NamespacedKey(this.plugin, "pico3x3");

               if (meta.getPersistentDataContainer().has(keyPico, PersistentDataType.BYTE)) {
                  Block base = event.getBlock();

                  if (!PROCESSING.contains(base)) {
                     if (base.getType() == Material.BEDROCK && this.plugin.getConfig().getBoolean("pickaxe.prevent-bedrock-break", true)) {
                        event.setCancelled(true);
                        player.sendMessage(Utils.colorize(this.plugin.getConfig().getString("messages.cannot-break-bedrock", "&cEste pico no puede romper bedrock.")));
                     } else {

                        NamespacedKey keyRadius = new NamespacedKey(this.plugin, "pico_radius");
                        NamespacedKey keyDepth = new NamespacedKey(this.plugin, "pico_depth");

                        int radius = meta.getPersistentDataContainer().getOrDefault(keyRadius, PersistentDataType.INTEGER, this.plugin.getConfig().getInt("pickaxe.radius", 1));
                        byte depthByte = meta.getPersistentDataContainer().getOrDefault(keyDepth, PersistentDataType.BYTE, (byte) (this.plugin.getConfig().getBoolean("pickaxe.break-depth", true) ? 1 : 0));
                        boolean breakDepth = (depthByte == 1);

                        PROCESSING.add(base);

                        try {
                           for(int dx = -radius; dx <= radius; ++dx) {
                              for(int dy = breakDepth ? -radius : 0; dy <= (breakDepth ? radius : 0); ++dy) {
                                 for(int dz = -radius; dz <= radius; ++dz) {
                                    if (dx != 0 || dy != 0 || dz != 0) {
                                       Block target = base.getRelative(dx, dy, dz);
                                       if (target.getType() != Material.AIR && target.getType() != Material.BEDROCK) {

                                          if (PROCESSING.contains(target)) {
                                             continue;
                                          }

                                          PROCESSING.add(target);

                                          BlockBreakEvent simulatedEvent = new BlockBreakEvent(target, player);
                                          Bukkit.getPluginManager().callEvent(simulatedEvent);
                                          
                                          if (simulatedEvent.isCancelled()) {
                                             PROCESSING.remove(target);
                                             continue;
                                          }

                                          target.breakNaturally(item);
                                          PROCESSING.remove(target);
                                       }
                                    }
                                 }
                              }
                           }
                        } finally {
                           PROCESSING.remove(base);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
