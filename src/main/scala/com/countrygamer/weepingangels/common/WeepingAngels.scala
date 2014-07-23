package com.countrygamer.weepingangels.common

import com.countrygamer.cgo.wrapper.common.PluginWrapper
import com.countrygamer.cgo.wrapper.common.registries.RegisterHelper
import com.countrygamer.weepingangels.common.block.WABlocks
import com.countrygamer.weepingangels.common.entity.WAEntity
import com.countrygamer.weepingangels.common.extended.{AngelPlayerHandler, AngelPlayer}
import com.countrygamer.weepingangels.common.network.PacketModifyStatue
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.{Mod, SidedProxy}

/**
 *
 *
 * @author CountryGamer
 */
@Mod(modid = WeepingAngels.pluginID, name = WeepingAngels.pluginName, version = "@PLUGIN_VERSION@",
	modLanguage = "scala",
	guiFactory = "com.countrygamer.weepingangels.client.gui.configFactory.WeepingAngelsFactory")
object WeepingAngels extends PluginWrapper {

	final val pluginID = "weepingangels"
	final val pluginName = "Weeping Angels"

	@SidedProxy(clientSide = "com.countrygamer.weepingangels.client.ClientProxy",
		serverSide = "com.countrygamer.weepingangels.common.CommonProxy")
	var proxy: CommonProxy = null

	@Mod.EventHandler
	def preInit(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this.pluginID, this.pluginName, event, this.proxy, WAOptions, WABlocks,
			WAEntity)

		RegisterHelper.registerExtendedPlayer("Extended Angel Player", classOf[AngelPlayer], false)

		RegisterHelper.registerHandler(AngelPlayerHandler, null)

		RegisterHelper.registerPacketHandler(this.pluginID, classOf[PacketModifyStatue])

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event)

	}

	@Mod.EventHandler
	def postInit(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)

	}

}
