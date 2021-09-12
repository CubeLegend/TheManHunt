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
        /*if (!channel.equals(Objects.requireNonNull(TheManHunt.getInstance().getConfig().getString("PluginMessagingChannelOfMiniGame")))) {
            return;
        }*/
        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String subChannel = in.readUTF();
        System.out.println(subChannel);
        if (subChannel.equals(Objects.requireNonNull(TheManHunt.getInstance().getConfig().getString("PluginMessagingChannelOfMiniGame")))) {

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            try {
                String dataDescription = msgin.readUTF();
                System.out.println(dataDescription);
                if (dataDescription.equalsIgnoreCase("teamInformation")) {
                    System.out.println("Test 2");

                    String teamName = msgin.readUTF();
                    String teamIcon = msgin.readUTF();
                    int teamSelectionSlot = msgin.readInt();
                    String teamColor = msgin.readUTF();
                    int membersLength = msgin.readInt();

                    System.out.println("Test 3");
                    TeamHandler.getInstance().createTeam(teamName, teamIcon, teamSelectionSlot, teamColor);
                    int teamNumber = TeamHandler.getInstance().getTotalTeamNumber() - 1;
                    for (int i = 0; i < membersLength; i++) {
                        System.out.println(teamNumber);
                        TeamHandler.getInstance().getTeam(teamNumber).addMember(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(msgin.readUTF()))));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
