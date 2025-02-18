package ca.tristan.easycommands.commands.music;

import ca.tristan.easycommands.EasyCommands;
import ca.tristan.easycommands.commands.slash.SlashExecutor;
import ca.tristan.easycommands.commands.EventData;
import ca.tristan.easycommands.database.MySQL;
import ca.tristan.easycommands.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PlayCmd extends SlashExecutor {

    public final EasyCommands easyCommands;

    public PlayCmd(EasyCommands easyCommands) {
        this.easyCommands = easyCommands;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Makes the bot join your channel and start playing the selected music.";
    }

    @Override
    public List<OptionData> getOptions() {
        options.add(new OptionData(OptionType.STRING, "link", "The link or name to your music."));
        return options;
    }

    @Override
    public List<Channel> getAuthorizedChannels(JDA jda) {
        List<Channel> channels = new ArrayList<>();
        if(easyCommands.getGuildsMusicChannel().isEmpty()) {
            return channels;
        }
        easyCommands.getGuildsMusicChannel().forEach((guild, channel) -> {
            channels.add(channel);
        });
        return channels;
    }

    @Override
    public void execute(EventData data, MySQL mySQL) {
        if(data.getCommand().getOptions().isEmpty()) {
            data.getEvent().reply("You need to specify a music. Usage: '/play <music>'").setEphemeral(true).queue();
            return;
        }

        if(!data.getMemberVoiceState().inAudioChannel()) {
            data.getEvent().reply("You must be in an audio channel to perform that command.").setEphemeral(true).queue();
            return;
        }

        data.deferReply();

        if(!data.getSelfVoiceState().inAudioChannel()){
            final AudioManager audioManager = data.getGuild().getAudioManager();
            final VoiceChannel memberChannel = (VoiceChannel) data.getMemberVoiceState().getChannel();

            audioManager.openAudioConnection(memberChannel);
        }

        String link = data.getCommand().getOptions().get(0).getAsString();

        if(!isUrl(link)) {
            link = "ytsearch:" + String.join(" ", data.getCommand().getOptions().get(0).getAsString() + " music");
        }
        PlayerManager.getInstance().loadAndPlay(data.getTextChannel(), data, link);
    }

    private boolean isUrl(String input) {
        try {
            new URI(input);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
