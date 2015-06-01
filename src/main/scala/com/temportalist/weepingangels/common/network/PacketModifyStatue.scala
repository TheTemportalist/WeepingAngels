package com.temportalist.weepingangels.common.network

import com.temportalist.origin.foundation.common.network.PacketTile
import com.temportalist.weepingangels.common.tile.TEStatue
import cpw.mods.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity

/**
 * Used to update different states of a Statue Tile Entity
 * States: Face, Arms, Rotation
 *
 * @author TheTemportalist
 */
class PacketModifyStatue(tile: TileEntity) extends PacketTile(tile) {

	def this() {
		this(null)
	}

	/**
	 * @param tile The tile entity
	 * @param state 1 = Face, 2 = Arms, 3 = Rotation
	 * @param value Face (1 = Calm, 2 = Angry), Arms (1 = Hiding, 2 = Peaking, 3 = Confident), Rotation (in degrees)
	 */
	def this(tile: TileEntity, state: Int, value: Float) {
		this(tile)
		this.add(state)
		this.add(value)
	}

	override def handle(player: EntityPlayer, tileEntity: TileEntity, side: Side): Unit = {
		tileEntity match {
			case statue: TEStatue =>
				this.get[Int] match {
					case 1 => // Face
						statue.setFacialState(Math.floor(this.get[Float]).toInt)
					case 2 => // Arms
						statue.setArmState(Math.floor(this.get[Float]).toInt)
						println("Set arm state to " + statue.getArmState)
					case 3 => // Rotation
						statue.setRotation(this.get[Float])
					case 4 => // Corruption
						statue.setCorruption(this.get[Float].toInt)
					case _ =>
				}
			case _ =>
		}
	}

}
