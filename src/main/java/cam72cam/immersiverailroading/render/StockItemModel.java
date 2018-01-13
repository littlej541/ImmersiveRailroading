package cam72cam.immersiverailroading.render;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cam72cam.immersiverailroading.items.nbt.ItemDefinition;
import cam72cam.immersiverailroading.items.nbt.ItemGauge;
import cam72cam.immersiverailroading.util.GLBoolTracker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class StockItemModel implements IBakedModel {
	private OBJRender model;
	private double scale;

	public StockItemModel() {
	}
	
	public StockItemModel(ItemStack stack) {
		scale = ItemGauge.get(stack).scale();
		String defID = ItemDefinition.getID(stack);
		model = StockRenderCache.getRender(defID);
		if (model == null) {
			stack.setCount(0);
		}
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		/*
		 * I am an evil wizard!
		 * 
		 * So it turns out that I can stick a draw call in here to
		 * render my own stuff. This subverts forge's entire baked model
		 * system with a single line of code and injects my own OpenGL
		 * payload. Fuck you modeling restrictions.
		 * 
		 * This is probably really fragile if someone calls getQuads
		 * before actually setting up the correct GL context.
		 */
		if (model != null) {
			GLBoolTracker tex = new GLBoolTracker(GL11.GL_TEXTURE_2D, model.hasTexture());
			GLBoolTracker cull = new GLBoolTracker(GL11.GL_CULL_FACE, false);
			
			GL11.glPushMatrix();
			double scale = 0.2 * Math.sqrt(this.scale);
			GL11.glScaled(scale, scale, scale);
			model.bindTexture();
			model.draw();
			model.restoreTexture();
			GL11.glPopMatrix();
			
			tex.restore();
			cull.restore();
			
			// Model can only be rendered once.  If mods go through the itemrenderer as they are supposed to this should work just fine
			model = null;
		}
		return new ArrayList<BakedQuad>();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	public class ItemOverrideListHack extends ItemOverrideList {
		public ItemOverrideListHack() {
			super(new ArrayList<ItemOverride>());
		}

		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			return new StockItemModel(stack);
		}
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideListHack();
	}
/*
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> defaultVal = ForgeHooksClient.handlePerspective(this, cameraTransformType);
		switch (cameraTransformType) {
		case THIRD_PERSON_LEFT_HAND:
		case THIRD_PERSON_RIGHT_HAND:
			return Pair.of(defaultVal.getLeft(),
					new Matrix4().rotate(Math.toRadians(60), 1, 0, 0).rotate(Math.toRadians(-90), 0, 1, 0).toMatrix4f());
		case FIRST_PERSON_LEFT_HAND:
		case FIRST_PERSON_RIGHT_HAND:
			return Pair.of(defaultVal.getLeft(),
					new Matrix4().rotate(Math.toRadians(10), 1, 0, 0).rotate(Math.toRadians(-90), 0, 1, 0).toMatrix4f());
		case GROUND:
			return Pair.of(defaultVal.getLeft(), new Matrix4().rotate(Math.toRadians(-90), 0, 1, 0).toMatrix4f());
		case FIXED:
			// Item Frame
			return Pair.of(defaultVal.getLeft(), new Matrix4().rotate(Math.toRadians(-90), 0, 1, 0).toMatrix4f());
		case GUI:
			return Pair.of(defaultVal.getLeft(), new Matrix4().translate(0.5, 0, 0).rotate(Math.toRadians(+5+90), 0, 1, 0).toMatrix4f());
		case HEAD:
			return Pair.of(defaultVal.getLeft(),
					new Matrix4().translate(0, 0, 0.5).rotate(Math.toRadians(-90), 0, 1, 0).toMatrix4f());
		case NONE:
			return defaultVal;
		}
		return defaultVal;
	}
	*/
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return new ItemCameraTransforms(ItemCameraTransforms.DEFAULT) {
			public ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
				switch (type) {
				case THIRD_PERSON_LEFT_HAND:
				case THIRD_PERSON_RIGHT_HAND:
					return new ItemTransformVec3f(new Vector3f(60, -90, 0), new Vector3f(0f,0f,0f), new Vector3f(1f, 1f, 1f));
				case FIRST_PERSON_LEFT_HAND:
				case FIRST_PERSON_RIGHT_HAND:
					return new ItemTransformVec3f(new Vector3f(10, -90, 0), new Vector3f(0f,0f,0f), new Vector3f(1f, 1f, 1f));
				case GROUND:
					return new ItemTransformVec3f(new Vector3f(0, -90, 0), new Vector3f(0f,0f,0f), new Vector3f(1f, 1f, 1f));
				case FIXED:
					// Item Frame
					return new ItemTransformVec3f(new Vector3f(0, -90, 0), new Vector3f(0f,0f,0f), new Vector3f(1f, 1f, 1f));
				case GUI:
					return new ItemTransformVec3f(new Vector3f(0, 95, 0), new Vector3f(0.5f,0f,0f), new Vector3f(1f, 1f, 1f));
				case HEAD:
					return new ItemTransformVec3f(new Vector3f(0, -90, 0), new Vector3f(0f, 0f, 0.5f), new Vector3f(1f, 1f, 1f));
				case NONE:
					break;
				default:
					break;
				}
				
				return super.getTransform(type);
			}
		};
	}
}
