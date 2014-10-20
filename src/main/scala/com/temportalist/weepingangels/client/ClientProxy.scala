package com.temportalist.weepingangels.client

import java.util

import com.temportalist.weepingangels.client.gui.configFactory.GuiConfig
import com.temportalist.weepingangels.client.gui.{GuiStatue, GuiTimeManipulation, HUDOverlay}
import com.temportalist.weepingangels.client.render.{RenderWeepingAngel, TERendererStatue}
import com.temportalist.weepingangels.common.entity.{EntityAngelArrow, EntityWeepingAngel}
import com.temportalist.weepingangels.common.tile.TileEntityStatue
import com.temportalist.weepingangels.common.{CommonProxy, WAOptions}
import cpw.mods.fml.client.IModGuiFactory
import cpw.mods.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import cpw.mods.fml.client.registry.{ClientRegistry, RenderingRegistry}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.entity.RenderArrow
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge

/**
 *
 *
 * @author TheTemportalist
 */
class ClientProxy() extends CommonProxy with IModGuiFactory {

	override def registerRender(): Unit = {

		ClientRegistry.bindTileEntitySpecialRenderer(
			classOf[TileEntityStatue], new TERendererStatue()
		)

		RenderingRegistry.registerEntityRenderingHandler(
			classOf[EntityWeepingAngel], new RenderWeepingAngel()
		)

		RenderingRegistry.registerEntityRenderingHandler(
			classOf[EntityAngelArrow], new RenderArrow()
		)

		MinecraftForge.EVENT_BUS.register(HUDOverlay)

	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == WAOptions.statueGui && tileEntity.isInstanceOf[TileEntityStatue]) {
			return new GuiStatue(tileEntity.asInstanceOf[TileEntityStatue])
		}
		else if (ID == WAOptions.timeManipGui) {
			return new GuiTimeManipulation(player)
		}
		null
	}

	override def initialize(minecraftInstance: Minecraft): Unit = {

	}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = {
		null
	}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = {
		null
	}

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = {
		classOf[GuiConfig]
	}

}
