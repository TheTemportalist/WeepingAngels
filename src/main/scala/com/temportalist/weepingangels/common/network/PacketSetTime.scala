package com.temportalist.weepingangels.common.network

import com.temportalist.origin.library.common.nethandler.IPacket
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

/**
 *
 *
 * @author TheTemportalist
 */
class PacketSetTime() extends IPacket {

	def this(setTime: Int) {
		this()
		this.add(setTime)
	}

	override def handle(player: EntityPlayer, isServer: Boolean): Unit = {
		for (i <- 0 until MinecraftServer.getServer.worldServers.length) {
			MinecraftServer.getServer.worldServers(i).setWorldTime(this.get[Int].toLong)
		}
	}

}
