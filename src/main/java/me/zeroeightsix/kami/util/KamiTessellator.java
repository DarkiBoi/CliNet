package me.zeroeightsix.kami.util;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by 086 on 9/07/2017.
 */
public class KamiTessellator extends Tessellator {

    public static KamiTessellator INSTANCE = new KamiTessellator();

    public KamiTessellator() {
        super(0x200000);
    }

    public static void prepare(int mode) {
        prepareGL();
        begin(mode);
    }

    public static void prepareGL() {
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1,1,1);
    }

    public static void begin(int mode) {
        INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void release() {
        render();
        releaseGL();
    }

    public static void render() {
        INSTANCE.draw();
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public static void drawBox(BlockPos blockPos, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBox(blockPos, r, g, b, a, sides);
    }

    public static void drawBox(float x, float y, float z, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawBox(INSTANCE.getBuffer(), x, y, z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawHalfBox(BlockPos blockPos, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawHalfBox(blockPos, r, g, b, a, sides);
    }

    public static void drawHalfBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 0.5f, 1, r, g, b, a, sides);
    }

    public static void drawPlane(BlockPos blockPos, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawPlane(blockPos, r, g, b, a, sides);
    }

    public static void drawPlane(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 0f, 1, r, g, b, a, sides);
    }

    public static void drawItemBox(EntityItem item, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawItemBox(item, r, g, b, a, sides);
    }

    public static void drawItemBox(EntityItem item, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), ((float) item.posX - 0.1f), ((float) item.posY), ((float) item.posZ - 0.1f), item.width + 0.1f, item.height + 0.1f, item.width + 0.1f, r, g, b, a, sides);
    }

    public static BufferBuilder getBufferBuilder() {
        return INSTANCE.getBuffer();
    }

    public static void drawLinesWithVec3d(Vec3d vec, int r, int g, int b, int a, int sides) {
        drawLines(INSTANCE.getBuffer(), ((float) vec.x), ((float) vec.y), ((float) vec.z), 1, 2, 1, r, g, b, a, sides);
    }

    public static void drawBoxWithVec3d(Vec3d vec, int r, int g, int b, int a, int sides) {
        drawBox(INSTANCE.getBuffer(), ((float) vec.x), ((float) vec.y), ((float) vec.z), 1, 2, 1, r, g, b, a, sides);
    }

    public static void drawOutlinesWithVec3d(Vec3d vec, int r, int g, int b, int a, int sides) {
    }

    public static void drawOutlines(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.UP) != 0) {
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.NORTH) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.WEST) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.EAST) != 0) {
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawBox(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Quad.DOWN) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.UP) != 0) {
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.NORTH) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.SOUTH) != 0) {
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.WEST) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Quad.EAST) != 0) {
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawLines(float x, float y, float z, int argb, int sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        drawLines(INSTANCE.getBuffer(), x, y, z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void drawLines(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawLines(INSTANCE.getBuffer(), blockPos.x, blockPos.y, blockPos.z, 1, 1, 1, r, g, b, a, sides);
    }


    public static void drawLines(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & GeometryMasks.Line.DOWN_WEST) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_WEST) != 0) {
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_EAST) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_EAST) != 0) {
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_NORTH) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_NORTH) != 0) {
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.DOWN_SOUTH) != 0) {
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.UP_SOUTH) != 0) {
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.NORTH_WEST) != 0) {
            buffer.pos(x, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.NORTH_EAST) != 0) {
            buffer.pos(x+w, y, z).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.SOUTH_WEST) != 0) {
            buffer.pos(x, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x, y+h, z+d).color(r, g, b, a).endVertex();
        }

        if ((sides & GeometryMasks.Line.SOUTH_EAST) != 0) {
            buffer.pos(x+w, y, z+d).color(r, g, b, a).endVertex();
            buffer.pos(x+w, y+h, z+d).color(r, g, b, a).endVertex();
        }
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) x, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

}
