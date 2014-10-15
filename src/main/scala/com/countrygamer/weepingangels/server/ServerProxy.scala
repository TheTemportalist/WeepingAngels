package com.countrygamer.weepingangels.server

import com.countrygamer.weepingangels.common.CommonProxy
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Vec3
import net.minecraft.world.World

/**
 *
 *
 * @author CountryGamer
 */
class ServerProxy() extends CommonProxy() {

	override def getServerElement(ID: Int, player: EntityPlayer, world: World, coord: Vec3,
			tileEntity: TileEntity): AnyRef = {
		null
	}

}
