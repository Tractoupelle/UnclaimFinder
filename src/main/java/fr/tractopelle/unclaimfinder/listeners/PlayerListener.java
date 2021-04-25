package fr.tractopelle.unclaimfinder.listeners;

import fr.tractopelle.unclaimfinder.CorePlugin;
import fr.tractopelle.unclaimfinder.item.RNBItem;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerListener implements Listener {

    private final CorePlugin corePlugin;

    public PlayerListener(CorePlugin corePlugin) {
        this.corePlugin = corePlugin;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (!(itemStack.hasItemMeta()) && !itemStack.getItemMeta().hasLore()) return;

        Player player = event.getPlayer();
        RNBItem rnbItem = new RNBItem(itemStack);

        if (rnbItem.getString("identifier").equalsIgnoreCase("unclaimfinder")) {

            int durability = rnbItem.getInt("durability") - 1;

            if (durability <= 0) {

                player.setItemInHand(null);
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 10L, 10L);

            } else {

                rnbItem.setInt("durability", durability);

                ItemMeta itemMeta = rnbItem.build().getItemMeta();

                itemMeta.getLore().clear();

                List<String> lore = corePlugin.getConfiguration().getStringList("ITEM.LORE");

                lore.replaceAll(s -> s.replace("%durability%", String.valueOf(durability)));
                lore.replaceAll(s -> s.replace("%maxdurability%", String.valueOf(corePlugin.getConfiguration().getInt("ITEM.DURABILITY"))));

                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                player.updateInventory();

                int percentage = this.getPercentage(this.getChunkAroundPlayerWithRadius(player, corePlugin.getConfiguration().getInt("RANGE-IN-CHUNK")));

                for (String string : corePlugin.getConfiguration().getStringList("DISPLAY.TYPE")) {

                    if (string.equalsIgnoreCase("CHAT")) {

                        player.sendMessage(corePlugin.getConfiguration().getString("DISPLAY.MESSAGE").replace("%percentage%", String.valueOf(percentage)));

                    } else if (string.equalsIgnoreCase("ACTIONBAR")) {

                        this.sendActionBar(player, corePlugin.getConfiguration().getString("DISPLAY.MESSAGE").replace("%percentage%", String.valueOf(percentage)));

                    }
                }
            }
        }
    }

    public int getPercentage(Collection<Chunk> chunks) {

        int percentage = 0;

        for (Chunk chunk : chunks) {

            for (BlockState tileEntity : chunk.getTileEntities()) {

                switch (tileEntity.getType()) {

                    case CHEST:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.CHEST");
                        break;
                    case WORKBENCH:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.WORKBENCH");
                        break;
                    case FURNACE:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.FURNACE");
                        break;
                    case ENDER_CHEST:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.ENDER_CHEST");
                        break;
                    case TRAPPED_CHEST:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.TRAPPED_CHEST");
                        break;
                    case ANVIL:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.ANVIL");
                        break;
                    case DROPPER:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.DROPPER");
                        break;
                    case DISPENSER:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.DISPENSER");
                        break;
                    case HOPPER:
                        percentage += corePlugin.getConfiguration().getInt("PERCENTAGE.HOPPER");
                        break;

                }
            }
        }

        return (Math.min(percentage, 100));

    }

    public void sendActionBar(Player player, String message) {

        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        craftPlayer.getHandle().playerConnection.sendPacket(ppoc);

    }

    public Collection<Chunk> getChunkAroundPlayerWithRadius(Player player, int radius) {
        World world = player.getWorld();

        int length = (radius * 2) + 1;
        Set<Chunk> chunks = new HashSet<>(length * length);

        int cX = player.getLocation().getChunk().getX();
        int cZ = player.getLocation().getChunk().getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(world.getChunkAt(cX + x, cZ + z));
            }
        }
        return chunks;
    }
}
