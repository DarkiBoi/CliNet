package me.zeroeightsix.kami.module.modules.render;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.ChunkEvent;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import net.minecraft.block.BlockPortal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.Mod;

import javax.xml.bind.Marshaller;
import java.util.ArrayList;

@Module.Info(name = "PortalTracer", description = "Tracks Portals", category = Module.Category.RENDER)
public class PortalTracer extends Module {

    private ArrayList<BlockPos> portals = new ArrayList<>();

    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if(packet.getSound() == SoundEvents.BLOCK_PORTAL_AMBIENT){
                Command.sendChatMessage("Portal Located at: " + packet.posX + " " + packet.getY() + " " + packet.posZ);
            }
            if(packet.getSound() == SoundEvents.ENTITY_COW_STEP){
                Command.sendChatMessage("Cow Located at: " + packet.posX + " " + packet.getY() + " " + packet.posZ);

            }
            if(packet.getSound() == SoundEvents.ENTITY_PLAYER_DEATH){
                Command.sendChatMessage("Player dead Located at: " + packet.posX + " " + packet.getY() + " " + packet.posZ);

            }
            final String sound = packet.getSound().getSoundName().getPath();
                if(sound.endsWith("ambient")){
                    Command.sendChatMessage("Located at "+ packet.posX);
                }

            if(packet.getSound() == SoundEvents.BLOCK_PORTAL_TRIGGER){
                Command.sendChatMessage("Portal Located at: " + packet.posX + " " + packet.getY() + " " + packet.posZ);
            }if(packet.getSound() == SoundEvents.BLOCK_PORTAL_TRAVEL){
                Command.sendChatMessage("Portal Located at: " + packet.posX + " " + packet.getY() + " " + packet.posZ);
            }


        }
    });

    @EventHandler
    private Listener<ChunkEvent> loadListener = new Listener<>(event -> {
        Chunk chunk = event.getChunk();
        // Remove already registered portals from this chunk, allowing removed portals to vanish from tracers and no duplicates to be made
        portals.removeIf(blockPos -> blockPos.getX() / 16 == chunk.x && blockPos.getZ() / 16 == chunk.z);

        for (ExtendedBlockStorage storage : chunk.getBlockStorageArray()) {
            if (storage != null) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            if (storage.get(x, y, z).getBlock() instanceof BlockPortal) {
                                int px = chunk.x * 16 + x;
                                int py = storage.yBase + y;
                                int pz = chunk.z * 16 + z;
                                portals.add(new BlockPos(px, py, pz));
                                y += 6;
                                Command.sendChatMessage("Portal Located at: " + portals.toString());
                            }
                        }
                    }
                }
            }
        }
    });
}
