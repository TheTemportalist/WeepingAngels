package com.temportalist.weepingangels.client

import java.util

import com.temportalist.origin.library.common.handlers.RegisterHelper
import com.temportalist.weepingangels.client.gui.configFactory.GuiConfig
import com.temportalist.weepingangels.client.gui.{GuiDynamic, GuiStatue, GuiTimeManipulation, HUDOverlay}
import com.temportalist.weepingangels.client.render.{RenderAngel, TERendererStatue}
import com.temportalist.weepingangels.common.entity.{EntityAngel, EntityAngelArrow}
import com.temportalist.weepingangels.common.tile.TEStatue
import com.temportalist.weepingangels.common.{ProxyCommon, WAOptions}
import cpw.mods.fml.client.IModGuiFactory
import cpw.mods.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import cpw.mods.fml.client.registry.{ClientRegistry, RenderingRegistry}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.entity.RenderArrow
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class ProxyClient() extends ProxyCommon with IModGuiFactory {

	override def registerRender(): Unit = {

		ClientRegistry.bindTileEntitySpecialRenderer(
			classOf[TEStatue], new TERendererStatue()
		)

		RenderingRegistry.registerEntityRenderingHandler(
			classOf[EntityAngel], new RenderAngel()
		)
		RenderingRegistry.registerEntityRenderingHandler(
			classOf[EntityAngelArrow], new RenderArrow()
		)

		RegisterHelper.registerHandler(HUDOverlay, GuiDynamic)

	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		if (ID == WAOptions.statueGui && tileEntity.isInstanceOf[TEStatue]) {
			return new GuiStatue(tileEntity.asInstanceOf[TEStatue])
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
