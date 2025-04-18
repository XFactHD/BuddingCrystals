package xfacthd.buddingcrystals.common.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;

/**
 * Reference holder which can be serialized without being created for an owner.
 * <p>
 * Must only be used for serialization.
 */
public final class UnownedReferenceHolder<T> extends Holder.Reference<T>
{
    @SuppressWarnings("ConstantConditions") // The only relevant usage of the owner is handled
    public UnownedReferenceHolder(ResourceKey<T> key)
    {
        super(Type.STAND_ALONE, null, key, null);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner)
    {
        return true;
    }
}
