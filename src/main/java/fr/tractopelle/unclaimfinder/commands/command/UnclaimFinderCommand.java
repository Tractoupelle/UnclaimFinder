package fr.tractopelle.unclaimfinder.commands.command;

import fr.tractopelle.unclaimfinder.CorePlugin;
import fr.tractopelle.unclaimfinder.commands.UCommand;
import fr.tractopelle.unclaimfinder.item.ItemBuilder;
import fr.tractopelle.unclaimfinder.item.RItemUnsafe;
import fr.tractopelle.unclaimfinder.item.RNBItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class UnclaimFinderCommand extends UCommand {

    private final CorePlugin corePlugin;

    public UnclaimFinderCommand(CorePlugin corePlugin) {
        super(corePlugin, "unclaimfinder", true, "UNCLAIMFINDER.ADMIN");
        this.corePlugin = corePlugin;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {

        String prefix = corePlugin.getConfiguration().getString("PREFIX");

        if (args.length != 2) {

            corePlugin.getConfiguration().getStringList("USAGE-ADMIN").forEach(commandSender::sendMessage);

        } else {

            if(!args[0].equalsIgnoreCase("give")){
                corePlugin.getConfiguration().getStringList("USAGE-ADMIN").forEach(commandSender::sendMessage);
                return false;
            }

            Player target = Bukkit.getPlayerExact(args[1]);

            if (target == null) {

                commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("UNKNOW-PLAYER"));
                return false;

            } else {

                target.sendMessage(prefix + corePlugin.getConfiguration().getString("RECIEVE"));
                commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("GIVE").replace("%player%", target.getName()));

                int durability = corePlugin.getConfiguration().getInt("ITEM.DURABILITY");

                List<String> lore = corePlugin.getConfiguration().getStringList("ITEM.LORE");

                lore.replaceAll(s -> s.replace("%durability%", String.valueOf(durability)));
                lore.replaceAll(s -> s.replace("%maxdurability%", String.valueOf(durability)));

                ItemBuilder itemBuilder = new ItemBuilder(Material.getMaterial(corePlugin.getConfiguration().getString("ITEM.MATERIAL")))
                        .setName(corePlugin.getConfiguration().getString("ITEM.NAME"))
                        .setListLore(lore)
                        .addGlow(corePlugin.getConfiguration().getBoolean("ITEM.GLOW"));

                RItemUnsafe rItemUnsafe = new RItemUnsafe(itemBuilder);
                rItemUnsafe.setString("identifier", "unclaimfinder");
                rItemUnsafe.setInt("durability", durability);
                rItemUnsafe.setInt("maxdurability", durability);

                if (target.getInventory().firstEmpty() == -1) {

                        target.getLocation().getWorld().dropItemNaturally(target.getLocation(), rItemUnsafe.toItemBuilder().toItemStack());

                } else {

                        target.getInventory().addItem(rItemUnsafe.toItemBuilder().toItemStack());

                }

            }

        }

        return false;
    }
}
