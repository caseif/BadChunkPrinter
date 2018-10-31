package net.caseif.badchunkprinter.mixin;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {

    private static final String SELF_CHECKEDREADCHUNKFROMNBT__ASYNC
            = "checkedReadChunkFromNBT__Async(Lnet/minecraft/world/World;IILnet/minecraft/nbt/NBTTagCompound;)[Ljava/lang/Object;";

    private static final String ANVILCHUNKLOADER_READCHUNKFROMNBT
            = "net/minecraft/world/chunk/storage/AnvilChunkLoader.readChunkFromNBT(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;";

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

}
