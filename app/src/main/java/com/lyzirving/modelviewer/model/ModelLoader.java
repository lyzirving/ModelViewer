package com.lyzirving.modelviewer.model;

import android.content.res.Resources;

import com.lyzirving.modelviewer.model.data.MtlInfo;
import com.lyzirving.modelviewer.model.data.Obj3d;
import com.lyzirving.modelviewer.model.data.ObjGroup;
import com.lyzirving.modelviewer.util.AppContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ModelLoader {

    //key for obj
    private final String VERTEX = "v";
    private final String FACE = "f";
    private final String TEXCOORD = "vt";
    private final String MTL_LIB = "mtllib";
    private final String USE_MTL = "usemtl";

    //key for mtllib
    private final String NEW_MTL = "newmtl";
    private final String NS = "Ns";
    private final String D = "d";
    private final String ILLUM = "illum";
    private final String KD = "Kd";
    private final String KS = "Ks";
    private final String KA = "Ka";
    private final String MAP_KD = "map_Kd";

    private static class ModelLoaderWrapper {
        private static ModelLoader mInstance = new ModelLoader();
    }

    private Resources mResources;

    public static ModelLoader get() {
        return ModelLoaderWrapper.mInstance;
    }

    public void setResources(Resources res) {
        mResources = res;
    }

    /**
     * @param path, the path of file in the assets must contain the type like "xxx.obj"
     * @return
     */
    public Obj3d loadFromAssets(String path) {
        BufferedReader buffer = null;
        try {
            InputStream fileIn = mResources.getAssets().open(path);
            buffer = new BufferedReader(new InputStreamReader(fileIn));
            List<Float> vertex = new ArrayList();
            List<Float> texCoords = new ArrayList();
            List<MtlInfo> mtlInfos = new ArrayList<>();
            List<ObjGroup> groupList = new ArrayList<>();

            String line;
            float maxVertexVal = 1;
            float tmp1, tmp2, tmp3;
            ObjGroup group = null;

            while ((line = buffer.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '#')
                    continue;
                StringTokenizer parts = new StringTokenizer(line, " ");

                int numTokens = parts.countTokens();
                if (numTokens == 0)
                    continue;

                String type = parts.nextToken();
                switch (type) {
                    case MTL_LIB: {
                        mtlInfos = parseMtllib(parts.nextToken().split(".mtl")[0]);
                        break;
                    }
                    case VERTEX: {
                        tmp1 = Float.parseFloat(parts.nextToken());
                        tmp2 = Float.parseFloat(parts.nextToken());
                        tmp3 = Float.parseFloat(parts.nextToken());
                        vertex.add(tmp1);
                        vertex.add(tmp2);
                        vertex.add(tmp3);
                        maxVertexVal = Math.max(Math.max(Math.abs(tmp1), Math.abs(tmp2)),
                                Math.abs(tmp3));
                        break;
                    }
                    case TEXCOORD: {
                        texCoords.add(Float.parseFloat(parts.nextToken()));
                        texCoords.add(Float.parseFloat(parts.nextToken()));
                        break;
                    }
                    case USE_MTL: {
                        group = new ObjGroup();
                        group.setMtlName(parts.nextToken());
                        groupList.add(group);
                        break;
                    }
                    case FACE: {
                        if (group == null)
                            break;
                        StringTokenizer subParts = new StringTokenizer(parts.nextToken(), "/");
                        int idx;
                        for (int i = 1; i < numTokens; i++) {
                            //parse vertex index for face
                            if (i > 1)
                                subParts = new StringTokenizer(parts.nextToken(), "/");
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0)
                                idx = (vertex.size() / 3) + idx;
                            else
                                idx -= 1;
                            group.addVertexIndex(idx);

                            //parse textureCoord index for face
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0)
                                idx = (texCoords.size() / 2) + idx;
                            else
                                idx -= 1;
                            group.addTexCoordIndex(idx);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

            int vertexIndCount, texCoordIndCount, index;
            float[] tmpVertex, tmpTexCoord;
            Obj3d obj3d = new Obj3d();

            //map index
            for (ObjGroup tmp : groupList) {
                vertexIndCount = tmp.getVertexIndex().size();
                texCoordIndCount = tmp.getTexCoordIndex().size();
                tmpVertex = new float[vertexIndCount * 3];
                tmpTexCoord = new float[texCoordIndCount * 2];

                for (int i = 0; i < vertexIndCount; i++) {
                    index = tmp.getVertexIndex().get(i);
                    //we must normalize the vertex coord
                    tmpVertex[i * 3] = vertex.get(index * 3) / maxVertexVal;
                    tmpVertex[i * 3 + 1] = vertex.get(index * 3 + 1) / maxVertexVal;
                    tmpVertex[i * 3 + 2] = vertex.get(index * 3 + 2) / maxVertexVal;
                }
                tmp.setVertex(tmpVertex);

                for (int i = 0; i < texCoordIndCount; i++) {
                    index = tmp.getTexCoordIndex().get(i);
                    tmpTexCoord[i * 2] = texCoords.get(index * 2);
                    tmpTexCoord[i * 2 + 1] = texCoords.get(index * 2 + 1);
                }
                tmp.setTexCoord(tmpTexCoord);

                for (MtlInfo info : mtlInfos) {
                    if (info.getName() != null && tmp.getMtlName() != null
                            && info.getName().equals(tmp.getMtlName())) {
                        tmp.setMtlInfo(info);
                    }
                }
                obj3d.addGroup(tmp);
            }

            return obj3d;

        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (buffer != null)
                    buffer.close();
            } catch (IOException e) {}
        }
    }

    private List<MtlInfo> parseMtllib(String name) {
        InputStream mtllib = mResources.openRawResource(AppContext.get().getIdFromName(name, "raw"));
        BufferedReader mtlReader = new BufferedReader(new InputStreamReader(mtllib));
        String line;
        MtlInfo info = null;
        float[] tmpVal = new float[3];
        List<MtlInfo> result = new ArrayList<>();
        try {
            while ((line = mtlReader.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '#')
                    continue;
                line = line.trim();//remove the blank in the head and tail
                StringTokenizer parts = new StringTokenizer(line, " ");
                int numTokens = parts.countTokens();
                if (numTokens == 0)
                    continue;
                String type = parts.nextToken();
                switch (type) {
                    case NEW_MTL: {
                        info = new MtlInfo();
                        info.setName(parts.nextToken());
                        break;
                    }
                    case NS: {
                        info.setD(Integer.parseInt(parts.nextToken()));
                        break;
                    }
                    case D: {
                        info.setD(Integer.parseInt(parts.nextToken()));
                        break;
                    }
                    case ILLUM: {
                        info.setIllum(Integer.parseInt(parts.nextToken()));
                        break;
                    }
                    case KD: {
                        tmpVal[0] = Float.parseFloat(parts.nextToken());
                        tmpVal[1] = Float.parseFloat(parts.nextToken());
                        tmpVal[2] = Float.parseFloat(parts.nextToken());
                        float[] val = new float[3];
                        System.arraycopy(tmpVal, 0, val, 0, 3);
                        info.setKd(val);
                        break;
                    }
                    case KS: {
                        tmpVal[0] = Float.parseFloat(parts.nextToken());
                        tmpVal[1] = Float.parseFloat(parts.nextToken());
                        tmpVal[2] = Float.parseFloat(parts.nextToken());
                        float[] val = new float[3];
                        System.arraycopy(tmpVal, 0, val, 0, 3);
                        info.setKs(val);
                        break;
                    }
                    case KA: {
                        tmpVal[0] = Float.parseFloat(parts.nextToken());
                        tmpVal[1] = Float.parseFloat(parts.nextToken());
                        tmpVal[2] = Float.parseFloat(parts.nextToken());
                        float[] val = new float[3];
                        System.arraycopy(tmpVal, 0, val, 0, 3);
                        info.setKa(val);
                        break;
                    }
                    case MAP_KD: {
                        info.setMapKd(parts.nextToken());
                        result.add(info);//the last attribute in the file
                        break;
                    }
                    default: {
                        break;
                    }
                }

            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                mtlReader.close();
            } catch (IOException iOException) {}
        }
        return result;
    }

}
