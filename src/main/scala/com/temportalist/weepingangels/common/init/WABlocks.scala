package com.temportalist.weepingangels.common.init

import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.register.BlockRegister
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.block.BlockStatue
import com.temportalist.weepingangels.common.tile.TEStatue
import net.minecraft.block.material.Material
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 *
 *
 * @author TheTemportalist
 */
object WABlocks extends BlockRegister {

	var statue: BlockStatue = null

	override def registerTileEntities: Unit = {
		GameRegistry.registerTileEntity(classOf[TEStatue], WeepingAngels.MODID + "_Statue")
	}

	override def register(): Unit = {
		WABlocks.statue = new BlockStatue(Material.rock, "plinth", classOf[TEStatue])
		Origin.addBlockToTab(WABlocks.statue)
	}

	override def registerCrafting: Unit = {

	}

	override def registerSmelting: Unit = {

	}

	override def registerOther: Unit = {

	}

}
