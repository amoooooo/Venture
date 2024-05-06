package com.pokeskies.$mod_id$.commands.subcommands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.$mod_id$.$mod_name$
import com.pokeskies.$mod_id$.utils.SubCommand
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

class ReloadCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("reload")
            .requires(Permissions.require("${$mod_name$.MOD_ID}.command.reload", 2))
            .executes(Companion::reload)
            .build()
    }

    companion object {
        fun reload(ctx: CommandContext<CommandSourceStack>): Int {
            $mod_name$.INSTANCE.reload()
            ctx.source.sendMessage(Component.text("Reloaded ${$mod_name$.MOD_NAME}!").color(NamedTextColor.GREEN))
            return 1
        }
    }
}