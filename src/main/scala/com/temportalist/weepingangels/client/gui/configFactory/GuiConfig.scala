package com.temportalist.weepingangels.client.gui.configFactory

import com.temportalist.origin.foundation.client.gui.GuiConfigBase
import com.temportalist.weepingangels.common.WeepingAngels
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.GuiScreen

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen)
		extends GuiConfigBase(guiScreen, WeepingAngels, WeepingAngels.MODID) {

}
