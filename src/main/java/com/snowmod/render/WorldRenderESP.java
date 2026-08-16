package com.snowmod.render;

import com.snowmod.finder.BitTracker;
import com.snowmod.finder.Finder;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WorldRenderESP {

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        Vec3d cameraPos = context.camera().getPos();
        MatrixStack matrices = context.matrixStack();

        for (Finder finder : BitTracker.getInstance().getActiveFinders()) {
            if (!client.world.getRegistryKey().getValue().getPath().equals(finder.getDimension())) continue;

            BlockPos pos = finder.getBlockPos();
            Box box = new Box(pos).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            int color = finder.getColor();
            float red = ((color >> 16) & 0xFF) / 255.0f;
            float green = ((color >> 8) & 0xFF) / 255.0f;
            float blue = (color & 0xFF) / 255.0f;

            VertexConsumer buffer = context.consumers().getBuffer(RenderLayer.getLines());
            drawBoxOutline(matrices, buffer, box, red, green, blue, 0.8f);
        }
    }

    private static void drawBoxOutline(MatrixStack matrices, VertexConsumer buffer, Box box, float r, float g, float b, float a) {
        MatrixStack.Entry entry = matrices.peek();
        
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Bottom box outline
        buffer.vertex(entry.getPositionMatrix(), minX, minY, minZ).color(r, g, b, a).normal(entry, 1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), maxX, minY, minZ).color(r, g, b, a).normal(entry, 1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), maxX, minY, minZ).color(r, g, b, a).normal(entry, 0, 0, 1);
        buffer.vertex(entry.getPositionMatrix(), maxX, minY, maxZ).color(r, g, b, a).normal(entry, 0, 0, 1);
        buffer.vertex(entry.getPositionMatrix(), maxX, minY, maxZ).color(r, g, b, a).normal(entry, -1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), minX, minY, maxZ).color(r, g, b, a).normal(entry, -1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), minX, minY, maxZ).color(r, g, b, a).normal(entry, 0, 0, -1);
        buffer.vertex(entry.getPositionMatrix(), minX, minY, minZ).color(r, g, b, a).normal(entry, 0, 0, -1);

        // Top box outline
        buffer.vertex(entry.getPositionMatrix(), minX, maxY, minZ).color(r, g, b, a).normal(entry, 1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), maxX, maxY, minZ).color(r, g, b, a).normal(entry, 1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), maxX, maxY, minZ).color(r, g, b, a).normal(entry, 0, 0, 1);
        buffer.vertex(entry.getPositionMatrix(), maxX, maxY, maxZ).color(r, g, b, a).normal(entry, 0, 0, 1);
        buffer.vertex(entry.getPositionMatrix(), maxX, maxY, maxZ).color(r, g, b, a).normal(entry, -1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), minX, maxY, maxZ).color(r, g, b, a).normal(entry, -1, 0, 0);
        buffer.vertex(entry.getPositionMatrix(), minX, maxY, maxZ).color(r, g, b, a).normal(entry, 0, 0, -1);
        buffer.vertex(entry.getPositionMatrix(), minX, maxY, minZ).color(r, g, b, a).normal(entry, 0, 0, -1);
    }
}
