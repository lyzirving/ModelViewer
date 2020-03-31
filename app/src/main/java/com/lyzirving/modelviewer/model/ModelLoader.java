package com.lyzirving.modelviewer.model;

import android.content.res.Resources;

import com.lyzirving.modelviewer.model.data.Obj3d;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ModelLoader {

    private final String VERTEX = "v";
    private final String FACE = "f";
    private final String TEXCOORD = "vt";
    private final String TEXNAME = "tn";

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

    public Obj3d loadFromAssets(int id) {
        InputStream fileIn = mResources.openRawResource(id);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(fileIn));

        String line;
        String texName = null;
        List<Integer> vertexIndex = new ArrayList<>();
        List<Integer> texIndex = new ArrayList<>();
        List<Float> vertex = new ArrayList();
        List<Float> texCoords = new ArrayList();

        float maxVertexVal = 1;
        float tmp1, tmp2, tmp3;

        try {
            while ((line = buffer.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '#')
                    continue;
                StringTokenizer parts = new StringTokenizer(line, " ");

                int numTokens = parts.countTokens();
                if (numTokens == 0)
                    continue;

                String type = parts.nextToken();
                switch (type) {
                    case TEXNAME: {
                        texName = new String(parts.nextToken());
                        break;
                    }
                    case VERTEX: {
                        tmp1 = Float.parseFloat(parts.nextToken());
                        vertex.add(tmp1);
                        tmp2 = Float.parseFloat(parts.nextToken());
                        vertex.add(tmp2);
                        tmp3 = Float.parseFloat(parts.nextToken());
                        vertex.add(tmp3);
                        maxVertexVal = Math.max(Math.max(Math.abs(tmp1), Math.abs(tmp2)),
                                Math.abs(tmp3));
                        break;
                    }
                    case TEXCOORD: {
                        texCoords.add(Float.parseFloat(parts.nextToken()));
                        texCoords.add(1f - Float.parseFloat(parts.nextToken()));
                        break;
                    }
                    case FACE: {
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
                            vertexIndex.add(idx);

                            //parse textureCoord index for face
                            idx = Integer.parseInt(subParts.nextToken());
                            if(idx < 0)
                                idx = (texCoords.size() / 2) + idx;
                            else
                                idx -= 1;
                            texIndex.add(idx);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        float[] finalVertex = new float[vertexIndex.size() * 3];
        float[] finalTexCoords = new float[texIndex.size() * 2];

        int index;
        for (int i = 0; i < vertexIndex.size(); i++) {
            index = vertexIndex.get(i);
            //we must normalize the vertex coord
            finalVertex[i * 3] = vertex.get(index * 3) / maxVertexVal;
            finalVertex[i * 3 + 1] = vertex.get(index * 3 + 1) / maxVertexVal;
            finalVertex[i * 3 + 2] = vertex.get(index * 3 + 2) / maxVertexVal;
        }

        for (int i = 0; i < texIndex.size(); i++) {
            index = texIndex.get(i);
            finalTexCoords[i * 2] = texCoords.get(index * 2);
            finalTexCoords[i * 2 + 1] = texCoords.get(index * 2 + 1);
        }

        Obj3d obj3d = new Obj3d();
        obj3d.setTextureName(texName);
        obj3d.setVertex(finalVertex);
        obj3d.setTextureCoord(finalTexCoords);

        return obj3d;
    }

}
