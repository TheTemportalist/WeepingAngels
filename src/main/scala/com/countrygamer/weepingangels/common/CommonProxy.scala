package com.countrygamer.weepingangels.common

import com.countrygamer.cgo.wrapper.common.ProxyWrapper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

/**
 *
 *
 * @author CountryGamer
 */
class CommonProxy() extends ProxyWrapper {

	// Default Constructor
	{

	}

	// End Constructor

	// Other Constructors

	// End Constructors

	override def registerRender(): Unit = {}

	override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int): AnyRef = {
		return null
	}

	override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int): AnyRef = {

		return null
	}

}
