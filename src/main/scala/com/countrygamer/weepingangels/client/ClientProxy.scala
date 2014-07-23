package com.countrygamer.weepingangels.client

import com.countrygamer.weepingangels.client.gui.{GuiStatue, HUDOverlay}
import com.countrygamer.weepingangels.client.render.{RenderWeepingAngel, TERendererStatue}
import com.countrygamer.weepingangels.common.{WAOptions, CommonProxy}
import com.countrygamer.weepingangels.common.entity.EntityWeepingAngel
import com.countrygamer.weepingangels.common.tile.TileEntityStatue
import cpw.mods.fml.client.registry.{RenderingRegistry, ClientRegistry}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge

/**
 *
 *
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
class ClientProxy() extends CommonProxy() {

	// Default Constructor
	{

	}

	// End Constructor

	// Other Constructors

	// End Constructors
	override def registerRender(): Unit = {

		ClientRegistry
				.bindTileEntitySpecialRenderer(classOf[TileEntityStatue], new TERendererStatue())

		RenderingRegistry.registerEntityRenderingHandler(classOf[EntityWeepingAngel],
			new RenderWeepingAngel())

		MinecraftForge.EVENT_BUS.register(HUDOverlay)

	}

	@SideOnly(Side.CLIENT)
	override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int): AnyRef = {
		val tileEntity: TileEntity = world.getTileEntity(x, y, z)

		if (ID == WAOptions.statueGui && tileEntity.isInstanceOf[TileEntityStatue]) {
			return new GuiStatue(tileEntity.asInstanceOf[TileEntityStatue])
		}

		return null
	}

}
