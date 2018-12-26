package net.caseif.badchunkprinter.mixin;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.DataInputStream;
import java.io.IOException;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {

    private static final String SELF_CHECKEDREADCHUNKFROMNBT__ASYNC
            = "checkedReadChunkFromNBT__Async(Lnet/minecraft/world/World;IILnet/minecraft/nbt/NBTTagCompound;)[Ljava/lang/Object;";

    private static final String ANVILCHUNKLOADER_READCHUNKFROMNBT
            = "net/minecraft/world/chunk/storage/AnvilChunkLoader.readChunkFromNBT(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;";

    private static final String SELF_LOADCHUNK__ASYNC
            = "loadChunk__Async(Lnet/minecraft/world/World;II)[Ljava/lang/Object;";

    private static final String COMPRESSEDSTREAMTOOLS_READ
            = "net/minecraft/nbt/CompressedStreamTools.read(Ljava/io/DataInputStream;)Lnet/minecraft/nbt/NBTTagCompound;";

    @Shadow abstract Chunk readChunkFromNBT(World world, NBTTagCompound nbt);

    @Redirect(method = SELF_CHECKEDREADCHUNKFROMNBT__ASYNC, at = @At(value = "INVOKE", target = ANVILCHUNKLOADER_READCHUNKFROMNBT))
    public Chunk readChunkFromNBT_redirect(AnvilChunkLoader self, World world, NBTTagCompound nbt, World parentWorld, int chunkX, int chunkZ, NBTTagCompound parentNbt) {
        try {
            return readChunkFromNBT(world, nbt);
        } catch (Throwable t) {
            System.err.println("Encountered corrupted chunk at (" + chunkX + ", " + chunkZ + ").");
            throw t;
        }
    }

    @Redirect(method = SELF_LOADCHUNK__ASYNC, at = @At(value = "INVOKE", target = COMPRESSEDSTREAMTOOLS_READ))
    public NBTTagCompound read_redirect(DataInputStream dataStream, World parentWorld, int chunkX, int chunkZ) throws IOException {
        try {
            return CompressedStreamTools.read(dataStream);
        } catch (Throwable t) {
            System.err.println("Encountered corrupted chunk at (" + chunkX + ", " + chunkZ + ").");
            throw t;
        }
    }

}
