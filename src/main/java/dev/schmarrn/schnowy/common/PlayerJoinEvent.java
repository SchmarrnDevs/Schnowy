package dev.schmarrn.schnowy.common;

import dev.schmarrn.schnowy.Schnowy;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;

public class PlayerJoinEvent implements ServerPlayConnectionEvents.Join{
	@Override
	public void onPlayReady(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
		if (SchnowyEngine.getBlizzard().isActive()) {
			server.sendSystemMessage(Component.translatable("announcement.schnowy.blizzard.start"));
		}
	}
}
