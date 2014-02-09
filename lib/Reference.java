package com.countrygamer.weepingangels.lib;

import net.minecraft.util.ResourceLocation;

public class Reference {

	/* Mod constants */
	public static final String MOD_ID = "weepingangels";
	public static final String BASE_TEX = Reference.MOD_ID + ":";
	public static final String MOD_NAME = "Weeping Angels Mod";
	public static final String VERSION = "2.0.0";
	public static final String MC_VERSION = "1.7.2";
	public static final String CHANNEL_NAME = MOD_ID;
	public static final String SERVER_PROXY_CLASS = "com.countrygamer."
			+ MOD_ID + ".proxy.ServerProxy";
	public static final String CLIENT_PROXY_CLASS = "com.countrygamer."
			+ MOD_ID + ".proxy.ClientProxy";

	public static final ResourceLocation weepingAngelTex = new ResourceLocation(
			BASE_TEX + "textures/entities/weepingangel.png");
	public static final ResourceLocation weepingAngelAngryTex = new ResourceLocation(
			BASE_TEX + "textures/entities/weepingangel-angry.png");

	public static final ResourceLocation weepingAngelStatueTex = new ResourceLocation(
			BASE_TEX + "textures/blocks/plinth.png");

}
