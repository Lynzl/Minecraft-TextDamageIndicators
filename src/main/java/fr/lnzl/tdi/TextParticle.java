package fr.lnzl.tdi;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class TextParticle extends Particle {

    private String text;

    private double particleScale;

    private Double animationMinSize;
    private Double animationMaxSize;

    private Integer animationColorOne;
    private Integer animationColorTwo;

    private Boolean animationFade;

    public TextParticle(World world, double x, double y, double z) {
        super(world, x, y, z);

        this.canCollide = false;

        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;

    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSize(double size) {
        this.particleScale = size;
    }

    public void setAlpha(float alpha) {
        this.particleAlpha = alpha;
    }

    public void setColor(int color) {
        this.particleRed = Util.getRedFromColor(color);
        this.particleGreen = Util.getGreenFromColor(color);
        this.particleBlue = Util.getBlueFromColor(color);
    }

    public void setAnimationSize(double min, double max) {
        this.animationMinSize = min;
        this.animationMaxSize = max;
    }

    public void setAnimationColor(int one, int two) {
        this.animationColorOne = one;
        this.animationColorTwo = two;
    }

    public void setAnimationFade(boolean animationFade) {
        this.animationFade = animationFade;
    }

    @Override
    public void renderParticle(@Nonnull IVertexBuilder buffer, @Nonnull ActiveRenderInfo renderInfo, float partialTicks) {
        if (this.text == null) return;
        if (this.text.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        EntityRendererManager renderManager = minecraft.getRenderManager();
        FontRenderer fontrenderer = renderManager.getFontRenderer();

        float particleX = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - renderInfo.getProjectedView().getX());
        float particleY = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - renderInfo.getProjectedView().getY());
        float particleZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - renderInfo.getProjectedView().getZ());

        float textX = (float) (-fontrenderer.getStringWidth(this.text) / 2);
        float textY = 0;

        int textColor = Util.getColorFromRGBA(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        int shadowColor = Util.getColorFromRGBA(this.particleRed * 0.314F, this.particleGreen * 0.314F, this.particleBlue * 0.314F, this.particleAlpha);

        this.animateSize(partialTicks);
        this.animateFade(partialTicks);
        this.animateColor(partialTicks);

        if (this.particleAlpha == 0) return;

        IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.translate(particleX, particleY, particleZ);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-renderInfo.getYaw()));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(renderInfo.getPitch()));
        matrixStack.scale(-0.024F, -0.024F, 0.024F);

        if (this.particleScale != 0)
            matrixStack.scale((float) this.particleScale, (float) this.particleScale, 1);
        matrixStack.translate(0, 0, 1);
        fontrenderer.renderString(this.text, textX, textY, shadowColor, false, matrixStack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
        matrixStack.translate(0, 0, -2);
        fontrenderer.renderString(this.text, textX, textY, textColor, false, matrixStack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
        matrixStack.translate(0, 0, 1);
        matrixStack.pop();

        irendertypebuffer$impl.finish();
    }

    @Override
    @Nonnull
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    public void animateSize(float partialTicks) {
        if (this.animationMinSize == null || this.animationMaxSize == null) return;

        double d1 = 6D * (this.age - 1 + partialTicks) / this.maxAge * (this.animationMaxSize - this.animationMinSize) - this.animationMaxSize + this.animationMinSize;
        double d2 = -3D * (this.age - 1 + partialTicks) / this.maxAge * (this.animationMaxSize - this.animationMinSize) + 2.5D * this.animationMaxSize - 2.5D * this.animationMinSize;
        double d3 = -(d1 + Math.abs(d1)) + 2D * this.animationMaxSize - 2D * this.animationMinSize;
        double d4 = -(d2 + Math.abs(d2)) + 2D * this.animationMaxSize - 2D * this.animationMinSize;
        this.particleScale = -(d3 + Math.abs(d3) + d4 + Math.abs(d4)) / 4D + this.animationMaxSize;
    }

    public void animateColor(float partialTicks) {
        if (this.animationColorOne == null || this.animationColorTwo == null) return;

        double d1 = (6D * (this.age - 1 + partialTicks) / this.maxAge - 2D) + Math.abs(6D * (this.age - 1 + partialTicks) / this.maxAge - 2D);
        double d2 = -(6D * (this.age - 1 + partialTicks) / this.maxAge - 4D) + Math.abs(6D * (this.age - 1 + partialTicks) / this.maxAge - 4D);
        double alpha = (-d1 - d2 + 6D + Math.abs(-d1 - d2 + 6D)) / 4D;
        this.particleRed = (float) (Util.getRedFromColor(this.animationColorOne) * (1D - alpha) + Util.getRedFromColor(this.animationColorTwo) * alpha);
        this.particleGreen = (float) (Util.getGreenFromColor(this.animationColorOne) * (1D - alpha) + Util.getGreenFromColor(this.animationColorTwo) * alpha);
        this.particleBlue = (float) (Util.getBlueFromColor(this.animationColorOne) * (1D - alpha) + Util.getBlueFromColor(this.animationColorTwo) * alpha);
    }

    public void animateFade(float partialTicks) {
        if (!this.animationFade) return;

        double d1 = (this.maxAge - (this.age - 1 + partialTicks)) + this.maxAge / 6D;
        double d2 = (this.maxAge - (this.age - 1 + partialTicks)) - this.maxAge / 6D;
        this.particleAlpha = (float) ((d1 - Math.abs(d2)) / (this.maxAge / 3D));
    }

    public static class OnoParticle extends TextParticle {

        public OnoParticle(World world, double x, double y, double z) {
            super(world, x + 0.1D, y, z + 0.1D);

            double d1 = (Math.random() + Math.random() + 1.0D) * 0.15D;
            double d2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

            this.motionX = (Math.random() * 2.0D - 1.0D) * 0.4D;
            this.motionX = this.motionX / d1 * d2 * 0.4D;
            this.motionY = (Math.random() * 2.0D - 1.0D) * 0.4D;
            this.motionY = this.motionY / d1 * d2 * 0.4D + 0.1D;
            this.motionZ = (Math.random() * 2.0D - 1.0D) * 0.4D;
            this.motionZ = this.motionZ / d1 * d2 * 0.4D;

            this.motionY *= 0.1F;
            this.motionY += 0.3D;

            this.tick();
        }

        @Override
        public void tick() {
            if (this.age++ >= this.maxAge) {
                this.setExpired();
                return;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            this.move(this.motionX, this.motionY, this.motionZ);

            this.motionX *= 0.7F;
            this.motionY *= 0.7F;
            this.motionZ *= 0.7F;
            this.motionY -= 0.02F;
        }
    }

    public static class DamageParticle extends TextParticle {

        public DamageParticle(World world, double x, double y, double z) {
            super(world, x, y, z);

            this.tick();
        }

        @Override
        public void tick() {
            if (this.age++ >= this.maxAge) {
                this.setExpired();
                return;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            this.motionY = (0.12D * (2D * (this.age - 1) - this.maxAge) * (2D * (this.age - 1) - this.maxAge) / (this.maxAge * this.maxAge));
            this.move(this.motionX, this.motionY, this.motionZ);
        }
    }
}