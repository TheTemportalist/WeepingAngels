package com.countrygamer.weepingangels.client.gui

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.client.gui.GuiButton

/**
 *
 *
 * @author CountryGamer
 */
@SideOnly(Side.CLIENT)
class GuiButtonIterator(id: Int, x: Int, y: Int, w: Int, h: Int, val list: Array[String])
		extends GuiButton(id, x, y, w, h, "") {

	private var displayIndex: Int = 0

	// Default Constructor
	{
		this.setText(this.displayIndex)

	}

	// End Constructor

	// Other Constructors
	def this(id: Int, x: Int, y: Int, list: Array[String]) {
		this(id, x, y, 100, 20, list)

	}

	// End Constructors

	def setText(listIndex: Int): Unit = {
		if (listIndex < this.list.length) {
			this.displayString = this.list(listIndex)
		}
	}

	def onPressed(mouseButton: Int): Unit = {
		if (mouseButton == 0) {
			this.displayIndex += 1
			if (this.displayIndex >= this.list.length) {
				this.displayIndex = 0
			}
			this.setText(this.displayIndex)

		}
		else if (mouseButton == 1) {
			this.displayIndex -= 1
			if (this.displayIndex < 0) {
				this.displayIndex = this.list.length - 1
			}
			this.setText(this.displayIndex)

		}

	}

	def updateIndexAndText(newIndex: Int): Unit = {
		this.displayIndex = newIndex
		this.setText(this.displayIndex)
	}

	def getIndex: Int = {
		this.displayIndex
	}

}
