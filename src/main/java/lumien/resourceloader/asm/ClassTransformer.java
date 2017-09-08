package lumien.resourceloader.asm;

import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.SWAP;
import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassTransformer implements IClassTransformer
{
	Logger logger = LogManager.getLogger("ResourceLoader");

	public ClassTransformer()
	{

	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (transformedName.equals("net.minecraft.client.Minecraft"))
		{
			return patchMinecraft(basicClass);
		}
		return basicClass;
	}

	private byte[] patchMinecraft(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.DEBUG, "Found Minecraft Class: " + classNode.name);

		MethodNode refreshResources = null;
		MethodNode startGame = null;

		for (MethodNode mn : classNode.methods)
		{
			if (mn.name.equals(MCPNames.method("func_110436_a")))
			{
				refreshResources = mn;
			}
			else if (mn.name.equals(MCPNames.method("func_71384_a")))
			{
				startGame = mn;
			}
		}

		if (refreshResources != null)
		{
			logger.log(Level.DEBUG, " - Found refreshResources 1/3");

			for (int i = 0; i < refreshResources.instructions.size(); i++)
			{
				AbstractInsnNode ain = refreshResources.instructions.get(i);
				if (ain instanceof MethodInsnNode)
				{
					MethodInsnNode min = (MethodInsnNode) ain;
					if (min.name.equals(MCPNames.method("func_110541_a")))
					{
						InsnList toInsert = new InsnList();
						toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/resourceloader/ResourceLoader", "insertForcedPack", "(Ljava/util/List;)V", false));
						toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));

						refreshResources.instructions.insertBefore(min, toInsert);
						logger.log(Level.DEBUG, " - Patched refreshResources 3/3");
						
						i+=2;
					}
					else if (min.name.equals("newArrayList"))
					{
						AbstractInsnNode target = refreshResources.instructions.get(i + 1);
						
						InsnList toInsert = new InsnList();
						toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1));
						toInsert.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "lumien/resourceloader/ResourceLoader", "insertNormalPack", "(Ljava/util/List;)V", false));

						refreshResources.instructions.insert(target, toInsert);
						logger.log(Level.DEBUG, " - Patched Patched refreshResources 2/3");
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}

	private byte[] patchDummyClass(byte[] basicClass)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		logger.log(Level.INFO, "Found Dummy Class: " + classNode.name);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
}
