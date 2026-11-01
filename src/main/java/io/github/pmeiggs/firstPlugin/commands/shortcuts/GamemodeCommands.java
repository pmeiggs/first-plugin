package io.github.pmeiggs.firstPlugin.commands.shortcuts;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommands {
    public static LiteralCommandNode<CommandSourceStack> create(){
        return Commands.literal("gm")
                .then(Commands.literal("s").
                        executes(context -> gamemodeChange(context.getSource(), GameMode.SURVIVAL)))
                .then(Commands.literal("c").
                        executes(context -> gamemodeChange(context.getSource(), GameMode.CREATIVE)))
                .then(Commands.literal("p").
                        executes(context -> gamemodeChange(context.getSource(), GameMode.SPECTATOR)))
                .then(Commands.literal("a").
                        executes(context -> gamemodeChange(context.getSource(), GameMode.ADVENTURE)))
                .build();
    }

    private static int gamemodeChange(CommandSourceStack source,GameMode gameMode) {
        if(!(source.getExecutor() instanceof Player player)){
            return 0;
        }

        player.setGameMode(gameMode);
        return Command.SINGLE_SUCCESS;
    }

}
