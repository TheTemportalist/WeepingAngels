package com.temportalist.weepingangels.common.network

import com.temportalist.origin.foundation.common.network.IPacket
import com.temportalist.weepingangels.common.tile.TEStatue
import cpw.mods.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}
import cpw.mods.fml.relauncher.Side
import net.minecraft.tileentity.TileEntity

/**
 * Used to update different states of a Statue Tile Entity
 * States: Face, Arms, Rotation
 *
 * @author TheTemportalist
 */
class PacketModifyStatue extends IPacket {

	/**
	 * @param tile The tile entity
	 * @param state 1 = Face, 2 = Arms, 3 = Rotation
	 * @param value Face (1 = Calm, 2 = Angry), Arms (1 = Hiding, 2 = Peaking, 3 = Confident), Rotation (in degrees)
	 */
	def this(tile: TileEntity, state: Int, value: Float) {
		this()
		this.add(tile)
		this.add(state)
		this.add(value)
	}

	override def getReceivableSide: Side = Side.SERVER

}
object PacketModifyStatue {
	class Handler extends IMessageHandler[PacketModifyStatue, IMessage] {
		override def onMessage(message: PacketModifyStatue, ctx: MessageContext): IMessage = {
			message.getTile(ctx.getServerHandler.playerEntity.worldObj) match {
				case statue: TEStatue =>
					message.get[Int] match {
						case 1 => // Face
							statue.setFacialState(Math.floor(message.get[Float]).toInt)
						case 2 => // Arms
							statue.setArmState(Math.floor(message.get[Float]).toInt)
							println("Set arm state to " + statue.getArmState)
						case 3 => // Rotation
							statue.setRotation(message.get[Float])
						case 4 => // Corruption
							statue.setCorruption(message.get[Float].toInt)
						case _ =>
					}
				case _ =>
			}
			null
		}
	}
}
