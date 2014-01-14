package WeepingAngels.Client.Gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import CountryGamer_Core.Client.Gui.GuiButtonArrow;
import WeepingAngels.WeepingAngelsMod;
import WeepingAngels.lib.Reference;
import WeepingAngels.lib.Util;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVortex extends GuiScreen {

	public static final ResourceLocation bkgd = new ResourceLocation(
			Reference.MOD_ID_LOWERCASE, "textures/gui/"
					+ WeepingAngelsMod.vortexManName + ".png");
	private int leftOfGui, topOfGui;
	protected int xSize = 176;
	protected int ySize = 166;

	private final EntityPlayer thePlayer;
	private final ItemStack vortexStack;

	private GuiButton teleport, saveLocation, dimension, select, selectCurrent;
	private GuiTextField indexTextBox, nameTextBox, dimIDText, newXTextBox,
			newYTextBox, newZTextBox;

	private int locationIndex = 0;
	private String indexName = "", dimName = "Overworld";
	private double coordX = 0.0D, coordY = 0.0D, coordZ = 0.0D;

	private GuiButtonArrow up, down;

	public GuiVortex(EntityPlayer player) {
		super();
		this.vortexStack = player.getHeldItem();
		this.thePlayer = player;

	}

	public void initGui() {
		super.initGui();
		this.leftOfGui = (this.width / 2) - (this.xSize / 2);
		this.topOfGui = (this.height / 2) - (this.ySize / 2);
		int xTextFields = this.leftOfGui + (int) (0.15 * this.xSize);
		int yTextFields = (int) (0.64 * this.ySize);
		int actionButtonsY = (this.height / 2) + (int) (0.2 * this.ySize);
		int middleOfLeft = (this.width / 2) + (this.xSize / 4);
		int middleOfRight = (this.width / 2) - (this.xSize / 4);
		int buttonID = 0;

		// Buttons: id, x, y, width, height, text
		this.buttonList.clear();
		// Dimension
		this.buttonList.add(this.dimension = new GuiButton(++buttonID,
				this.leftOfGui + (int) (0.1 * this.xSize), this.topOfGui
						+ (int) (0.285 * this.ySize), 75, 20, "Overworld"));
		// Arrows
		int arrowX = (this.width / 2) + (this.xSize / 10);
		// Up
		this.buttonList.add(this.up = new GuiButtonArrow(++buttonID, arrowX,
				this.topOfGui + (int) (0.1 * this.ySize),
				GuiButtonArrow.ButtonType.UP));
		// Down
		this.buttonList.add(this.down = new GuiButtonArrow(++buttonID, arrowX,
				this.topOfGui + (int) (0.2 * this.ySize),
				GuiButtonArrow.ButtonType.DOWN));

		// Select from displayed index
		this.buttonList.add(this.select = new GuiButton(++buttonID, arrowX,
				this.topOfGui + (int) (0.5 * this.ySize), 50, 20, "Select"));

		// Select from current player location
		int width = 160;
		this.buttonList.add(this.selectCurrent = new GuiButton(++buttonID,
				middleOfLeft - (width / 4), actionButtonsY, width / 2, 20,
				"Current XYZ"));
		// Save Typed index
		this.buttonList.add(this.saveLocation = new GuiButton(++buttonID,
				middleOfRight - (width / 4), actionButtonsY, width / 2, 20,
				"Save Location"));
		// Teleport
		this.buttonList.add(this.teleport = new GuiButton(++buttonID,
				(this.width / 2) - (width / 2), actionButtonsY + 25, width, 20,
				"Teleport"));

		// Text Fields
		Keyboard.enableRepeatEvents(true);
		this.indexTextBox = new GuiTextField(this.fontRenderer, this.leftOfGui
				+ (int) (0.25 * this.xSize), this.topOfGui
				+ (int) (0.12 * this.ySize), 30, 12);
		this.setupTextField(this.indexTextBox);

		this.nameTextBox = new GuiTextField(this.fontRenderer, this.leftOfGui
				+ (int) (0.25 * this.xSize), this.topOfGui
				+ (int) (0.21 * this.ySize), 30, 12);
		this.setupTextField(this.nameTextBox);

		width = 70;
		this.newXTextBox = new GuiTextField(this.fontRenderer, xTextFields,
				yTextFields + 0, width, 12);
		this.setupTextField(this.newXTextBox);

		this.newYTextBox = new GuiTextField(this.fontRenderer, xTextFields,
				yTextFields + 12, width, 12);
		this.setupTextField(this.newYTextBox);

		this.newZTextBox = new GuiTextField(this.fontRenderer, xTextFields,
				yTextFields + 24, width, 12);
		this.setupTextField(this.newZTextBox);

	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton guiB) {
		if (guiB.id == this.dimension.id) {
			String currentDimName = this.dimension.displayString;
			ArrayList<String> dimNames = new ArrayList<String>();
			for (String key : CountryGamer_Core.CG_Core.dimensions
					.keySet()) {
				dimNames.add(key);

			}
			String nextDimName = this.getNext(dimNames, currentDimName);
			WeepingAngelsMod.log.info(currentDimName + ":" + nextDimName);
			this.dimension.displayString = nextDimName;
			this.updateScreen();
		}
		if (guiB.id == this.saveLocation.id) {
			double[] coords = this.getCoords();
			if (coords != null) {
				int index;
				try {
					index = Integer.parseInt(this.indexTextBox.getText());
				} catch (NumberFormatException err) {
					this.mc.displayGuiScreen(null);
					this.thePlayer.addChatMessage("Your index is wrong.");
					this.thePlayer.addChatMessage("Please type a valid index.");
					return;
				}
				this.storeCoords(index, this.nameTextBox.getText(),
						CountryGamer_Core.CG_Core.dimensions
								.get(this.dimension.displayString), coords[0],
						coords[1], coords[2]);
			}
		}
		if (guiB.id == this.up.id) { // previous
			// WeepingAngelsMod.log.info("UP");
			this.loadNextIndex(this.locationIndex, -1);
			this.updateScreen();
		}
		if (guiB.id == this.down.id) { // next
			// WeepingAngelsMod.log.info("Down");
			this.loadNextIndex(this.locationIndex, 1);
			this.updateScreen();
		}
		if (guiB.id == this.select.id) {
			this.indexTextBox.setText(this.locationIndex + "");
			this.nameTextBox.setText(this.indexName);
			this.dimension.displayString = this.dimName;
			this.newXTextBox.setText(this.coordX + "");
			this.newYTextBox.setText(this.coordY + "");
			this.newZTextBox.setText(this.coordZ + "");
		}
		if (guiB.id == this.selectCurrent.id) {
			this.dimension.displayString = CountryGamer_Core.CG_Core.dimensions1
					.get(this.thePlayer.dimension);
			this.newXTextBox.setText(this.round(this.thePlayer.posX, 2) + "");
			this.newYTextBox.setText(this.round(this.thePlayer.posY, 2) + "");
			this.newZTextBox.setText(this.round(this.thePlayer.posZ, 2) + "");
		}
		if (guiB.id == this.teleport.id) {
			double[] coords = this.getCoords();
			if (coords != null) {
				int dimID = CountryGamer_Core.CG_Core.dimensions
						.get(this.dimension.displayString);
				this.mc.displayGuiScreen(null);
				EntityClientPlayerMP clientPlayer = (EntityClientPlayerMP) this.thePlayer;
				clientPlayer.sendQueue.addToSendQueue(Util.buildTeleportPacket(
						"CGC_Teleport", dimID, coords));
			} else if (WeepingAngelsMod.DEBUG)
				WeepingAngelsMod.log.info("coords are null");
		}

	}

	protected void keyTyped(char letter, int par2) {
		if (this.indexTextBox.textboxKeyTyped(letter, par2)) {
			this.mc.thePlayer.sendQueue
					.addToSendQueue(new Packet250CustomPayload("MC|ItemName",
							this.indexTextBox.getText().getBytes()));
		} else if (this.nameTextBox.textboxKeyTyped(letter, par2)) {
			this.mc.thePlayer.sendQueue
					.addToSendQueue(new Packet250CustomPayload("MC|ItemName",
							this.nameTextBox.getText().getBytes()));
		} else if (this.newXTextBox.textboxKeyTyped(letter, par2)) {
			this.mc.thePlayer.sendQueue
					.addToSendQueue(new Packet250CustomPayload("MC|ItemName",
							this.newXTextBox.getText().getBytes()));
		} else if (this.newYTextBox.textboxKeyTyped(letter, par2)) {
			this.mc.thePlayer.sendQueue
					.addToSendQueue(new Packet250CustomPayload("MC|ItemName",
							this.newYTextBox.getText().getBytes()));
		} else if (this.newZTextBox.textboxKeyTyped(letter, par2)) {
			this.mc.thePlayer.sendQueue
					.addToSendQueue(new Packet250CustomPayload("MC|ItemName",
							this.newZTextBox.getText().getBytes()));
		} else
			super.keyTyped(letter, par2);
	}

	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		this.indexTextBox.mouseClicked(par1, par2, par3);
		this.nameTextBox.mouseClicked(par1, par2, par3);
		this.newXTextBox.mouseClicked(par1, par2, par3);
		this.newYTextBox.mouseClicked(par1, par2, par3);
		this.newZTextBox.mouseClicked(par1, par2, par3);
	}

	public void drawScreen(int par1, int par2, float par3) {
		this.drawGuiContainerBackgroundLayer(par3, par1, par2);
		this.drawGuiContainerForegroundLayer(par1, par2);

		super.drawScreen(par1, par2, par3);
	}

	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String s = WeepingAngelsMod.vortexManName;
		this.fontRenderer.drawString(s,
				(this.width / 2) - (this.fontRenderer.getStringWidth(s) / 2),
				this.topOfGui + (int) (0.035 * this.ySize), 4210752);

		int gray = 4210752;
		int x = this.leftOfGui + (int) (0.07 * this.xSize);
		int y = this.topOfGui + (int) (0.435 * this.ySize);
		this.drawString(this.fontRenderer, "Index:", x, this.topOfGui
				+ (int) (0.13 * this.ySize), 0xffffff);
		this.drawString(this.fontRenderer, "Name:", x, this.topOfGui
				+ (int) (0.225 * this.ySize), 0xffffff);
		this.drawString(this.fontRenderer, "X:", x, y + 0, 0xffffff);
		this.drawString(this.fontRenderer, "Y:", x, y + 12, 0xffffff);
		this.drawString(this.fontRenderer, "Z:", x, y + 24, 0xffffff);

		x = this.leftOfGui + (int) (0.63 * this.xSize);

		String indexAndName = this.locationIndex + " : ";
		if (this.indexName.equals(""))
			indexAndName += "None";
		else
			indexAndName += this.indexName;

		this.fontRenderer.drawString(indexAndName, x
				+ (int) (0.11 * this.xSize), this.topOfGui
				+ (int) (0.165 * this.ySize), 4210752);

		this.fontRenderer.drawString(this.dimName, x, this.topOfGui
				+ (int) (0.28 * this.ySize), 4210752);

		x = this.leftOfGui + (int) (0.65 * this.xSize);
		int x2 = x + (int) (0.08 * this.xSize);

		this.fontRenderer.drawString("X:", x, this.topOfGui
				+ (int) (0.35 * this.ySize), 4210752);
		this.fontRenderer.drawString(this.coordX + "", x2, this.topOfGui
				+ (int) (0.35 * this.ySize), 4210752);

		this.fontRenderer.drawString("Y:", x, this.topOfGui
				+ (int) (0.4 * this.ySize), 4210752);
		this.fontRenderer.drawString(this.coordY + "", x2, this.topOfGui
				+ (int) (0.4 * this.ySize), 4210752);

		this.fontRenderer.drawString("Z:", x, this.topOfGui
				+ (int) (0.45 * this.ySize), 4210752);
		this.fontRenderer.drawString(this.coordZ + "", x2, this.topOfGui
				+ (int) (0.45 * this.ySize), 4210752);

	}

	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(bkgd);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		this.indexTextBox.drawTextBox();
		this.nameTextBox.drawTextBox();
		this.newXTextBox.drawTextBox();
		this.newYTextBox.drawTextBox();
		this.newZTextBox.drawTextBox();

	}

	// Utilities
	private void setupTextField(GuiTextField textField) {

		textField.setTextColor(-1);
		textField.setDisabledTextColour(-1);
		textField.setEnableBackgroundDrawing(true);
		textField.setMaxStringLength(6);
	}

	private void storeCoords(int index, String name, int dimID, double x,
			double y, double z) {
		if (this.vortexStack != null) {
			if (index <= 0 || index >= 11) {
				this.mc.displayGuiScreen(null);
				this.thePlayer.addChatMessage("Indexes cannot be less than 1,");
				this.thePlayer.addChatMessage("nor greater than 10.");
				return;
			}
			NBTTagCompound tagCom;
			if (this.vortexStack.hasTagCompound())
				tagCom = this.vortexStack.getTagCompound();
			else
				tagCom = new NBTTagCompound();

			NBTTagCompound coorsTag = tagCom.getCompoundTag("Coords");
			if (coorsTag == null) {
				coorsTag = new NBTTagCompound();
			}

			NBTTagCompound coor = new NBTTagCompound();
			coor.setString("name", name);
			coor.setInteger("dim", dimID);
			coor.setDouble("x", x);
			coor.setDouble("y", y);
			coor.setDouble("z", z);

			coorsTag.setCompoundTag(index + "", coor);
			tagCom.setTag("Coords", coorsTag);
			this.vortexStack.setTagCompound(tagCom);
			PacketDispatcher.sendPacketToServer(Util.buildNBTPacket(
					"WepAng_vortex", this.vortexStack));
		}
	}

	private void printCoordinates() {
		if (this.vortexStack != null) {
			NBTTagCompound tagCom;
			if (this.vortexStack.hasTagCompound())
				tagCom = this.vortexStack.getTagCompound();
			else
				tagCom = new NBTTagCompound();

			NBTTagCompound coorsTag = tagCom.getCompoundTag("Coords");
			if (coorsTag == null)
				return;

			int i = 1;
			while (coorsTag.getTag(i + "") != null && i <= 10) {
				NBTTagCompound coor = (NBTTagCompound) coorsTag.getTag(i + "");
				int dimID = coor.getInteger("dim");
				double x = coor.getDouble("x");
				double y = coor.getDouble("y");
				double z = coor.getDouble("z");
				if (WeepingAngelsMod.DEBUG)
					WeepingAngelsMod.log.info(i + "~" + "Dim: " + dimID + "|"
							+ "X: " + x + "|" + "Y: " + y + "|" + "Z: " + z);
				i++;
			}

		}
	}

	private void guiDrawCentString(String str, int x, int y) {
		this.drawCenteredString(this.fontRenderer, str, x, y, 0xffffff);
	}

	private void guiDrawString(String str, int x, int y) {
		this.drawString(this.fontRenderer, str, x, y, 0xffffff);
	}

	public String getNext(List<String> list, String current) {
		int index = list.indexOf(current);
		index++;
		if (index >= list.size())
			return list.get(0);
		return list.get(index);
	}

	private double[] getCoords() {
		double newX, newY, newZ;
		try {
			newX = Double.parseDouble(this.newXTextBox.getText());
			newY = Double.parseDouble(this.newYTextBox.getText());
			newZ = Double.parseDouble(this.newZTextBox.getText());
			return new double[] { newX, newY, newZ };
		} catch (NumberFormatException err) {
			this.mc.displayGuiScreen(null);
			this.thePlayer.addChatMessage("Your coordinates are wrong.");
			this.thePlayer.addChatMessage("Please type valid coordinates.");
			return null;
		}
	}

	private boolean loadNextIndex(int current, int i) {
		if (i == 0)
			i = 1;
		else if (i >> 31 != 0)
			i = -1;
		else
			i = 1;
		if (this.vortexStack != null && this.vortexStack.hasTagCompound()) {
			int nextIndex = current + i;
			if (nextIndex < 0)
				nextIndex = 10;
			else if (nextIndex > 10)
				nextIndex = 0;
			NBTTagCompound tagCom = this.vortexStack.getTagCompound();
			NBTTagCompound coorsTag = tagCom.getCompoundTag("Coords");
			NBTTagCompound coord = coorsTag.getCompoundTag(nextIndex + "");
			if (coord == null) {
				return this.loadNextIndex(nextIndex, i);
			}
			this.locationIndex = nextIndex;
			this.indexName = coord.getString("name");
			this.dimName = CountryGamer_Core.CG_Core.dimensions1
					.get(coord.getInteger("dim"));
			this.coordX = coord.getDouble("x");
			this.coordY = coord.getDouble("y");
			this.coordZ = coord.getDouble("z");
			if (WeepingAngelsMod.DEBUG)
				WeepingAngelsMod.log.info("Index:" + this.locationIndex + " | "
						+ "Name:" + this.indexName + " | " + "Dim:"
						+ this.dimName + " | " + "X:" + this.coordX + " | "
						+ "Y:" + this.coordY + " | " + "Z:" + this.coordZ);
			return true;
		}
		return false;
	}

	public double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

}