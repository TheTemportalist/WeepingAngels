package com.countrygamer.weepingangels.common.init

import com.countrygamer.cgo.library.common.Origin
import com.countrygamer.cgo.library.common.register.BlockRegister
import com.countrygamer.weepingangels.common.WeepingAngels
import com.countrygamer.weepingangels.common.block.BlockStatue
import com.countrygamer.weepingangels.common.tile.TileEntityStatue
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material

/**
 *
 *
 * @author CountryGamer
 */
object WABlocks extends BlockRegister {

	var statue: Block = null

	override def registerTileEntities: Unit = {

		GameRegistry
				.registerTileEntity(classOf[TileEntityStatue], WeepingAngels.pluginID + "_Statue")

	}

	override def register(): Unit = {

		WABlocks.statue = new BlockStatue(Material.rock, WeepingAngels.pluginID, "Plinth",
			classOf[TileEntityStatue])
		Origin.addBlockToTab(WABlocks.statue)

	}

	override def registerCrafting: Unit = {

	}

	override def registerSmelting: Unit = {

	}

	override def registerOther: Unit = {

	}

}
