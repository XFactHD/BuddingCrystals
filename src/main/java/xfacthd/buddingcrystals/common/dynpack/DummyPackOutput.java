package xfacthd.buddingcrystals.common.dynpack;

import net.minecraft.data.PackOutput;

import java.nio.file.Path;

@SuppressWarnings({ "ConstantConditions", "NullableProblems" })
public final class DummyPackOutput extends PackOutput
{
    public static final DummyPackOutput INSTANCE = new DummyPackOutput();

    private DummyPackOutput() { super(null); }

    @Override
    public Path getOutputFolder(Target target) { return null; }
}
