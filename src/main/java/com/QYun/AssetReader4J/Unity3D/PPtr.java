package com.QYun.AssetReader4J.Unity3D;

import com.QYun.AssetReader4J.Entities.Enums;
import com.QYun.AssetReader4J.Entities.Struct.FileIdentifier;
import com.QYun.AssetReader4J.SerializedFile;

public class PPtr<T extends UObject> {
    private final SerializedFile assetsFile;
    private final int index = -2; //-2 - Prepare, -1 - Missing
    public int m_FileID;
    public long m_PathID;

    public PPtr(UObjectReader reader) {
        m_FileID = reader.readInt();
        m_PathID = reader.m_Version.ordinal() < Enums.SerializedFileFormatVersion.kUnknown_14.ordinal() ? reader.readInt() : reader.readLong();
        assetsFile = reader.assetsFile;
    }

    public boolean isNull() {
        return m_PathID == 0 || m_FileID < 0;
    }

    private SerializedFile tryGetAssetsFile() {
        if (m_FileID == 0)
            return assetsFile;

        if (m_FileID > 0 && m_FileID - 1 < assetsFile.m_Externals.size()) {
            var assetsManager = assetsFile.assetsManager;
            var assetsFileList = assetsManager.assetsFileList;
            var assetsFileIndexCache = assetsManager.assetsFileIndexCache;

            if (index == -2) {
                var m_External = assetsFile.m_Externals.get(m_FileID - 1);
                var name = m_External.fileName;
                var index = assetsFileIndexCache.get(name);
                if (index == null) {
                    index = assetsFileList.detectIndex(SerializedFile -> SerializedFile.file.getName().equalsIgnoreCase(name));
                    assetsFileIndexCache.put(name, index);
                }
            }

            if (index >= 0)
                return assetsFileList.get(index);
        }
        return null;
    }

    public T tryGet() {
        var sourceFile = tryGetAssetsFile();
        T result = null;

        if (sourceFile != null) {
            UObject obj = sourceFile.ObjectsDic.get(m_PathID);
            if (sourceFile.ObjectsDic.get(m_PathID) != null) {
                try {
                    result = (T) obj;
                } catch (ClassCastException ignored) {
                }
            }
        }
        return result;
    }

    public <T2 extends UObject> T2 tryGet(Class<T2> type) {
        var sourceFile = tryGetAssetsFile();
        T2 result = null;

        if (sourceFile != null) {
            UObject obj = sourceFile.ObjectsDic.get(m_PathID);
            if (sourceFile.ObjectsDic.get(m_PathID) != null) {
                try {
                    result = type.cast(obj);
                } catch (ClassCastException ignored) {
                }
            }
        }

        return result;
    }

    public void set(T m_Object) {
        var name = m_Object.assetsFile.file.getName();
        if (name.equalsIgnoreCase(assetsFile.file.getName())) {
            m_FileID = 0;
        } else {
            m_FileID = assetsFile.m_Externals.detectIndex(FileIdentifier -> FileIdentifier.fileName.equalsIgnoreCase(name));
            if (m_FileID == -1) {
                assetsFile.m_Externals.add(new FileIdentifier() {{
                    fileName = m_Object.assetsFile.file.getName();
                }});
                m_FileID = assetsFile.m_Externals.size();
            } else m_FileID += 1;
        }

        var assetsManager = assetsFile.assetsManager;
        var assetsFileList = assetsManager.assetsFileList;
        var assetsFileIndexCache = assetsManager.assetsFileIndexCache;
        var index = assetsFileIndexCache.get(name);

        if (index == null) {
            index = assetsFileList.detectIndex(SerializedFile -> SerializedFile.file.getName().equalsIgnoreCase(name));
            assetsFileIndexCache.put(name, index);
        }

        m_PathID = m_Object.m_PathID;
    }
}
