package marumasa.marumasa_sign.client;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CustomSignBlockEntityRenderer extends SignBlockEntityRenderer {

    private final MinecraftClient client;

    public CustomSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
        client = MinecraftClient.getInstance();
    }

    // renderメソッドをオーバーライドして レンダリング処理を変更する
    @Override
    public void render(SignBlockEntity sign, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        String textSign = CustomSign.read(sign);

        CustomSign customSign = CustomSign.load(textSign);

        if (customSign == null) {
            // 通常のMinecraft看板をレンダリングするために
            // 親クラスのrenderメソッドを呼び出して 看板のレンダリング処理をする
            super.render(sign, tickDelta, matrices, vertexConsumers, light, overlay);
            return;
        }

        final ClientPlayerEntity clientPlayer = client.player;

        // もし プレイヤーがスペクテイターモードだったら
        // 通常のMinecraft看板 も レンダリングするようにする
        if (clientPlayer != null && clientPlayer.isSpectator()) {
            // 親クラスのrenderメソッドを呼び出して 看板のレンダリング処理をする
            super.render(sign, tickDelta, matrices, vertexConsumers, light, overlay);
        }

        // 看板URLから画像をレンダリングする
        render(customSign, matrices, vertexConsumers, light, overlay);
    }

    public void render(CustomSign customSign, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(customSign.renderLayer);

        matrices.translate(0.5f, 0.5f, 0.5f); // 位置を設定
        matrices.scale(0.5f, 0.5f, 0.5f); // 大きさを設定
        matrices.multiply(customSign.rotation); // 回転を設定

        final MatrixStack.Entry peek = matrices.peek();
        final Matrix4f matrix4f = peek.getPositionMatrix();
        final Matrix3f matrix3f = peek.getNormalMatrix();
        final CustomSign.Vertex vertex = customSign.vertex;

        // 表面の描画処理 開始
        matrices.push();
        vertexConsumer.vertex(matrix4f,
                vertex.minusX(), vertex.Y(), vertex.minusZ()
        ).color(255, 255, 255, 255).texture(1, 1).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f,
                vertex.minusX(), vertex.Y(), vertex.plusZ()
        ).color(255, 255, 255, 255).texture(1, 0).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f,
                vertex.plusX(), vertex.Y(), vertex.plusZ()
        ).color(255, 255, 255, 255).texture(0, 0).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f,
                vertex.plusX(), vertex.Y(), vertex.minusZ()
        ).color(255, 255, 255, 255).texture(0, 1).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        matrices.pop();
        // 表面の描画処理 終了

        // 裏面の描画処理 開始
        matrices.push();
        vertexConsumer.vertex(matrix4f,
                vertex.plusX(), vertex.Y(), vertex.minusZ()
        ).color(255, 255, 255, 255).texture(0, 1).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f,
                vertex.plusX(), vertex.Y(), vertex.plusZ()
        ).color(255, 255, 255, 255).texture(0, 0).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f,
                vertex.minusX(), vertex.Y(), vertex.plusZ()
        ).color(255, 255, 255, 255).texture(1, 0).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        vertexConsumer.vertex(matrix4f,
                vertex.minusX(), vertex.Y(), vertex.minusZ()
        ).color(255, 255, 255, 255).texture(1, 1).overlay(overlay).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
        matrices.pop();
        // 裏面の描画処理 終了
    }


    @Override
    // ここで ブロックエンティティの表示範囲を設定できる
    public int getRenderDistance() {
        return 512;
    }

    @Override
    // 看板が通常では見えなくなる範囲になってもレンダリングをする
    public boolean rendersOutsideBoundingBox(SignBlockEntity blockEntity) {
        return true;
    }
}
