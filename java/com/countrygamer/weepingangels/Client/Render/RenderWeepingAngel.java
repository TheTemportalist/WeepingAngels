package com.countrygamer.weepingangels.Client.Render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import com.countrygamer.weepingangels.Client.Model.ModelWeepingAngel;
import com.countrygamer.weepingangels.Entity.EntityWeepingAngel;
import com.countrygamer.weepingangels.lib.Reference;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWeepingAngel extends RenderLiving {
	protected ModelWeepingAngel weepingAngelModel;
	private ResourceLocation textureToUse = Reference.weepingAngelTex;

	public RenderWeepingAngel(float f) {
		super(new ModelWeepingAngel(), f);
		this.weepingAngelModel = (ModelWeepingAngel) this.mainModel;
	}

	public void renderWeepingAngel(EntityWeepingAngel angel, double d,
			double d1, double d2, float f, float f1) {
		// 0=false, 1=true
		byte angry = angel.getDataWatcher().getWatchableObjectByte(16);
		// if(WeepingAngelsMod.DEBUG)
		// WeepingAngelsMod.log.info(
		// "Render: Angry byte = " + angry);
		if (angry > 0) {
			// if(WeepingAngelsMod.DEBUG)
			// WeepingAngelsMod.log.info(
			// "Render: Is not angry");
			this.textureToUse = Reference.weepingAngelAngryTex;
		} else {
			// if(WeepingAngelsMod.DEBUG)WeepingAngelsMod.log.info(
			// "Render: Is angry");
			this.textureToUse = Reference.weepingAngelTex;
		}
		super.doRender(angel, d, d1, d2, f, f1);
	}

	public void doRenderLiving(EntityLiving entityliving, double d, double d1,
			double d2, float f, float f1) {
		renderWeepingAngel((EntityWeepingAngel) entityliving, d, d1, d2, f, f1);
	}

	public void doRender(Entity entity, double d, double d1, double d2,
			float f, float f1) {
		renderWeepingAngel((EntityWeepingAngel) entity, d, d1, d2, f, f1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.textureToUse;
	}

}