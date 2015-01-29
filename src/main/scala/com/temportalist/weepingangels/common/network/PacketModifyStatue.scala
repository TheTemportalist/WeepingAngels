package com.temportalist.weepingangels.common.network

import com.temportalist.origin.wrapper.common.network.PacketTEWrapper
import com.temportalist.weepingangels.common.tile.TEStatue
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos

/**
 * Used to update different states of a Statue Tile Entity
 * States: Face, Arms, Rotation
 *
 * @param pos The statue position
 * @param state 1 = Face, 2 = Arms, 3 = Rotation
 * @param value Face (1 = Calm, 2 = Angry), Arms (1 = Hiding, 2 = Peaking, 3 = Confident), Rotation (in degrees)
 * @author TheTemportalist
 */
class PacketModifyStatue(pos: BlockPos, var state: Int, var value: Float)
		extends PacketTEWrapper(pos) {

	def this() {
		this(null, 0, 0.0F)
	}

	override def writeTo(buffer: ByteBuf): Unit = {
		super.writeTo(buffer)

		buffer.writeInt(this.state)
		buffer.writeFloat(this.value)

	}

	override def readFrom(buffer: ByteBuf): Unit = {
		super.readFrom(buffer)

		this.state = buffer.readInt()
		this.value = buffer.readFloat()

	}

	override def handleSync(player: EntityPlayer, tileEntity: TileEntity): Unit = {
		tileEntity match {
			case statueTE: TEStatue =>

				// Switch statement
				this.state match {
					case 1 => // Face
						statueTE.setFacialState(Math.floor(this.value).asInstanceOf[Int])
					case 2 => // Arms
						statueTE.setArmState(Math.floor(this.value).asInstanceOf[Int])
					case 3 => // Rotation
						statueTE.setRotation(this.value)
					case 4 =>
						statueTE.setCorruption(this.value.toInt)
					case _ =>
				}

			case _ =>
		}

	}

}
