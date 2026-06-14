package com.prueba.listeners;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
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

import com.prueba.utils.Utils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class PlayerListener implements Listener {
   private static final Set<Block> PROCESSING = Collections.synchronizedSet(new HashSet<>());
   private final JavaPlugin plugin;
   private boolean worldGuardEnabled = false;
   private RegionQuery query = null;

   public PlayerListener(JavaPlugin plugin) {
      this.plugin = plugin;
      if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null && plugin.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
         this.worldGuardEnabled = true;
         this.query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
      }

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
            if (meta != null && meta.getPersistentDataContainer().has(new NamespacedKey(this.plugin, "pico3x3"), PersistentDataType.BYTE)) {
               Block base = event.getBlock();
               if (!PROCESSING.contains(base)) {
                  if (base.getType() == Material.BEDROCK && this.plugin.getConfig().getBoolean("pickaxe.prevent-bedrock-break", true)) {
                     event.setCancelled(true);
                     player.sendMessage(Utils.colorize(this.plugin.getConfig().getString("messages.cannot-break-bedrock", "&cEste pico no puede romper bedrock.")));
                  } else {
                     LocalPlayer localPlayer = null;
                     if (this.worldGuardEnabled) {
                        localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
                     }

                     int radius = this.plugin.getConfig().getInt("pickaxe.radius", 1);
                     boolean breakDepth = this.plugin.getConfig().getBoolean("pickaxe.break-depth", true);
                     PROCESSING.add(base);

                     try {
                        for(int dx = -radius; dx <= radius; ++dx) {
                           for(int dy = breakDepth ? -radius : 0; dy <= (breakDepth ? radius : 0); ++dy) {
                              for(int dz = -radius; dz <= radius; ++dz) {
                                 if (dx != 0 || dy != 0 || dz != 0) {
                                    Block target = base.getRelative(dx, dy, dz);
                                    if (target.getType() != Material.AIR && target.getType() != Material.BEDROCK) {
                                       if (this.worldGuardEnabled) {
                                          Location bukkitLoc = target.getLocation();
                                          World weWorld = BukkitAdapter.adapt(bukkitLoc.getWorld());
                                          com.sk89q.worldedit.util.Location weLoc = new com.sk89q.worldedit.util.Location(weWorld, bukkitLoc.getX(), bukkitLoc.getY(), bukkitLoc.getZ());
                                          Boolean canBreak = this.query.testState(weLoc, localPlayer, new StateFlag[]{Flags.BLOCK_BREAK});
                                          if (Boolean.FALSE.equals(canBreak)) {
                                             continue;
                                          }
                                       }

                                       PROCESSING.add(target);
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
