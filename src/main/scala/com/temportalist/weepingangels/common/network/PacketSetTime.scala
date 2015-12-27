package com.temportalist.weepingangels.common.network

import com.temportalist.origin.foundation.common.network.IPacket
import cpw.mods.fml.common.network.simpleimpl.{MessageContext, IMessage, IMessageHandler}
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.DimensionManager

/**
 *
 *
 * @author TheTemportalist
 */
class PacketSetTime() extends IPacket {

	def this(dim: Int, setTime: Int) {
		this()
		this.add(dim)
		this.add(setTime)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketSetTime {
	class Handler extends IMessageHandler[PacketSetTime, IMessage] {
		override def onMessage(message: PacketSetTime, ctx: MessageContext): IMessage = {
			DimensionManager.getWorld(message.get[Int]).setWorldTime(message.get[Int].toLong)
			null
		}
	}
}
