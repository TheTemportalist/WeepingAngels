package com.countrygamer.weepingangels.client.gui.configFactory

import com.countrygamer.cgo.wrapper.client.gui.configFactory.GuiConfigWrapper
import com.countrygamer.weepingangels.common.WeepingAngels
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.GuiScreen

/**
 *
 *
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen)
		extends GuiConfigWrapper(guiScreen, WeepingAngels.pluginID, WeepingAngels) {

}
