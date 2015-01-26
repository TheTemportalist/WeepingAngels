package com.temportalist.weepingangels.client.render.models

import com.temportalist.weepingangels.common.entity.EntityWeepingAngel
import net.minecraft.client.model.{ModelBase, ModelRenderer}
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.util.MathHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class ModelWeepingAngel() extends ModelBase() {

	private var leftfoot: ModelRenderer = null
	private var rightfoot: ModelRenderer = null
	private var leftwing1: ModelRenderer = null
	private var leftwing2: ModelRenderer = null
	private var leftwing3: ModelRenderer = null
	private var leftwing4: ModelRenderer = null
	private var rightwing1: ModelRenderer = null
	private var rightwing2: ModelRenderer = null
	private var rightwing3: ModelRenderer = null
	private var rightwing4: ModelRenderer = null
	private var head: ModelRenderer = null
	private var body: ModelRenderer = null
	private var rightarm: ModelRenderer = null
	private var leftarm: ModelRenderer = null
	private var rightleg: ModelRenderer = null
	private var leftleg: ModelRenderer = null
	private var angleX: Float = 0.0F
	private var angleY: Float = 0.0F
	private var angleZ: Float = 0.0F

	// Default Constructor
	{
		this.textureHeight = 32
		this.textureWidth = 64
		leftfoot = new ModelRenderer(this, 32, 0)
		leftfoot.addBox(-2F, 7F, -4F, 6, 5, 8)
		leftfoot.setRotationPoint(2.0F, 12F, 0.0F)
		leftfoot.rotateAngleX = 0.0F
		leftfoot.rotateAngleY = 0.0F
		leftfoot.rotateAngleZ = 0.0F
		leftfoot.mirror = false
		rightfoot = new ModelRenderer(this, 32, 0)
		rightfoot.addBox(-4F, 7F, -4F, 6, 5, 8)
		rightfoot.setRotationPoint(-2F, 12F, 0.0F)
		rightfoot.rotateAngleX = 0.0F
		rightfoot.rotateAngleY = 0.0F
		rightfoot.rotateAngleZ = 0.0F
		rightfoot.mirror = false
		leftwing1 = new ModelRenderer(this, 40, 25)
		leftwing1.addBox(-0.5F, -1F, 1.0F, 1, 5, 2)
		leftwing1.setRotationPoint(1.0F, 1.0F, 1.0F)
		leftwing1.rotateAngleX = 0.20944F
		leftwing1.rotateAngleY = 0.61087F
		leftwing1.rotateAngleZ = 0.0F
		leftwing1.mirror = false
		leftwing2 = new ModelRenderer(this, 46, 19)
		leftwing2.addBox(-0.5F, -2F, 3F, 1, 11, 2)
		leftwing2.setRotationPoint(1.0F, 1.0F, 1.0F)
		leftwing2.rotateAngleX = 0.20944F
		leftwing2.rotateAngleY = 0.61087F
		leftwing2.rotateAngleZ = 0.01745F
		leftwing2.mirror = false
		leftwing3 = new ModelRenderer(this, 58, 12)
		leftwing3.addBox(-0.5F, -2F, 5F, 1, 18, 2)
		leftwing3.setRotationPoint(1.0F, 1.0F, 1.0F)
		leftwing3.rotateAngleX = 0.20944F
		leftwing3.rotateAngleY = 0.61087F
		leftwing3.rotateAngleZ = 0.0F
		leftwing3.mirror = false
		leftwing4 = new ModelRenderer(this, 52, 16)
		leftwing4.addBox(-0.5F, 0.0F, 7F, 1, 14, 2)
		leftwing4.setRotationPoint(1.0F, 1.0F, 1.0F)
		leftwing4.rotateAngleX = 0.20944F
		leftwing4.rotateAngleY = 0.61087F
		leftwing4.rotateAngleZ = 0.0F
		leftwing4.mirror = false
		rightwing1 = new ModelRenderer(this, 40, 25)
		rightwing1.addBox(-0.5F, -1F, 1.0F, 1, 5, 2)
		rightwing1.setRotationPoint(-2F, 1.0F, 1.0F)
		rightwing1.rotateAngleX = 0.20944F
		rightwing1.rotateAngleY = -0.61087F
		rightwing1.rotateAngleZ = 0.0F
		rightwing1.mirror = false
		rightwing2 = new ModelRenderer(this, 46, 19)
		rightwing2.addBox(-0.5F, -2F, 3F, 1, 11, 2)
		rightwing2.setRotationPoint(-2F, 1.0F, 1.0F)
		rightwing2.rotateAngleX = 0.20944F
		rightwing2.rotateAngleY = -0.61087F
		rightwing2.rotateAngleZ = 0.0F
		rightwing2.mirror = false
		rightwing3 = new ModelRenderer(this, 58, 12)
		rightwing3.addBox(-0.5F, -2F, 5F, 1, 18, 2)
		rightwing3.setRotationPoint(-2F, 1.0F, 1.0F)
		rightwing3.rotateAngleX = 0.20944F
		rightwing3.rotateAngleY = -0.61087F
		rightwing3.rotateAngleZ = 0.0F
		rightwing3.mirror = false
		rightwing4 = new ModelRenderer(this, 52, 16)
		rightwing4.addBox(-0.5F, 0.0F, 7F, 1, 14, 2)
		rightwing4.setRotationPoint(-2F, 1.0F, 1.0F)
		rightwing4.rotateAngleX = 0.20944F
		rightwing4.rotateAngleY = -0.61087F
		rightwing4.rotateAngleZ = 0.0F
		rightwing4.mirror = false
		head = new ModelRenderer(this, 0, 0)
		head.addBox(-4F, -8F, -4F, 8, 8, 8)
		head.setRotationPoint(0.0F, 0.0F, 0.0F)
		head.rotateAngleX = 0.24435F
		head.rotateAngleY = 0.0F
		head.rotateAngleZ = 0.0F
		head.mirror = false
		body = new ModelRenderer(this, 0, 16)
		body.addBox(-4F, 0.0F, -2F, 8, 12, 4)
		body.setRotationPoint(0.0F, 0.0F, 0.0F)
		body.rotateAngleX = 0.0F
		body.rotateAngleY = 0.0F
		body.rotateAngleZ = 0.0F
		body.mirror = false
		rightarm = new ModelRenderer(this, 24, 19)
		rightarm.addBox(-3F, 0.0F, -2F, 4, 9, 4)
		rightarm.setRotationPoint(-5F, 0.0F, 0.0F)
		rightarm.rotateAngleX = -1.74533F
		rightarm.rotateAngleY = -0.55851F
		rightarm.rotateAngleZ = 0.0F
		rightarm.mirror = false
		leftarm = new ModelRenderer(this, 24, 19)
		leftarm.addBox(-1F, 0.0F, -2F, 4, 9, 4)
		leftarm.setRotationPoint(5F, 0.0F, 0.0F)
		leftarm.rotateAngleX = -1.74533F
		leftarm.rotateAngleY = 0.55851F
		leftarm.rotateAngleZ = 0.0F
		leftarm.mirror = false
		rightleg = new ModelRenderer(this, 24, 19)
		rightleg.addBox(-2F, 0.0F, -2F, 4, 9, 4)
		rightleg.setRotationPoint(-2F, 12F, 0.0F)
		rightleg.rotateAngleX = 0.0F
		rightleg.rotateAngleY = 0.0F
		rightleg.rotateAngleZ = 0.0F
		rightleg.mirror = false
		leftleg = new ModelRenderer(this, 24, 19)
		leftleg.addBox(-2F, 0.0F, -2F, 4, 9, 4)
		leftleg.setRotationPoint(2.0F, 12F, 0.0F)
		leftleg.rotateAngleX = 0.0F
		leftleg.rotateAngleY = 0.0F
		leftleg.rotateAngleZ = 0.0F
		leftleg.mirror = false
	}

	// End Constructor
	override def render(entity: Entity, f: Float, f1: Float, f2: Float, f3: Float, f4: Float,
			f5: Float): Unit = {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity)

		if (this.isChild) {
			val f6: Float = 2.0F
			GlStateManager.pushMatrix()
			GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6)
			GlStateManager.translate(0.0F, 24.0F * f5, 0.0F)
			this.head.render(f5)
			this.leftfoot.render(f5)
			this.rightfoot.render(f5)
			this.body.render(f5)
			this.rightarm.render(f5)
			this.leftarm.render(f5)
			this.rightleg.render(f5)
			this.leftleg.render(f5)
			GlStateManager.popMatrix()
		}
		else {
			GlStateManager.pushMatrix()
			this.leftfoot.render(f5)
			this.rightfoot.render(f5)
			this.leftwing1.render(f5)
			this.leftwing2.render(f5)
			this.leftwing3.render(f5)
			this.leftwing4.render(f5)
			this.rightwing1.render(f5)
			this.rightwing2.render(f5)
			this.rightwing3.render(f5)
			this.rightwing4.render(f5)
			this.head.render(f5)
			this.body.render(f5)
			this.rightarm.render(f5)
			this.leftarm.render(f5)
			this.rightleg.render(f5)
			this.leftleg.render(f5)
			GlStateManager.popMatrix()
		}
	}

	override def setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float,
			entity: Entity): Unit = {
		entity match {
			case angel: EntityWeepingAngel =>
				if (angel.getAngryState == 1) {
					angleX = Math.toRadians(20).asInstanceOf[Float]
					angleY = Math.toRadians(60).asInstanceOf[Float]
					angleZ = Math.toRadians(5).asInstanceOf[Float]
				}
				else {
					angleX = Math.toRadians(10).asInstanceOf[Float]
					angleY = Math.toRadians(30).asInstanceOf[Float]
					angleZ = Math.toRadians(5).asInstanceOf[Float]
				}
				if (angel.getArmState >= 2) {
					val f6: Float = MathHelper.sin(this.swingProgress * 3.141593F)
					var f7: Float = MathHelper.sin((1.0F - (1.0F - this.swingProgress)
							* (1.0F - this.swingProgress)) * 3.141593F)
					rightarm.rotateAngleZ = 0.0F
					leftarm.rotateAngleZ = 0.0F
					rightarm.rotateAngleY = -(0.1F - f6 * 0.6F)
					leftarm.rotateAngleY = 0.1F - f6 * 0.6F
					rightarm.rotateAngleX = -1.570796F
					leftarm.rotateAngleX = -1.570796F
				}
				else if (angel.getArmState == 1) {
					rightarm.rotateAngleX = -1.04533F
					rightarm.rotateAngleY = -0.55851F
					rightarm.rotateAngleZ = 0.0F
					leftarm.rotateAngleX = -1.04533F
					leftarm.rotateAngleY = 0.55851F
					leftarm.rotateAngleZ = 0.0F
				}
				else {
					rightarm.rotateAngleX = -1.74533F
					rightarm.rotateAngleY = -0.55851F
					rightarm.rotateAngleZ = 0.0F
					leftarm.rotateAngleX = -1.74533F
					leftarm.rotateAngleY = 0.55851F
					leftarm.rotateAngleZ = 0.0F
				}
				rightwing2.rotateAngleX = angleX
				rightwing3.rotateAngleX = angleX
				rightwing4.rotateAngleX = angleX
				rightwing1.rotateAngleX = angleX
				leftwing2.rotateAngleX = angleX
				leftwing3.rotateAngleX = angleX
				leftwing4.rotateAngleX = angleX
				leftwing1.rotateAngleX = angleX
				rightwing2.rotateAngleY = -angleY
				rightwing3.rotateAngleY = -angleY
				rightwing4.rotateAngleY = -angleY
				rightwing1.rotateAngleY = -angleY
				leftwing2.rotateAngleY = angleY
				leftwing3.rotateAngleY = angleY
				leftwing4.rotateAngleY = angleY
				leftwing1.rotateAngleY = angleY
				rightwing2.rotateAngleZ = angleZ
				rightwing3.rotateAngleZ = angleZ
				rightwing4.rotateAngleZ = angleZ
				rightwing1.rotateAngleZ = angleZ
				leftwing2.rotateAngleZ = -angleZ
				leftwing3.rotateAngleZ = -angleZ
				leftwing4.rotateAngleZ = -angleZ
				leftwing1.rotateAngleZ = -angleZ
			case _ =>
				rightarm.rotateAngleX = -1.74533F
				rightarm.rotateAngleY = -0.55851F
				rightarm.rotateAngleZ = 0.0F
				leftarm.rotateAngleX = -1.74533F
				leftarm.rotateAngleY = 0.55851F
				leftarm.rotateAngleZ = 0.0F
		}

	}

}
