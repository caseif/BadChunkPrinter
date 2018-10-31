package net.caseif.badchunkprinter.mixin;

import net.caseif.badchunkprinter.Main;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {

    private static final String SELF_READCHUNKFROMNBT
            = "readChunkFromNBT(Lnet/minecraft/world/World;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/world/chunk/Chunk;";

    private static final String NIBBLE_ARRAY = "net/minecraft/world/chunk/NibbleArray";

    @Redirect(method = SELF_READCHUNKFROMNBT, at = @At(value = "NEW", target = NIBBLE_ARRAY))
    public NibbleArray constructNibbleArray(byte[] bytes, World world, NBTTagCompound nbt) {
        try {
            return new NibbleArray(bytes);
        } catch (Throwable t) {
            int chunkX = nbt.getInteger("xPos");
            int chunkZ = nbt.getInteger("zPos");

            System.err.println("Encountered corrupted chunk at (" + chunkX + ", " + chunkZ + ").");
            throw t;
        }
    }

}
