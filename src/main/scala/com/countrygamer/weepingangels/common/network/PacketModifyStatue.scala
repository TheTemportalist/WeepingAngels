package com.countrygamer.weepingangels.common.network

import com.countrygamer.cgo.wrapper.common.network.PacketTEWrapper
import com.countrygamer.weepingangels.common.tile.TileEntityStatue
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity

/**
 * Used to update different states of a Statue Tile Entity
 * States: Face, Arms, Rotation
 *
 * @author CountryGamer
 */
/**
 *
 * @param x X Coord of tile entity
 * @param y Y Coord of tile entity
 * @param z Z Coord of tile entity
 * @param state 1 = Face, 2 = Arms, 3 = Rotation
 * @param value Face (1 = Calm, 2 = Angry), Arms (1 = Hiding, 2 = Peaking, 3 = Confident), Rotation (in degrees)
 */
class PacketModifyStatue(x: Int, y: Int, z: Int, var state: Int, var value: Float)
		extends PacketTEWrapper(x, y, z) {

	// Default Constructor
	{
		//System.out.println("Recieved State: " + this.state)
		//System.out.println("Recieved value: " + this.value)

	}

	// End Constructor

	// Other Constructors
	def this() {
		this(0, 0, 0, 0, 0.0F)
	}

	// End Constructors

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
			case statueTE: TileEntityStatue =>

				// Switch statement
				this.state match {
					case 1 => // Face
						statueTE.setFacialState(Math.floor(this.value).asInstanceOf[Int])
					//System.out.println("Set face to " + this.value)
					//System.out.println("Face is " + statueTE.getFacialState())
					case 2 => // Arms
						statueTE.setArmState(Math.floor(this.value).asInstanceOf[Int])
					case 3 => // Rotation
						statueTE.setRotation(this.value)
					//System.out.println("Set rot to " + this.value)
					//System.out.println("Rot is " + statueTE.getRotation())
				}

			case _ =>
		}

	}

}
