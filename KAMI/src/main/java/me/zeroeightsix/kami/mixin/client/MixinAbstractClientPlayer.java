package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.util.CapeManager;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {

    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = ("getLocationCape"), at = {@At("HEAD")}, cancellable = true)
    public void getLocationCape(final CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) throws IOException {

        final NetworkPlayerInfo info = this.getPlayerInfo();

        UUID uuid = null;

        if (info != null) {
            uuid = this.getPlayerInfo().getGameProfile().getId();
        }

        if (uuid != null && CapeManager.hasCape(uuid)) {
            if (CapeManager.isDev(uuid)) {
                callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/devcape.png"));
            } else if (CapeManager.isUpperDonator(uuid)) { //
                DynamicTexture texture = new DynamicTexture(ImageIO.read(new URL("https://raw.githubusercontent.com/CliNetMC/capes/master/" + CapeManager.getImgName(uuid))));
                ResourceLocation location = Wrapper.getMinecraft().getTextureManager().getDynamicTextureLocation("textures/capes", texture);
                callbackInfoReturnable.setReturnValue(location);
            } else {
                callbackInfoReturnable.setReturnValue(new ResourceLocation("textures/donatorcape.png"));
            }
        }

    }

}