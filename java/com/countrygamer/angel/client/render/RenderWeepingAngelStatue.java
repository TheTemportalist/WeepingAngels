package com.countrygamer.angel.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.countrygamer.angel.client.model.ModelWeepingAngel;
import com.countrygamer.angel.common.WeepingAngels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWeepingAngelStatue extends RenderLiving {
	
	public RenderWeepingAngelStatue() {
		super(new ModelWeepingAngel(), 0.5f);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return WeepingAngels.weepingAngelTex;
	}
}
