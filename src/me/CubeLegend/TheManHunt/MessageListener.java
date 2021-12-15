package me.CubeLegend.TheManHunt;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        System.out.println(channel);
        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String subChannel = in.readUTF();
        System.out.println(subChannel);
        if (subChannel.equals(Objects.requireNonNull(TheManHunt.getInstance().getConfig().getString("PluginMessagingChannelOfMiniGame")))) {

            short len = in.readShort();
            byte[] msgBytes = new byte[len];
            in.readFully(msgBytes);
            DataInputStream msgIn = new DataInputStream(new ByteArrayInputStream(msgBytes));
            try {
                String dataDescription = msgIn.readUTF();
                Bukkit.getLogger().info(dataDescription);
                if (dataDescription.equalsIgnoreCase("teamInformation")) {

                    String teamName = msgIn.readUTF();
                    Bukkit.getLogger().info(teamName);
                    String teamIcon = msgIn.readUTF();
                    Bukkit.getLogger().info(teamIcon);
                    int teamSelectionSlot = msgIn.readInt();
                    Bukkit.getLogger().info(String.valueOf(teamSelectionSlot));
                    String teamColor = msgIn.readUTF();
                    Bukkit.getLogger().info(teamColor);
                    int membersLength = msgIn.readInt();
                    Bukkit.getLogger().info(String.valueOf(membersLength));

                    TeamHandler.getInstance().createTeam(teamName, teamIcon, teamSelectionSlot, teamColor);
                    int teamNumber = TeamHandler.getInstance().getTotalTeamNumber() - 1;
                    for (int i = 0; i < membersLength; i++) {
                        System.out.println(teamNumber);
                        TeamHandler.getInstance().getTeam(teamNumber).addMember(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(msgIn.readUTF()))));
                    }
                    GameHandler.getInstance().checkAllTeamsSetup();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
