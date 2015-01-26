package com.temportalist.weepingangels.client.gui.configFactory

import com.temportalist.origin.wrapper.client.gui.configFactory.GuiConfigWrapper
import com.temportalist.weepingangels.common.WeepingAngels
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiConfig(guiScreen: GuiScreen)
		extends GuiConfigWrapper(guiScreen, WeepingAngels, WeepingAngels.MODID) {

}
