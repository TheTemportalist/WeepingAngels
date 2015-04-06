package com.temportalist.weepingangels.common.network

import com.temportalist.origin.library.common.nethandler.IPacket
import com.temportalist.weepingangels.common.WeepingAngels
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.DimensionManager

/**
 *
 *
 * @author TheTemportalist
 */
class PacketSetTime() extends IPacket {

	override def getChannel(): String = WeepingAngels.MODID

	def this(dim: Int, setTime: Int) {
		this()
		this.add(dim)
		this.add(setTime)
	}

	override def handle(player: EntityPlayer, isServer: Boolean): Unit = {
		DimensionManager.getWorld(this.get[Int]).setWorldTime(this.get[Int].toLong)
	}

}
