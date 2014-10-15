package com.countrygamer.weepingangels.common.network

import com.countrygamer.cgo.library.common.nethandler.IPacket
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

/**
 *
 *
 * @author CountryGamer
 */
class PacketSetTime(var setTime: Int) extends IPacket {

	def this() {
		this(0)
	}

	override def writeTo(buffer: ByteBuf): Unit = {
		buffer.writeInt(this.setTime)
	}

	override def readFrom(buffer: ByteBuf): Unit = {
		this.setTime = buffer.readInt()
	}

	override def handle(player: EntityPlayer): Unit = {
		for (i <- 0 until MinecraftServer.getServer.worldServers.length) {
			MinecraftServer.getServer.worldServers(i).setWorldTime(this.setTime.asInstanceOf[Long])
		}
	}

}
