package ca.tristan.easycommands.commands;

import ca.tristan.easycommands.utils.LogType;
import ca.tristan.easycommands.utils.Logger;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class HelpCmd extends CommandExecutor {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "/help to get help with the commands.";
    }

    @Override
    public boolean isOwnerOnly() {
        return false;
    }

    @Override
    public void execute(EventData data) {
        data.deferReply().queue();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Help - " + data.getGuild().getName());
        builder.setColor(Color.GREEN);
        EasyCommands.executors.forEach(commandExecutor -> {
            if(!commandExecutor.isOwnerOnly() && !commandExecutor.getName().equals("help") && (commandExecutor.getDescription() != null || !commandExecutor.getDescription().isEmpty())) {
                builder.addField("/" + commandExecutor.getName(), commandExecutor.getDescription(), false);
            }
        });
        if(builder.getFields().isEmpty()) {
            builder.addField("There's no command to show for this server.", "", false);
        }else {
            builder.setDescription("Here's a list of command you might be able to use on this server.");
        }
        builder.setFooter("This help message was generated by EasyCommands.", data.getGuild().getIconUrl());
        data.getHook().sendMessageEmbeds(builder.build()).queue();
    }

}
