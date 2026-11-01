package io.github.pmeiggs.firstPlugin.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class HealCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            return;
        }
        player.setHealth(20.0);
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.sendMessage(Component.text("You've been healed.")
                .color(NamedTextColor.GREEN));
    }
}
