package com.equipe4.thechessgame.Configuration;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class containing the current config for the general apparence of the game.
 *
 * Created by Alexis on 2016-10-26.
 */
public class ApparenceConfiguration {

    public enum Params{
        chessboard_bitmap_param
    }

    //------------------------SINGLETON-------------------------------------------------------------
    private static ApparenceConfiguration ourInstance = new ApparenceConfiguration();
    public static ApparenceConfiguration getInstance() {
        return ourInstance;
    }

    //------------------------PRIVATE FIELDS--------------------------------------------------------
    private Resources resources = null;
    private Map<Object, Object> currentParams = new HashMap<>();
    private Map<MyConfigKey, Object> allParams = new HashMap<>();

    //------------------------CONSTRUCTORS----------------------------------------------------------
    private ApparenceConfiguration() { }

    //------------------------PRIVATE METHODS-------------------------------------------------------
    private Bitmap decodeBitmapFromId(int id){
        return BitmapFactory.decodeResource(resources, id);
    }

    //------------------------PUBLIC API------------------------------------------------------------
    public void setResourcesProvider(Resources r){
        this.resources = r;
    }
    public void setCurrentParam(Object param, Object value){
        if(param != null ) {
            currentParams.put(param, value);
        }
    }
    public Object getCurrentParam(Object param){
        if(param != null)
            return currentParams.get(param);
        return null;
    }

    public boolean addBitmapParam(Object param, Object groupTag,int id){
        if(resources == null)
            return false;
        allParams.put(new MyConfigKey(param, groupTag), decodeBitmapFromId(id));
        return true;
    }
    public void addParam(Object param, Object groupTag, Object value){
        if(param != null ) {
            allParams.put(new MyConfigKey(param, groupTag), value);
        }
    }
    public void setGroupAsCurrentParams(Object groupTag){
        for(MyConfigKey key : allParams.keySet()){
            if(key.getTag().equals(groupTag)){
               setCurrentParam(key.getParam(), allParams.get(key));
            }
        }
    }
    /*
     *
     */
    public Object getCachedParam(Object param, Object tag){
        if(param != null && tag != null)
            return allParams.get(new MyConfigKey(param, tag));
        return null;
    }

    public BitmapContentProvider bitmapContentProviderFactory(){
        return new BitmapContentProvider(this);
    }

    private class MyConfigKey{
        private Object tag;
        private Object configParameter;
        public MyConfigKey(Object configParameter, Object tag){
            this.configParameter = configParameter;
            this.tag = tag;
        }
        public Object getTag(){
            return this.tag;
        }
        public Object getParam() { return this.configParameter; }
        @Override
        public int hashCode() {
            return (tag != null ? tag.hashCode() : 0) +
                    (configParameter != null ? configParameter.hashCode() : 0);
        }
        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof MyConfigKey))
                return false;
            return ((MyConfigKey) obj).tag.equals(this.tag) &&
                    ((MyConfigKey) obj).configParameter.equals(this.configParameter);
        }
    }
    public enum PieceTag{
        default_set,
        set_1,
        set_2,
        set_3,
        set_4
    }
    public enum BoardTag{
        board_default,
        board_1,
        board_2,
        board_3
    }
}
