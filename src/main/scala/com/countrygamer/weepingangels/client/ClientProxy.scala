package com.countrygamer.weepingangels.client

import java.util

import com.countrygamer.weepingangels.client.gui.configFactory.GuiConfig
import com.countrygamer.weepingangels.client.gui.{GuiStatue, GuiTimeManipulation, HUDOverlay}
import com.countrygamer.weepingangels.client.render.{RenderWeepingAngel, TERendererStatue}
import com.countrygamer.weepingangels.common.entity.{EntityAngelArrow, EntityWeepingAngel}
import com.countrygamer.weepingangels.common.tile.TileEntityStatue
import com.countrygamer.weepingangels.common.{CommonProxy, WAOptions}
import cpw.mods.fml.client.IModGuiFactory
import cpw.mods.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import cpw.mods.fml.client.registry.{ClientRegistry, RenderingRegistry}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.entity.RenderArrow
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Vec3
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge

/**
 *
 *
 * @author CountryGamer
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

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, coord: Vec3,
			tileEntity: TileEntity): AnyRef = {
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
