package com.temportalist.weepingangels.common.init

import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.register.BlockRegister
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.block.BlockStatue
import com.temportalist.weepingangels.common.tile.TileEntityStatue
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material

/**
 *
 *
 * @author TheTemportalist
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
