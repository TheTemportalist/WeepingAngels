package com.temportalist.weepingangels.common.network

import com.temportalist.origin.foundation.common.network.IPacket
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

	override def handle(player: EntityPlayer, side: Side): Unit = {
		DimensionManager.getWorld(this.get[Int]).setWorldTime(this.get[Int].toLong)
	}

}
