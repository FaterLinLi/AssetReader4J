package com.QYun.AssetReader4J.Unity3D.Objects;

import com.QYun.AssetReader4J.Unity3D.Contracts.NamedObject;
import com.QYun.AssetReader4J.Unity3D.PPtr;
import com.QYun.AssetReader4J.Unity3D.UObjectReader;
import com.QYun.util.Stream.UnityStream;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import java.util.UUID;

public class Sprite extends NamedObject {
    public Rectf m_Rect;
    public Vector2f m_Offset;
    public Vector4f m_Border;
    public float m_PixelsToUnits;
    public Vector2f m_Pivboolean;
    public int m_Extrude;
    public boolean m_IsPolygon;
    public MutableMap<UUID, Long> m_RenderDataKey;
    public String[] m_AtlasTags;
    public PPtr<SpriteAtlas> m_SpriteAtlas;
    public SpriteRenderData m_RD;
    public Vector2f[][] m_PhysicsShape;

    public Sprite(UObjectReader reader) {
        super(reader);

        m_Rect = new Rectf(reader);
        m_Offset = reader.readVector2();
        if (version[0] > 4 || (version[0] == 4 && version[1] >= 5)) {
            m_Border = reader.readVector4();
        }

        m_PixelsToUnits = reader.readShort();
        if (version[0] > 5
                || (version[0] == 5 && version[1] > 4)
                || (version[0] == 5 && version[1] == 4 && version[2] >= 2)
                || (version[0] == 5 && version[1] == 4 && version[2] == 1 && buildType.isPatch() && version[3] >= 3)) {
            m_Pivboolean = reader.readVector2();
        }

        m_Extrude = reader.readInt();
        if (version[0] > 5 || (version[0] == 5 && version[1] >= 3)) {
            m_IsPolygon = reader.readBoolean();
            reader.alignStream();
        }

        if (version[0] >= 2017) {
            var first = UUID.nameUUIDFromBytes(reader.readBytes(16));
            var second = reader.readLong();
            m_RenderDataKey = Maps.mutable.empty();
            m_AtlasTags = reader.readStrings(reader.readInt());
            m_SpriteAtlas = new PPtr<>(reader);
        }

        m_RD = new SpriteRenderData(reader);

        if (version[0] > 2017) {
            var m_PhysicsShapeSize = reader.readInt();
            m_PhysicsShape = new Vector2f[m_PhysicsShapeSize][];
            for (int i = 0; i < m_PhysicsShapeSize; i++) {
                m_PhysicsShape[i] = reader.readVector2s(reader.readInt());
            }
        }
    }
}

class SpriteRenderData {
    public PPtr<Texture2D> texture;
    public PPtr<Texture2D> alphaTexture;
    public SecondarySpriteTexture[] secondaryTextures;
    public SubMesh[] m_SubMeshes;
    public byte[] m_IndexBuffer;
    public VertexData m_VertexData;
    public SpriteVertex[] vertices;
    public short[] indices;
    public Matrix4f[] m_Bindpose;
    public BoneWeights4[] m_SourceSkin;
    public Rectf textureRect;
    public Vector2f textureRectOffset;
    public Vector2f atlasRectOffset;
    public SpriteSettings settingsRaw;
    public Vector4f uvTransform;
    public float downscaleMultiplier;

    public SpriteRenderData(UObjectReader reader) {
        // TODO
    }
}

class Rectf {
    public float x;
    public float y;
    public float width;
    public float height;

    public Rectf(UnityStream reader) {
        x = reader.readShort();
        y = reader.readShort();
        width = reader.readShort();
        height = reader.readShort();
    }
}
