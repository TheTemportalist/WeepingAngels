package com.temportalist.weepingangels.common.init

import java.util

import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.register.ItemRegister
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.item.ItemAngelTear
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
import net.minecraftforge.oredict.OreDictionary

/**
 *
 *
 * @author TheTemportalist
 */
object WAItems extends ItemRegister {

	var angelTear: Item = null
	var angelArrow: Item = null

	override def register(): Unit = {

		this.angelTear = new ItemAngelTear()
		Origin.addItemToTab(this.angelTear)

		this.angelArrow = new ItemWrapper(WeepingAngels.MODID, "angelArrow") {
			@SideOnly(Side.CLIENT)
			override def addInformation(itemStack: ItemStack, player: EntityPlayer,
					list: util.List[_], isAdvanced: Boolean): Unit = {
				this.addInfo(list, "Beware! This arrow")
				this.addInfo(list, "  holds the power of")
				this.addInfo(list, "  the silent assassin.")
			}
		}
		Origin.addItemToTab(this.angelArrow)

		if (OreDictionary.getOres("ghastTear").isEmpty) {
			OreDictionary.registerOre("ghastTear", Items.ghast_tear)
		}
		OreDictionary.registerOre("ghastTear", this.angelArrow)

	}

	override def registerCrafting(): Unit = {
		GameRegistry.addShapelessRecipe(new ItemStack(this.angelArrow), this.angelTear, Items.arrow)
	}

}
