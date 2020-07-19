package net.torocraft.dailies.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class QuestNumberArgument implements ArgumentType<Integer> {
    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        return reader.readInt();
    }
}
