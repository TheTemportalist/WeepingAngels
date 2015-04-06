package com.temportalist.weepingangels.common.init

import java.util

import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.register.ItemRegister
import com.temportalist.origin.library.common.utility.Generic
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.item.ItemAngelTear
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 *
 *
 * @author TheTemportalist
 */
object WAItems extends ItemRegister {

	var angelTear: ItemWrapper = null
	var angelArrow: ItemWrapper = null

	override def register(): Unit = {

		this.angelTear = new ItemAngelTear()
		Origin.addItemToTab(this.angelTear)

		this.angelArrow = new ItemWrapper(WeepingAngels.MODID, "angelArrow") {
			@SideOnly(Side.CLIENT)
			override def addInformation(itemStack: ItemStack, player: EntityPlayer,
					list: util.List[_], isAdvanced: Boolean): Unit = {
				Generic.addToList(list, "Beware! This arrow")
				Generic.addToList(list, "  holds the power of")
				Generic.addToList(list, "  the silent assassin.")
			}
			@SideOnly(Side.CLIENT)
			override def registerIcons(reg: IIconRegister): Unit = {
				this.itemIcon = Items.arrow.getIconFromDamage(0)
			}
		}
		Origin.addItemToTab(this.angelArrow)

		if (OreDictionary.getOres("ghastTear").isEmpty) {
			OreDictionary.registerOre("ghastTear", Items.ghast_tear)
		}
		OreDictionary.registerOre("ghastTear", this.angelTear)

	}

	override def registerCrafting(): Unit = {
		GameRegistry.addShapelessRecipe(new ItemStack(this.angelArrow), this.angelTear, Items.arrow)
	}

}
