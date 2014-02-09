package com.countrygamer.weepingangels.proxy;

import com.countrygamer.weepingangels.WeepingAngelsMod;
import com.countrygamer.weepingangels.Handlers.Packet.ServerPacketHandler;

public class ServerProxy {

	public void registerRenderThings() {

	}

	public void preInit() {
		WeepingAngelsMod.packetChannel.register(new ServerPacketHandler());
	}
}
